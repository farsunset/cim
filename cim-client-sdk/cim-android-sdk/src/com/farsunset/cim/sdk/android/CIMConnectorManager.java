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
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.farsunset.cim.sdk.android.coder.CIMLogger;
import com.farsunset.cim.sdk.android.coder.ClientMessageDecoder;
import com.farsunset.cim.sdk.android.coder.ClientMessageEncoder;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.HeartbeatRequest;
import com.farsunset.cim.sdk.android.model.HeartbeatResponse;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.Protobufable;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 * @author 3979434@qq.com
 */
class CIMConnectorManager{

	private static CIMConnectorManager manager;
	
	private final int READ_BUFFER_SIZE = 2048;
	private final int WRITE_BUFFER_SIZE = 1024;

	private final int READ_IDLE_TIME = 120 * 1000;
	
	private final int CONNECT_ALIVE_TIME_OUT = 150 * 1000;
	
    private final AtomicLong LAST_READ_TIME = new AtomicLong(0);

    private final AtomicBoolean CONNECTING_FLAG = new AtomicBoolean(false) ;
    
    private final CIMLogger LOGGER = CIMLogger.getLogger();
    
    private static final HandlerThread IDLE_HANDLER_THREAD = new HandlerThread("READ-IDLE", Process.THREAD_PRIORITY_BACKGROUND);
    
    private LinkedBlockingQueue<Protobufable> sendFailBodyQueue = new LinkedBlockingQueue<Protobufable>();
    private final ReentrantLock IOLOCK = new ReentrantLock(); 
    
	private Selector selector;
	private SocketChannel socketChannel ;
	private Context context;
	
	
    private ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
    
	private ExecutorService workerExecutor = Executors.newFixedThreadPool(1);
	private ExecutorService bossExecutor = Executors.newFixedThreadPool(1);

	
	private ClientMessageEncoder messageEncoder = new  ClientMessageEncoder();
	private ClientMessageDecoder messageDecoder = new  ClientMessageDecoder();
    
	static {
		IDLE_HANDLER_THREAD.start();
	}
	private CIMConnectorManager(Context context) {
		this.context = context;
		makeNioConnector();
	}
	
	private void makeNioConnector() {
		try {
			 if(socketChannel == null || !socketChannel.isOpen()) {
				 socketChannel = SocketChannel.open();
		         socketChannel.configureBlocking(false);
	             socketChannel.socket().setTcpNoDelay(true);
	             socketChannel.socket().setKeepAlive(true);
	             socketChannel.socket().setReceiveBufferSize(READ_BUFFER_SIZE);
	             socketChannel.socket().setSendBufferSize(WRITE_BUFFER_SIZE);
			 }
			
             if(selector == null || !selector.isOpen()) {
            	 selector = Selector.open();
             }
             
             selector.wakeup(); 
		     socketChannel.register(selector, SelectionKey.OP_CONNECT);

		}catch(Exception ignore) {}
	 
	}
	
	public synchronized static CIMConnectorManager getManager(Context context) {
		
		if (manager == null) {
			manager = new CIMConnectorManager(context);
		}
		
		return manager;

	}

	public void connect(final String host, final int port) {

		boolean isNetworkConnected = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetwork() != null;

		if (!isNetworkConnected) {

			Intent intent = new Intent();
			intent.setPackage(context.getPackageName());
			intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_FAILED);
			context.sendBroadcast(intent);

			return;
		}
		 
		if (CONNECTING_FLAG.get() || isConnected()) {
			return;
		}
		
		CONNECTING_FLAG.set(true);
		
		if(!socketChannel.isOpen() ||!selector.isOpen()) {
			makeNioConnector();
		} 
		
		workerExecutor.execute(new Runnable() {
			@Override
			public void run() {
				
				IOLOCK.lock();
				
	            LOGGER.startConnect(host, port);
				
				CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE, false);
				
 				try {
 					
 					socketChannel.connect(new InetSocketAddress(host, port));
 					
 					while (socketChannel.isOpen()) {
 						
 						selector.select();
 						
 						if(!selector.isOpen()) {
 							break;
 						}
 						
 						for(SelectionKey key : selector.selectedKeys()){
 							
 							if((key.interestOps() & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT && socketChannel.finishConnect()) {
 								IOLOCK.unlock();
 								handelConnectedEvent();
 		                		continue;
 							}
 							
 							if((key.interestOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
 								handelSocketReadEvent();
 							}
 						}
 		            }
 					
				}catch(ConnectException ignore){
					handleConnectAbortedEvent();
				}catch(IllegalArgumentException ignore){
					handleConnectAbortedEvent();
				}catch(IOException ignore) {
					handelDisconnectedEvent();
				}catch(ClosedSelectorException ignore) {}
			}
		});
	}
 
 
    public void destroy() {
		
		closeSession();
		closeSelector();

	}
	

	public void closeSession() {
		try {
			socketChannel.close();
		} catch (IOException ignore) {
		}finally {
			 this.sessionClosed();
		}
	}

