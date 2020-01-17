/*
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

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import com.farsunset.cim.sdk.android.coder.CIMLogger;
import com.farsunset.cim.sdk.android.coder.ClientMessageDecoder;
import com.farsunset.cim.sdk.android.coder.ClientMessageEncoder;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 * @author 3979434@qq.com
 */
class CIMConnectorManager{

	private static CIMConnectorManager manager;
	
	private static final int READ_BUFFER_SIZE = 2048;
	private static final int WRITE_BUFFER_SIZE = 1024;

	private static final int READ_IDLE_TIME = 120 * 1000;
	
	private static final int CONNECT_TIME_OUT = 10 * 1000;

	
	private static final int CONNECT_ALIVE_TIME_OUT = 150 * 1000;
	
    private static final AtomicLong LAST_READ_TIME = new AtomicLong(0);
    
    private static final CIMLogger LOGGER = CIMLogger.getLogger();
    
    private static final HandlerThread IDLE_HANDLER_THREAD = new HandlerThread("READ-IDLE", Process.THREAD_PRIORITY_BACKGROUND);
    
	private Semaphore semaphore = new Semaphore(1, true);
	
	private volatile SocketChannel socketChannel ;
	
	private Context context;
	
	
    private ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
    
	private ExecutorService workerExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r,"worker-"));
	
	private ExecutorService bossExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r,"boss-"));
	
	private ClientMessageEncoder messageEncoder = new  ClientMessageEncoder();
	private ClientMessageDecoder messageDecoder = new  ClientMessageDecoder();
    
	static {
		IDLE_HANDLER_THREAD.start();
	}
	private CIMConnectorManager(Context context) {
		this.context = context;
	}
	 
	
	public synchronized static CIMConnectorManager getManager(Context context) {
		
		if (manager == null) {
			manager = new CIMConnectorManager(context);
		}
		
		return manager;

	}

	public void connect(final String host, final int port) {

		if (!CIMPushManager.isNetworkConnected(context)) {

			Intent intent = new Intent();
			intent.setPackage(context.getPackageName());
			intent.setAction(CIMConstant.IntentAction.ACTION_CONNECT_FAILED);
			context.sendBroadcast(intent);

			return;
		}
		
		if (isConnected()) {
			return;
		}
		
		bossExecutor.execute(() -> {

			if (isConnected()) {
				return;
			}

			LOGGER.startConnect(host, port);

			CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE, false);

			 try {

				 semaphore.acquire();

				 socketChannel = SocketChannel.open();
				 socketChannel.configureBlocking(true);
				 socketChannel.socket().setTcpNoDelay(true);
				 socketChannel.socket().setKeepAlive(true);
				 socketChannel.socket().setReceiveBufferSize(READ_BUFFER_SIZE);
				 socketChannel.socket().setSendBufferSize(WRITE_BUFFER_SIZE);

				 socketChannel.socket().connect(new InetSocketAddress(host, port),CONNECT_TIME_OUT);

				 semaphore.release();

				 handelConnectedEvent();


				 int result = -1;

				while((result = socketChannel.read(readBuffer)) > 0) {

					if(readBuffer.position() == readBuffer.capacity()) {
						extendByteBuffer();
					}

					handelSocketReadEvent(result);

				}

				handelSocketReadEvent(result);

			}catch(ConnectException | SocketTimeoutException ignore){
				semaphore.release();
				handleConnectAbortedEvent();
			} catch(IOException ignore) {
				semaphore.release();
				handelDisconnectedEvent();
			}catch(InterruptedException ignore) {
				semaphore.release();
			}
		});
	}
 
 
    public void destroy() {
		
		closeSession();

	}
	

	public void closeSession() {
		
		if(!isConnected()) {
			return;
		}
		
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
		
		if(!isConnected()) {
			return;
		}
		
		workerExecutor.execute(() -> {
			int result = 0;
			try {

				semaphore.acquire();

				ByteBuffer buffer =  messageEncoder.encode(body);
				while(buffer.hasRemaining()){
					result += socketChannel.write(buffer);
				}

			} catch (Exception e) {
				result = -1;
			}finally {

				semaphore.release();

				if(result <= 0) {
					closeSession();
				}else {
					messageSent(body);
				}
			}
		});
		
	}
 
   
   private void sessionCreated() {
		LOGGER.sessionCreated(socketChannel);
		
		LAST_READ_TIME.set(System.currentTimeMillis());

		Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECT_FINISHED);
		context.sendBroadcast(intent);
		
	}

	private void sessionClosed() {

		idleHandler.removeMessages(0);

		LAST_READ_TIME.set(0); 
		
		LOGGER.sessionClosed(socketChannel);

		readBuffer.clear();
		
		if(readBuffer.capacity() > READ_BUFFER_SIZE) {
			readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
		}
		
		Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_CLOSED);
		context.sendBroadcast(intent);
		
	}

	private void sessionIdle() {

		LOGGER.sessionIdle(socketChannel);

		/*
		 * 用于解决，wifi情况下。偶而路由器与服务器断开连接时，客户端并没及时收到关闭事件 导致这样的情况下当前连接无效也不会重连的问题
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
			intent.setAction(CIMConstant.IntentAction.ACTION_SEND_FINISHED);
			intent.putExtra(SentBody.class.getName(), (SentBody) message);
			context.sendBroadcast(intent);
		}
	}

	private Handler idleHandler = new Handler(IDLE_HANDLER_THREAD.getLooper()) {
		@Override
		public void handleMessage(android.os.Message m) {
			sessionIdle();
		}
	};
	
	private void handelDisconnectedEvent() {
		closeSession();
	}
	
    private void handleConnectAbortedEvent() {

		long interval = CIMConstant.RECONNECT_INTERVAL_TIME - (5 * 1000 - new Random().nextInt(15 * 1000));
		
		LOGGER.connectFailure(interval);
		
		Intent intent = new Intent();
		intent.setPackage(context.getPackageName());
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECT_FAILED);
		intent.putExtra("interval", interval);
		context.sendBroadcast(intent);

	}
	 
	private void handelConnectedEvent()   {
		
		sessionCreated();
		
		idleHandler.sendEmptyMessageDelayed(0, READ_IDLE_TIME);
	}
	
	private void handelSocketReadEvent(int result)  {
	 
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

			send(HeartbeatResponse.getInstance());
			
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


    private boolean isHeartbeatRequest(Object data) {
		return data instanceof HeartbeatRequest;
	}
	 

}
