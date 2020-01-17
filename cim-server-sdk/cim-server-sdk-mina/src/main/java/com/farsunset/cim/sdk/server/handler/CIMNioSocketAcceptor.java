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
package com.farsunset.cim.sdk.server.handler;

import com.farsunset.cim.sdk.server.coder.AppMessageCodecFactory;
import com.farsunset.cim.sdk.server.coder.WebMessageCodecFactory;
import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.CIMSession;
import com.farsunset.cim.sdk.server.model.HeartbeatRequest;
import com.farsunset.cim.sdk.server.model.HeartbeatResponse;
import com.farsunset.cim.sdk.server.model.SentBody;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.*;

public class CIMNioSocketAcceptor extends IoHandlerAdapter implements KeepAliveMessageFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(CIMNioSocketAcceptor.class);
	private static final HashMap<String, CIMRequestHandler> INNER_HANDLER_MAP = new HashMap<>();

	/**
	 * 读写空闲2分钟后 服务端 -> 客户端 发起心跳请求
	 */
	private static final int IDLE_HEART_REQUEST_TIME = 120;
	
	/**
	 * 发起心跳后等待客户端的心跳响应，超时10秒后断开连接
	 */
	private static final int HEART_RESPONSE_TIME_OUT = 10;

	private IoAcceptor appAcceptor;
	private IoAcceptor webAcceptor;
	private final Integer appPort;
	private final Integer webPort;
	private final CIMRequestHandler outerRequestHandler;

	private CIMNioSocketAcceptor(Builder builder){
		this.webPort = builder.webPort;
		this.appPort = builder.appPort;
		this.outerRequestHandler = builder.outerRequestHandler;
	}

	public void bind() {

		if (appPort != null){
			try {
				bindAppPort();
			} catch (IOException e) {
				LOGGER.error("App port bind error.",e);
			}
		}

		if (webPort != null){
			try {
				bindWebPort();
			} catch (IOException e) {
				LOGGER.error("Web port bind error.",e);
			}
		}
	}

	private void bindAppPort() throws IOException {


		appAcceptor = new NioSocketAcceptor();
		((DefaultSocketSessionConfig) appAcceptor.getSessionConfig()).setKeepAlive(true);
		((DefaultSocketSessionConfig) appAcceptor.getSessionConfig()).setTcpNoDelay(true);

		KeepAliveFilter keepAliveFilter = new KeepAliveFilter(this, IdleStatus.BOTH_IDLE);
		keepAliveFilter.setRequestInterval(IDLE_HEART_REQUEST_TIME);
		keepAliveFilter.setRequestTimeout(HEART_RESPONSE_TIME_OUT);
		keepAliveFilter.setForwardEvent(true);

		appAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new AppMessageCodecFactory()));
		appAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		appAcceptor.getFilterChain().addLast("heartbeat", keepAliveFilter);
		appAcceptor.getFilterChain().addLast("executor", new ExecutorFilter(createWorkerExecutor()));
		appAcceptor.setHandler(this);

		appAcceptor.bind(new InetSocketAddress(appPort));
		String logBanner = "\n\n" +
				"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
				"*                                                                                   *\n" +
				"*                                                                                   *\n" +
				"*                   App Socket Server started on port {}.                        *\n" +
				"*                                                                                   *\n" +
				"*                                                                                   *\n" +
				"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
		LOGGER.info(logBanner, appPort);
	}


	private void bindWebPort() throws IOException {

		/*
		 * 预制websocket握手请求的处理
		 */
		INNER_HANDLER_MAP.put(CIMConstant.CLIENT_WEBSOCKET_HANDSHAKE, new WebsocketHandler());

		webAcceptor = new NioSocketAcceptor();
		((DefaultSocketSessionConfig) webAcceptor.getSessionConfig()).setKeepAlive(true);
		((DefaultSocketSessionConfig) webAcceptor.getSessionConfig()).setTcpNoDelay(true);
		webAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new WebMessageCodecFactory()));
		webAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		webAcceptor.getFilterChain().addLast("executor", new ExecutorFilter(createWorkerExecutor()));
		webAcceptor.setHandler(this);

		webAcceptor.bind(new InetSocketAddress(webPort));
		String logBanner = "\n\n" +
				"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
				"*                                                                                   *\n" +
				"*                                                                                   *\n" +
				"*                   Websocket Server started on port {}.                         *\n" +
				"*                                                                                   *\n" +
				"*                                                                                   *\n" +
				"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
		LOGGER.info(logBanner, webPort);
	}


	private Executor createWorkerExecutor(){
		int coreSize = Runtime.getRuntime().availableProcessors() * 2;

		return new ThreadPoolExecutor(coreSize,coreSize,60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), runnable -> {
			Thread thread = Executors.defaultThreadFactory().newThread(runnable);
			thread.setName("mina-thread-" + thread.getId());
			return thread;
		});
	}
 
    public void destroy() {
    	if(webAcceptor != null) {
			webAcceptor.unbind();
			webAcceptor.dispose();
    	}

		if(appAcceptor != null) {
			appAcceptor.unbind();
			appAcceptor.dispose();
		}
    }


	@Override
	public void messageReceived(IoSession ios, Object message) {

		SentBody body = (SentBody) message;
		CIMSession session = new CIMSession(ios);
		
		CIMRequestHandler handler = INNER_HANDLER_MAP.get(body.getKey());
		/*
		 * 如果有内置的特殊handler需要处理，则使用内置的
		 */
		if (handler != null) {
			handler.process(session, body);
			return ;
		}
		
		/*
		 * 有业务层去处理其他的sentBody
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


	public IoSession getManagedSession(String nid) {
		if (nid == null) {
			return null;
		}

		long id = Long.parseLong(nid);

		IoSession session = appAcceptor == null ? null : appAcceptor.getManagedSessions().get(id);

		if (session != null){
			return session;
		}

		return webAcceptor == null ? null :webAcceptor.getManagedSessions().get(id);
	}

	public static class Builder{

		private Integer appPort;
		private Integer webPort;
		private CIMRequestHandler outerRequestHandler;

		public Builder setAppPort(Integer appPort) {
			this.appPort = appPort;
			return this;
		}

		public Builder setWebsocketPort(Integer port) {
			this.webPort = port;
			return this;
		}

		/**
		 * 设置应用层的sentBody处理handler
		 */
		public Builder setOuterRequestHandler(CIMRequestHandler outerRequestHandler) {
			this.outerRequestHandler = outerRequestHandler;
			return this;
		}

		public CIMNioSocketAcceptor build(){
			return new CIMNioSocketAcceptor(this);
		}

	}

}
