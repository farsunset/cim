/**
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim.sdk.android;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.farsunset.cim.sdk.android.coder.CIMLogger;
import com.farsunset.cim.sdk.android.coder.ClientMessageDecoder;
import com.farsunset.cim.sdk.android.coder.ClientMessageEncoder;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.exception.SessionClosedException;
import com.farsunset.cim.sdk.android.model.HeartbeatRequest;
import com.farsunset.cim.sdk.android.model.HeartbeatResponse;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.ArrayMap;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 * @author 3979434@qq.com
 */
class CIMConnectorManager {

	private final int READ_BUFFER_SIZE = 2048;
	private final int WRITE_BUFFER_SIZE = 1024;

	private final int READ_IDLE_TIME = 120 * 1000;// 秒
	private final int HEARBEAT_TIME_OUT = (READ_IDLE_TIME + 10) * 1000;// 收到服务端心跳请求超时时间 毫秒
	
	private final String KEY_LAST_HEART_TIME = "KEY_LAST_HEART_TIME";

	private Selector selector;
	private SocketChannel socketChannel ;
	private Context context;
	private static CIMConnectorManager manager;
	
	private CIMLogger logger = CIMLogger.getLogger();

	
	
    private  ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
    
    private ArrayMap<String, Object> attr = new ArrayMap<>();
    
	private ExecutorService workerExecutor = Executors.newFixedThreadPool(1);
	private ExecutorService bossExecutor = Executors.newFixedThreadPool(1);

	private final AtomicBoolean CONNECTING_FLAG = new AtomicBoolean(false) ;
	
	private ClientMessageEncoder messageEncoder = new  ClientMessageEncoder();
	private ClientMessageDecoder messageDecoder = new  ClientMessageDecoder();

	
	private CIMConnectorManager(Context ctx) {
		context = ctx;
		makeNioConnector();
	}
	
