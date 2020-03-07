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
package com.farsunset.cim.sdk.client.coder;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 日志打印，添加session 的id和ip address
 */
public class CIMLogger  {
    private static final Logger LOGGER = LoggerFactory.getLogger(CIMLogger.class);

	public static CIMLogger getLogger() {
		return LoggerHolder.logger;
	}
	
	private CIMLogger() {
		
	}
	
	private static class LoggerHolder{
		private static final CIMLogger logger = new CIMLogger();
	}
	 

	public void messageReceived(SocketChannel session, Object message)  {
		LOGGER.info(String.format("RECEIVED" + getSessionInfo(session) + "\n%s", message));
	}

	public void messageSent(SocketChannel session, Object message)   {
		LOGGER.info(String.format("SENT" + getSessionInfo(session) + "\n%s", message));
	}

	public void sessionCreated( SocketChannel session) {
		LOGGER.info("OPENED" + getSessionInfo(session));
	}

	public void sessionIdle( SocketChannel session)   {
		LOGGER.debug("IDLE READ" + getSessionInfo(session));
	}

	public void sessionClosed( SocketChannel session)  {
		LOGGER.warn("CLOSED ID = " + session.hashCode());

	}

	public void connectFailure(long interval)  {
		LOGGER.debug("CONNECT FAILURE TRY RECONNECT AFTER " + interval +"ms");
	}
	
	public void startConnect(String host , int port) {
		LOGGER.info("START CONNECT REMOTE HOST:" + host + " PORT:" + port);
	}
	
	public void connectState(boolean isConnected)  {
		LOGGER.debug("CONNECTED:" + isConnected);
	}
	 
	public void connectState(boolean isConnected,boolean isManualStop,boolean isDestroyed)  {
		LOGGER.debug("CONNECTED:" + isConnected + " STOPPED:"+isManualStop+ " DESTROYED:"+isDestroyed);
	}
	
	private String getSessionInfo(SocketChannel session) {
		StringBuilder builder = new StringBuilder();
		if (session == null) {
			return "";
		}
		builder.append(" [");
		builder.append("id:").append(session.hashCode());
		
		try {
			if (session.socket().getLocalAddress() != null) {
				builder.append(" L:").append(session.socket().getLocalAddress()+":"+session.socket().getLocalPort());
			}
		} catch (Exception ignore) {
		}
		
		
		try {
			if (session.socket().getRemoteSocketAddress() != null) {
				builder.append(" R:").append(session.socket().getRemoteSocketAddress().toString());
			}
		} catch (Exception ignore) {
		}
		builder.append("]");
		return builder.toString();
	}
	 
	 
	
}
