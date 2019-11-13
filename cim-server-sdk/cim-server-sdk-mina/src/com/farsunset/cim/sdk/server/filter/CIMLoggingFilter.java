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
package com.farsunset.cim.sdk.server.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.farsunset.cim.sdk.server.constant.CIMConstant;

/**
 * 日志打印，添加session 的id和ip address
 */
public class CIMLoggingFilter extends IoFilterAdapter {

	private final Logger logger = LoggerFactory.getLogger(CIMLoggingFilter.class);

	@Override
	public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause)   {
		logger.error("EXCEPTION" + getSessionInfo(session) + "\n{}", cause);
		nextFilter.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message)  {
		logger.info("RECEIVED" + getSessionInfo(session) + "\n{}", message);
		nextFilter.messageReceived(session, message);
	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest)   {
		logger.info("SENT" + getSessionInfo(session) + "\n{}", writeRequest.getOriginalMessage());
		nextFilter.messageSent(session, writeRequest);
	}

	@Override
	public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {
		logger.info("CREATED" + getSessionInfo(session));
		nextFilter.sessionCreated(session);
	}

	@Override
	public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
		logger.info("OPENED" + getSessionInfo(session));
		nextFilter.sessionOpened(session);
	}

	@Override
	public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status)   {
		logger.info("IDLE" + getSessionInfo(session));
		nextFilter.sessionIdle(session, status);
	}

	@Override
	public void sessionClosed(NextFilter nextFilter, IoSession session)  {
		logger.info("CLOSED" + getSessionInfo(session));
		nextFilter.sessionClosed(session);
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
		
		if (session.containsAttribute(CIMConstant.KEY_ACCOUNT)) {
			builder.append(" account:").append(session.getAttribute(CIMConstant.KEY_ACCOUNT));
		}
		builder.append("]");
		return builder.toString();
	}
}