	private void makeNioConnector() {
		try {
			 if(socketChannel == null || !socketChannel.isOpen()) {
				 socketChannel = SocketChannel.open();
		         socketChannel.configureBlocking(false);
	             socketChannel.setOption(StandardSocketOptions.SO_RCVBUF,READ_BUFFER_SIZE);
	             socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, WRITE_BUFFER_SIZE);
	             socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
	             socketChannel.setOption(StandardSocketOptions.TCP_NODELAY,true);
			 }
			
             if(selector == null || !selector.isOpen()) {
            	 selector = Selector.open();
             }
             
             selector.wakeup(); 
		     socketChannel.register(selector, SelectionKey.OP_CONNECT);

		}catch(Exception e) {
			
		}
	 
	}
	
 
	
	public synchronized static CIMConnectorManager getManager(Context context) {
		
		if (manager == null) {
			manager = new CIMConnectorManager(context);
		}
		
		return manager;

	}
 
	

	public void connect(final String host, final int port) {

		if (!isNetworkConnected(context)) {

			Intent intent = new Intent();
			intent.setPackage(context.getPackageName());
			intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_FAILED);
			context.sendBroadcast(intent);

			return;
		}
		 
		boolean isConnected = isConnected();
		if (CONNECTING_FLAG.get() || isConnected) {
			return;
		}
		
		CONNECTING_FLAG.set(true);
		
		if(!socketChannel.isOpen() ||!selector.isOpen()) {
			makeNioConnector();
		} 
		
		bossExecutor.execute(new Runnable() {
			@Override
			public void run() {
				
				
				final InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
				
	            logger.startConnect(remoteAddress);
				
				CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE, false);
				
 				try {
					socketChannel.connect(remoteAddress);
				}catch(Exception e){
					closeSession();
					return;
				}
				

				workerExecutor.execute(new Runnable() {
					@Override
					public void run() {
						while (socketChannel.isOpen()) {
							
			                try {
								selector.select();
								for(SelectionKey key : selector.selectedKeys()){
									if (key.isConnectable() && socketChannel.finishConnect()) {
				                		handelConnectionEvent();
				                		continue;
				                	}
				                	 
				                	if (key.isReadable()) {
				                		handelReadEvent();
				                	}
								}
				                
							} catch (Exception e) {
								if(e instanceof ConnectException) {
									handleConnectFailure(remoteAddress);
								}else {
									closeSession();
								}
							}
			            }
					}
				});
			
			}
		});
	}
	
	
	Handler idleHandler = new Handler() {
		public void handleMessage(android.os.Message m) {
			sessionIdle();
		}
	};
	 
	private void handelConnectionEvent() throws Exception {
		CONNECTING_FLAG.set(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
		sessionCreated();
		
		idleHandler.sendEmptyMessageDelayed(0, READ_IDLE_TIME);
	}
	
	private void handelReadEvent() throws Exception {
		
	    idleHandler.removeCallbacksAndMessages(null);
		
		idleHandler.sendEmptyMessageDelayed(0, READ_IDLE_TIME);
		
	    
		int result = 0;
		
		while((result = socketChannel.read(readBuffer)) > 0) {
			if(readBuffer.position() == readBuffer.capacity()) {
				extemdByteBuffer();
			}
		}
		
	    if(result == -1) {
	    	closeSession();
	    	return;
	    }
		
	    readBuffer.position(0);
	     
		Object message = messageDecoder.doDecode(readBuffer);
		
		if(message == null) {
			return;
		}
		
		
		logger.messageReceived(socketChannel,message);

		if(isRequest(message)) {
			send(getResponse());
			return;
		}
		
		this.messageReceived(message);
	}
	
	
	private void extemdByteBuffer() {
		
		ByteBuffer newBuffer = ByteBuffer.allocate(readBuffer.capacity() + READ_BUFFER_SIZE / 2);
		readBuffer.position(0);
		newBuffer.put(readBuffer);
		
		readBuffer.clear();
		readBuffer = newBuffer;
	}
    
	private void handleConnectFailure(InetSocketAddress remoteAddress) {
		
		CONNECTING_FLAG.set(false);

		long interval = CIMConstant.RECONN_INTERVAL_TIME - (5 * 1000 - new Random().nextInt(15 * 1000));
		
		logger.connectFailure(remoteAddress, interval);

		
		Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_FAILED);
		intent.putExtra("interval", interval);
		context.sendBroadcast(intent);


	}
	
	public void send(final SentBody body) {
		
		bossExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				boolean isSuccessed = false;
				String exceptionName = SessionClosedException.class.getSimpleName();
				if (isConnected()) {
					
					try {
						ByteBuffer buffer =  messageEncoder.encode(body);
						int result = 0;
						while(buffer.hasRemaining()){
							result += socketChannel.write(buffer);   
						}
						isSuccessed = result > 0;
					} catch (IOException e) {
						exceptionName = e.getClass().getSimpleName();
						closeSession();
					}
					 
				}
				
				if (!isSuccessed) {
					Intent intent = new Intent();
					intent.setPackage(context.getPackageName());
					intent.setAction(CIMConstant.IntentAction.ACTION_SENT_FAILED);
					intent.putExtra(Exception.class.getName(), exceptionName);
					intent.putExtra(SentBody.class.getName(), body);
					context.sendBroadcast(intent);
				}else {
					messageSent(body);
				}
			}
		});
		
	}
	
	
   public void send(final HeartbeatResponse body) {
		
		bossExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					 socketChannel.write(messageEncoder.encode(body));
					 messageSent(body);
				} catch (IOException e) {
					 closeSession();
				}
			}
		});
		
	}


	public void sessionCreated() throws Exception {

		logger.sessionCreated(socketChannel);
		
		setLastHeartbeatTime();

		Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_SUCCESSED);
		context.sendBroadcast(intent);

	}

	public void sessionClosed() {

		readBuffer.clear();
		
		if(readBuffer.capacity() > READ_BUFFER_SIZE) {
			readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
		}
		
		attr.clear();
		
		logger.sessionClosed(socketChannel);

		closeSelector();
		
		Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_CLOSED);
		context.sendBroadcast(intent);
		
	}

	public void sessionIdle() {

		logger.sessionIdle(socketChannel);

		/**
		 * 用于解决，wifi情况下。偶而路由器与服务器断开连接时，客户端并没及时收到关闭事件 导致这样的情况下当前连接无效也不会重连的问题
		 * 
		 */
		long lastHeartbeatTime = getLastHeartbeatTime();
		if (System.currentTimeMillis() - lastHeartbeatTime >= HEARBEAT_TIME_OUT) {
			closeSession();
		}
	}


	public void messageReceived(Object obj) {

		if (obj instanceof Message) {

			Intent intent = new Intent();
			intent.setPackage(context.getPackageName());
			intent.setAction(CIMConstant.IntentAction.ACTION_MESSAGE_RECEIVED);
			intent.putExtra(Message.class.getName(), (Message) obj);
			context.sendBroadcast(intent);

		}
		if (obj instanceof ReplyBody) {

			Intent intent = new Intent();
			intent.setPackage(context.getPackageName());
			intent.setAction(CIMConstant.IntentAction.ACTION_REPLY_RECEIVED);
			intent.putExtra(ReplyBody.class.getName(), (ReplyBody) obj);
			context.sendBroadcast(intent);
		}
	}

	
	public void messageSent(Object message) {
		
		logger.messageSent(socketChannel, message);
		
		if (message instanceof SentBody) {
			Intent intent = new Intent();
			intent.setPackage(context.getPackageName());
			intent.setAction(CIMConstant.IntentAction.ACTION_SENT_SUCCESSED);
			intent.putExtra(SentBody.class.getName(), (SentBody) message);
			context.sendBroadcast(intent);
		}
	}

	
	private void setLastHeartbeatTime() {
		attr.put(KEY_LAST_HEART_TIME, System.currentTimeMillis());
	}
	

	private long getLastHeartbeatTime() {
		long time = 0;
		Object value = attr.get(KEY_LAST_HEART_TIME);
		if (value != null) {
			time = Long.parseLong(value.toString());
		}
		return time;
	}

	public static boolean isNetworkConnected(Context context) {
		try {
			ConnectivityManager nw = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = nw.getActiveNetworkInfo();
			return networkInfo != null;
		} catch (Exception e) {
		}

		return false;
	}
 

	public HeartbeatResponse getResponse() {
		return HeartbeatResponse.getInstance();
	}

	public boolean isRequest(Object data) {

		setLastHeartbeatTime();

		return data instanceof HeartbeatRequest;
	}
 
    public void destroy() {
		
		closeSession();
		closeSelector();

	}

	public boolean isConnected() {
		return socketChannel != null && socketChannel.isConnected();
	}

	public void closeSession() {
		try {
			socketChannel.close();
		} catch (IOException ignore) {
		}finally {
			 this.sessionClosed();
		}
	}

	public void closeSelector() {
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException ignore) {
			}
		}
	}

}