	public boolean isConnected() {
		return socketChannel != null && socketChannel.isConnected();
	}
	
	
	public void send(final Protobufable body) {
		
		bossExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				int result = 0;
				try {
					
					IOLOCK.lock();
					
					ByteBuffer buffer =  messageEncoder.encode(body);
					while(buffer.hasRemaining()){
						result += socketChannel.write(buffer);   
					}
					 
				} catch (IOException e) {
					result = -1;
				}finally {
					
					IOLOCK.unlock();
					
					if(result <= 0) {
						sendFailBodyQueue.offer(body);
						closeSession();
					}else {
						messageSent(body);
					}
				} 
			}
		});
		
	}

   private void sendFaildQueueBody() {
	   Protobufable body = sendFailBodyQueue.poll();
	   if(body == null) {
		   return;
	   }
	   
	   send(body);
	   
	   sendFaildQueueBody();
	   
   }
   private void sessionCreated() {
		LOGGER.sessionCreated(socketChannel);
		
		LAST_READ_TIME.set(System.currentTimeMillis());

		Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_SUCCESSED);
		context.sendBroadcast(intent);
		
		sendFaildQueueBody();

	}

	private void sessionClosed() {

		idleHandler.removeMessages(0);

		LAST_READ_TIME.set(0); 
		
		LOGGER.sessionClosed(socketChannel);

		readBuffer.clear();
		
		if(readBuffer.capacity() > READ_BUFFER_SIZE) {
			readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
		}
		
		closeSelector();
		
		Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_CLOSED);
		context.sendBroadcast(intent);
		
	}

	private void sessionIdle() {

		LOGGER.sessionIdle(socketChannel);

		/**
		 * 用于解决，wifi情况下。偶而路由器与服务器断开连接时，客户端并没及时收到关闭事件 导致这样的情况下当前连接无效也不会重连的问题
		 * 
		 */
		if (System.currentTimeMillis() - LAST_READ_TIME.get() >= CONNECT_ALIVE_TIME_OUT) {
			closeSession();
		}
	}


	private void messageReceived(Object obj) {

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

	
	private void messageSent(Object message) {
		
		LOGGER.messageSent(socketChannel, message);
		
		if (message instanceof SentBody) {
			Intent intent = new Intent();
			intent.setPackage(context.getPackageName());
			intent.setAction(CIMConstant.IntentAction.ACTION_SENT_SUCCESSED);
			intent.putExtra(SentBody.class.getName(), (SentBody) message);
			context.sendBroadcast(intent);
		}
	}

	private Handler idleHandler = new Handler(IDLE_HANDLER_THREAD.getLooper()) {
		public void handleMessage(android.os.Message m) {
			sessionIdle();
		}
	};
	
	private void handelDisconnectedEvent() {
		CONNECTING_FLAG.set(false);
		closeSession();
	}
	
    private void handleConnectAbortedEvent() {
		
		CONNECTING_FLAG.set(false);

		long interval = CIMConstant.RECONN_INTERVAL_TIME - (5 * 1000 - new Random().nextInt(15 * 1000));
		
		LOGGER.connectFailure(interval);
		
		Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_FAILED);
		intent.putExtra("interval", interval);
		context.sendBroadcast(intent);

	}
	 
	private void handelConnectedEvent() throws IOException {
		
		CONNECTING_FLAG.set(false);
 		socketChannel.register(selector, SelectionKey.OP_READ);
		sessionCreated();
		
		idleHandler.sendEmptyMessageDelayed(0, READ_IDLE_TIME);
	}
	
	private void handelSocketReadEvent() throws IOException   {
		
		int result = 0;
		
		while((result = socketChannel.read(readBuffer)) > 0) {
			if(readBuffer.position() == readBuffer.capacity()) {
				extendByteBuffer();
			}
		}
		
	    if(result == -1) {
	    	closeSession();
	    	return;
	    }
	    
	
	    markLastReadTime();
		
		
	    readBuffer.position(0);
	     
		Object message = messageDecoder.doDecode(readBuffer);
		
		if(message == null) {
			return;
		}
		
		
		
		LOGGER.messageReceived(socketChannel,message);

		if(isHeartbeatRequest(message)) {

			send(getHeartbeatResponse());
			
			return;
		}
		
		this.messageReceived(message);
	}
	
	
	private void extendByteBuffer() {
		
		ByteBuffer newBuffer = ByteBuffer.allocate(readBuffer.capacity() + READ_BUFFER_SIZE / 2);
		readBuffer.position(0);
		newBuffer.put(readBuffer);
		
		readBuffer.clear();
		readBuffer = newBuffer;
	}
	
	
    private void markLastReadTime() {

    	LAST_READ_TIME.set(System.currentTimeMillis());
    		
    	idleHandler.removeMessages(0);
    		
    	idleHandler.sendEmptyMessageDelayed(0, READ_IDLE_TIME);
    		
	}
    

    private HeartbeatResponse getHeartbeatResponse() {
		return HeartbeatResponse.getInstance();
	}

    private boolean isHeartbeatRequest(Object data) {
		return data instanceof HeartbeatRequest;
	}
	


	private void closeSelector() {
		try {
			selector.close();
		} catch (IOException ignore) {}
	}

}
