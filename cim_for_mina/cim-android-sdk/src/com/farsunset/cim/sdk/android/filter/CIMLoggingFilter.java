/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.sdk.android.filter;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

import android.util.Log;


/**
 * 日志打印，添加session 的id和ip address
 */
public class CIMLoggingFilter extends IoFilterAdapter {
	private final static String TAG = "CIM";
	private boolean debug = true;
	
	public static CIMLoggingFilter getLogger() {
		return LoggerHolder.logger;
	}
	
	private CIMLoggingFilter() {
		
	}
	
	private static class LoggerHolder{
		private static CIMLoggingFilter logger = new CIMLoggingFilter();
	}
	
	public void  debugMode(boolean mode) {
		debug = mode;
	}
	
	@Override
	public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause)   {
		if(debug) {
			Log.d(TAG,String.format("EXCEPTION" + getSessionInfo(session) + "\n%s", cause.getClass().getName()));
		}
		session.closeOnFlush();
	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message)  {
		if(debug) {
			Log.i(TAG,String.format("RECEIVED" + getSessionInfo(session) + "\n%s", message));
		}
		nextFilter.messageReceived(session, message);
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest)   {
		if(debug) {
			Log.i(TAG,String.format("SENT" + getSessionInfo(session) + "\n%s", writeRequest.getOriginalRequest().getMessage()));
		}
		nextFilter.messageSent(session, writeRequest);
	}

	@Override
	public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {
		nextFilter.sessionCreated(session);
	}

	@Override
	public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
		if(debug) {
			Log.i(TAG,"OPENED" + getSessionInfo(session));
		}
		nextFilter.sessionOpened(session);
	}

	@Override
	public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status)   {
		if(debug) {
			Log.i(TAG,"IDLE " + status.toString().toUpperCase() + getSessionInfo(session));
		}
		nextFilter.sessionIdle(session, status);
	}

	@Override
	public void sessionClosed(NextFilter nextFilter, IoSession session)  {
		if(debug) {
			Log.i(TAG,"CLOSED" + getSessionInfo(session));
		}
		nextFilter.sessionClosed(session);
	}

	public void connectFailure(InetSocketAddress remoteAddress,long interval)  {
		if(debug) {
			Log.i(TAG,"CONNECT FAILURE TRY RECONNECT AFTER " + interval +"ms");
		}
	}
	
	public void startConnect(InetSocketAddress remoteAddress) {
		if(debug) {
			Log.i(TAG,"START CONNECT REMOTE HOST: " + remoteAddress.toString());
		} 
	}
	
	public void connectState(boolean isConnected)  {
		if(debug) {
			Log.i(TAG,"CONNECTED:" + isConnected);
		}
	}
	 
	public void connectState(boolean isConnected,boolean isManualStop,boolean isDestroyed)  {
		if(debug) {
			Log.i(TAG,"CONNECTED:" + isConnected + " STOPED:"+isManualStop+ " DESTROYED:"+isDestroyed);
		}
	}
	private String getSessionInfo(IoSession session) {
		StringBuilder builder = new StringBuilder();
		if (session == null) {
			return "";
		}
		builder.append(" [");
		builder.append("id:").append(session.getId());
		
		if (session.getLocalAddress() != null) {
			builder.append(" L:").append(session.getLocalAddress().toString());
		}
		
		
		if (session.getRemoteAddress() != null) {
			builder.append(" R:").append(session.getRemoteAddress().toString());
		}
		builder.append("]");
		return builder.toString();
	}
	 
	
}
