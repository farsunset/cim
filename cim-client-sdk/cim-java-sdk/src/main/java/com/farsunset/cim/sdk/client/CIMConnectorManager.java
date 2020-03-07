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
package com.farsunset.cim.sdk.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.farsunset.cim.sdk.client.coder.CIMLogger;
import com.farsunset.cim.sdk.client.coder.ClientMessageDecoder;
import com.farsunset.cim.sdk.client.coder.ClientMessageEncoder;
import com.farsunset.cim.sdk.client.constant.CIMConstant;
import com.farsunset.cim.sdk.client.model.HeartbeatRequest;
import com.farsunset.cim.sdk.client.model.HeartbeatResponse;
import com.farsunset.cim.sdk.client.model.Intent;
import com.farsunset.cim.sdk.client.model.Message;
import com.farsunset.cim.sdk.client.model.Protobufable;
import com.farsunset.cim.sdk.client.model.ReplyBody;
import com.farsunset.cim.sdk.client.model.SentBody;


/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 * @author 3979434@qq.com
 */
class CIMConnectorManager {

	private static CIMConnectorManager manager;
	
	private final int READ_BUFFER_SIZE = 2048;
	private final int WRITE_BUFFER_SIZE = 1024;
	private final int CONNECT_TIME_OUT = 10 * 1000;

    private final CIMLogger LOGGER = CIMLogger.getLogger();

	private SocketChannel socketChannel ;
	
    private ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);

	private final ExecutorService workerExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r,"worker-"));
	private final ExecutorService bossExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r,"boss-"));
	private final ExecutorService eventExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r,"event-"));
	

	private final ClientMessageEncoder messageEncoder = new  ClientMessageEncoder();
	private final ClientMessageDecoder messageDecoder = new  ClientMessageDecoder();

	private final ByteBuffer headerBuffer = ByteBuffer.allocate(CIMConstant.DATA_HEADER_LENGTH);


	public synchronized static CIMConnectorManager getManager() {
		
		if (manager == null) {
			manager = new CIMConnectorManager();
		}
		
		return manager;

	}

	public void connect(final String host, final int port) {
 
		 
		if (isConnected()) {
			return;
		}
	 
		bossExecutor.execute(() -> {

			if (isConnected()) {
				return;
			}

			LOGGER.startConnect(host, port);

			CIMCacheManager.getInstance().putBoolean(CIMCacheManager.KEY_CIM_CONNECTION_STATE, false);

			 try {

				 socketChannel = SocketChannel.open();
				 socketChannel.configureBlocking(true);
				 socketChannel.socket().setTcpNoDelay(true);
				 socketChannel.socket().setKeepAlive(true);
				 socketChannel.socket().setReceiveBufferSize(READ_BUFFER_SIZE);
				 socketChannel.socket().setSendBufferSize(WRITE_BUFFER_SIZE);

				 socketChannel.socket().connect(new InetSocketAddress(host, port),CONNECT_TIME_OUT);

				 handleConnectedEvent();


				 /*
				  *开始读取来自服务端的消息，先读取3个字节的消息头
				  */
				 while (socketChannel.read(headerBuffer) > 0) {
					 handleSocketReadEvent();
				 }

				 /*
				  *read 返回 <= 0的情况，发生了意外需要断开重链
				  */
				 closeSession();

			}catch(ConnectException | SocketTimeoutException ignore){
				handleConnectAbortedEvent();
			} catch(IOException ignore) {
				handleDisconnectedEvent();
			}
		});
	}

	private void handleDisconnectedEvent() {
		closeSession();
	}
	
    private void handleConnectAbortedEvent() {

		long interval = CIMConstant.RECONNECT_INTERVAL_TIME - (5 * 1000 - new Random().nextInt(15 * 1000));
		
		LOGGER.connectFailure(interval);
		
		Intent intent = new Intent();
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECT_FAILED);
		intent.putExtra("interval", interval);
		sendBroadcast(intent);

	}
	 
	private void handleConnectedEvent() {
		
		sessionCreated();
	}

	private void handleSocketReadEvent() throws IOException {

		Object message = messageDecoder.doDecode(headerBuffer,socketChannel);

		LOGGER.messageReceived(socketChannel, message);

		if (isHeartbeatRequest(message)) {
			send(HeartbeatResponse.getInstance());
			return;
		}

		this.messageReceived(message);

	}


   public void send(final Protobufable body) {
		
	   if(!isConnected()) {
			return;
		}
		
		workerExecutor.execute(() -> {
			int result = 0;
			try {

				ByteBuffer buffer =  messageEncoder.encode(body);
				while(buffer.hasRemaining()){
					result += socketChannel.write(buffer);
				}

			} catch (Exception e) {
				result = -1;
			}finally {

				if(result <= 0) {
					closeSession();
				}else {
					messageSent(body);
				}
			}
		});
	}
  

	public void sessionCreated() {
		
		LOGGER.sessionCreated(socketChannel);
		
		Intent intent = new Intent();
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECT_FINISHED);
		sendBroadcast(intent);
		
	}

	public void sessionClosed() {

 		LOGGER.sessionClosed(socketChannel);

		readBuffer.clear();
		
		if(readBuffer.capacity() > READ_BUFFER_SIZE) {
			readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
		}
		
		Intent intent = new Intent();
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_CLOSED);
		sendBroadcast(intent);
		
	}
 
	public void messageReceived(Object data) {

		if (data instanceof Message) {

			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_MESSAGE_RECEIVED);
			intent.putExtra(Message.class.getName(), data);
			sendBroadcast(intent);

		}
		if (data instanceof ReplyBody) {

			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_REPLY_RECEIVED);
			intent.putExtra(ReplyBody.class.getName(), data);
			sendBroadcast(intent);
		}
	}

	
	public void messageSent(Object data) {
		
		LOGGER.messageSent(socketChannel, data);
		
		if (data instanceof SentBody) {
			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_SEND_FINISHED);
			intent.putExtra(SentBody.class.getName(), data);
			sendBroadcast(intent);
		}
	}

	public boolean isHeartbeatRequest(Object data) {
		return data instanceof HeartbeatRequest;
	}
 
    public void destroy() {
		closeSession();
	}

	public boolean isConnected() {
		return socketChannel != null && socketChannel.isConnected();
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
 
	
	private void sendBroadcast(final Intent intent) {
		eventExecutor.execute(() -> CIMEventBroadcastReceiver.getInstance().onReceive(intent));
	}
	 
}
