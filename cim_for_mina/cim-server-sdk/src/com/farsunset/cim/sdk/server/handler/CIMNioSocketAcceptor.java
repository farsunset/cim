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
package com.farsunset.cim.sdk.server.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.filter.CIMLoggingFilter;
import com.farsunset.cim.sdk.server.filter.ServerMessageCodecFactory;
import com.farsunset.cim.sdk.server.model.HeartbeatRequest;
import com.farsunset.cim.sdk.server.model.HeartbeatResponse;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.model.CIMSession;

public class CIMNioSocketAcceptor extends IoHandlerAdapter implements KeepAliveMessageFactory {

	private HashMap<String, CIMRequestHandler> innerHandlerMap = new HashMap<String, CIMRequestHandler>();
	private CIMRequestHandler outerRequestHandler;
	private IoAcceptor acceptor;
	private int port;
	private final int IDLE_TIME = 120; // 秒
	private final int TIME_OUT = 10; // 秒
	private final int READ_BUFFER_SIZE = 1024; // byte

	public void bind() throws IOException {

		/**
		 * 预制websocket握手请求的处理
		 */
		innerHandlerMap.put(CIMConstant.CLIENT_WEBSOCKET_HANDSHAKE, new WebsocketHandler());

		acceptor = new NioSocketAcceptor();
		acceptor.getSessionConfig().setReadBufferSize(READ_BUFFER_SIZE);
		((DefaultSocketSessionConfig) acceptor.getSessionConfig()).setKeepAlive(true);
		((DefaultSocketSessionConfig) acceptor.getSessionConfig()).setTcpNoDelay(true);

		KeepAliveFilter keepAliveFilter = new KeepAliveFilter(this, IdleStatus.WRITER_IDLE);
		keepAliveFilter.setRequestInterval(IDLE_TIME);
		keepAliveFilter.setRequestTimeout(TIME_OUT);
		keepAliveFilter.setForwardEvent(true);

		ExecutorService executor = Executors.newCachedThreadPool(runnable -> {
	        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
	        thread.setName("mina-thread-" + thread.getId());
	        return thread;
	    });
		
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerMessageCodecFactory()));
		acceptor.getFilterChain().addLast("logger", new CIMLoggingFilter());
		acceptor.getFilterChain().addLast("heartbeat", keepAliveFilter);
		acceptor.getFilterChain().addLast("executor", new ExecutorFilter(executor));
		acceptor.setHandler(this);

		acceptor.bind(new InetSocketAddress(port));
	}

	public void unbind() {
		acceptor.unbind();
		acceptor.dispose();
	}

	/**
	 * 设置应用层的sentbody处理handler
	 * @param outerRequestHandler
	 */
	public void setAppSentBodyHandler(CIMRequestHandler outerRequestHandler) {
		this.outerRequestHandler = outerRequestHandler;
	}

	@Override
	public void messageReceived(IoSession ios, Object message) {

		SentBody body = (SentBody) message;
		CIMSession session = new CIMSession(ios);
		
		CIMRequestHandler handler = innerHandlerMap.get(body.getKey());
		/**
		 * 如果有内置的特殊handler需要处理，则使用内置的
		 */
		if (handler != null) {
			handler.process(session, body);
			return ;
		}
		
		/**
		 * 有业务层去处理其他的sentbody
		 */
		outerRequestHandler.process(session, body);
	}
 
	@Override
	public void sessionClosed(IoSession ios) {
      
		CIMSession session = new CIMSession(ios);
        SentBody body = new SentBody();
        body.setKey(CIMConstant.CLIENT_CONNECT_CLOSED);
		outerRequestHandler.process(session, body);
	}

	@Override
	public Object getRequest(IoSession session) {
		return HeartbeatRequest.getInstance();
	}

	@Override
	public Object getResponse(IoSession arg0, Object arg1) {
		return null;
	}

	@Override
	public boolean isRequest(IoSession arg0, Object arg1) {
		return false;
	}

	@Override
	public boolean isResponse(IoSession arg0, Object arg1) {
		return arg1 instanceof HeartbeatResponse;
	}

	public Map<Long, IoSession> getManagedSessions() {
		return acceptor.getManagedSessions();
	}

	public IoSession getManagedSession(Long nid) {
		if (nid == null) {
			return null;
		}
		return getManagedSessions().get(nid);
	}

	public void setPort(int port) {
		this.port = port;
	}
 

}
