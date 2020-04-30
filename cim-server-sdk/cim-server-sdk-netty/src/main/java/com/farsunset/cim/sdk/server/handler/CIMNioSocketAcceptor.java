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

import com.farsunset.cim.sdk.server.coder.AppMessageDecoder;
import com.farsunset.cim.sdk.server.coder.AppMessageEncoder;
import com.farsunset.cim.sdk.server.coder.WebMessageDecoder;
import com.farsunset.cim.sdk.server.coder.WebMessageEncoder;
import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.CIMSession;
import com.farsunset.cim.sdk.server.model.HeartbeatRequest;
import com.farsunset.cim.sdk.server.model.HeartbeatResponse;
import com.farsunset.cim.sdk.server.model.SentBody;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CIMNioSocketAcceptor{
	private static final Logger LOGGER = LoggerFactory.getLogger(CIMNioSocketAcceptor.class);

	private final HashMap<String, CIMRequestHandler> innerHandlerMap = new HashMap<>();
    private final ConcurrentHashMap<String,Channel> channelGroup = new ConcurrentHashMap<>();


	private EventLoopGroup appBossGroup;
	private EventLoopGroup appWorkerGroup;

	private EventLoopGroup webBossGroup;
	private EventLoopGroup webWorkerGroup;

	private final Integer appPort;
	private final Integer webPort;
	private final CIMRequestHandler outerRequestHandler;
	private final ChannelHandler channelEventHandler = new FinalChannelEventHandler();

	/**
	 *  读空闲时间(秒)
	 */
	public static final int READ_IDLE_TIME = 150;

	/**
	 *  写接空闲时间(秒)
	 */
	public static final int WRITE_IDLE_TIME = 120;

	/**
	 * 心跳响应 超时为30秒
	 */
	public static final int PONG_TIME_OUT = 10;

	private CIMNioSocketAcceptor(Builder builder){
		this.webPort = builder.webPort;
		this.appPort = builder.appPort;
		this.outerRequestHandler = builder.outerRequestHandler;
	}

	public void bind() {
 
		innerHandlerMap.put(CIMConstant.CLIENT_HEARTBEAT, new HeartbeatHandler());

		if (appPort != null){
			bindAppPort();
		}

		if (webPort != null){
			bindWebPort();
		}
	}

	public void destroy(EventLoopGroup bossGroup , EventLoopGroup workerGroup) {
		if(bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown() ) {
			try {bossGroup.shutdownGracefully();}catch(Exception ignore) {}
		}

		if(workerGroup != null && !workerGroup.isShuttingDown() && !workerGroup.isShutdown() ) {
			try {workerGroup.shutdownGracefully();}catch(Exception ignore) {}
		}
	}


	/**
	 * 关闭长连接服务
	 */
    public void destroy() {
    	this.destroy(appBossGroup,appWorkerGroup);
		this.destroy(webBossGroup,webWorkerGroup);
	}


	public Channel getManagedSession(String id) {
		if (id == null) {
			return null;
		}
		return channelGroup.get(id);
	}

	private void bindAppPort(){
		appBossGroup = new NioEventLoopGroup();
		appWorkerGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = createServerBootstrap(appBossGroup,appWorkerGroup);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch){
				ch.pipeline().addLast(new AppMessageDecoder());
				ch.pipeline().addLast(new AppMessageEncoder());
				ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
				ch.pipeline().addLast(new IdleStateHandler(READ_IDLE_TIME, WRITE_IDLE_TIME, 0));
				ch.pipeline().addLast(channelEventHandler);
			}
		});

		ChannelFuture channelFuture = bootstrap.bind(appPort).syncUninterruptibly();
		channelFuture.channel().newSucceededFuture().addListener(future -> {
			String logBanner = "\n\n" +
					"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
					"*                                                                                   *\n" +
					"*                                                                                   *\n" +
					"*                   App Socket Server started on port {}.                        *\n" +
					"*                                                                                   *\n" +
					"*                                                                                   *\n" +
					"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
			LOGGER.info(logBanner, appPort);
		});
		channelFuture.channel().closeFuture().addListener(future -> this.destroy(appBossGroup,appWorkerGroup));
	}

	private void bindWebPort(){
		webBossGroup = new NioEventLoopGroup();
		webWorkerGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = createServerBootstrap(webBossGroup,webWorkerGroup);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch){
				ch.pipeline().addLast(new HttpServerCodec());
				ch.pipeline().addLast(new ChunkedWriteHandler());
				ch.pipeline().addLast(new HttpObjectAggregator(65536));
				ch.pipeline().addLast(new WebMessageEncoder());
				ch.pipeline().addLast(new WebMessageDecoder());
				ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
				ch.pipeline().addLast(channelEventHandler);
			}

		});

		ChannelFuture channelFuture = bootstrap.bind(webPort).syncUninterruptibly();
		channelFuture.channel().newSucceededFuture().addListener(future -> {
			String logBanner = "\n\n" +
					"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
					"*                                                                                   *\n" +
					"*                                                                                   *\n" +
					"*                   Websocket Server started on port {}.                         *\n" +
					"*                                                                                   *\n" +
					"*                                                                                   *\n" +
					"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
			LOGGER.info(logBanner, webPort);
		});
		channelFuture.channel().closeFuture().addListener(future -> this.destroy(webBossGroup,webWorkerGroup));
	}

	private ServerBootstrap createServerBootstrap(EventLoopGroup bossGroup,EventLoopGroup workerGroup){
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.channel(NioServerSocketChannel.class);
		return bootstrap;
	}

	@Sharable
	private class FinalChannelEventHandler extends  SimpleChannelInboundHandler<Object>{
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Object data) {

			if (data instanceof HeartbeatResponse){
				return;
			}

			SentBody body = (SentBody) data;

			CIMSession session = new CIMSession(ctx.channel());

			CIMRequestHandler handler = innerHandlerMap.get(body.getKey());
			/*
			 * 如果有内置的特殊handler需要处理，则使用内置的
			 */
			if (handler != null) {
				handler.process(session, body);
				return;
			}

			/*
			 * 有业务层去处理其他的sentBody
			 */
			outerRequestHandler.process(session, body);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) {
			channelGroup.remove(ctx.channel().id().asShortText());

			CIMSession session = new CIMSession(ctx.channel());
			SentBody body = new SentBody();
			body.setKey(CIMConstant.CLIENT_CONNECT_CLOSED);
			outerRequestHandler.process(session, body);

		}

		@Override
		public void channelActive(ChannelHandlerContext ctx){
			channelGroup.put(ctx.channel().id().asShortText(),ctx.channel());
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt){

			if (! (evt instanceof IdleStateEvent)){
				return;
			}

			IdleStateEvent idleEvent = (IdleStateEvent) evt;

			if (idleEvent.state().equals(IdleState.WRITER_IDLE)) {
				ctx.channel().attr(AttributeKey.valueOf(CIMConstant.HEARTBEAT_KEY)).set(System.currentTimeMillis());
				ctx.channel().writeAndFlush(HeartbeatRequest.getInstance());
			}

			/*
			 * 如果心跳请求发出30秒内没收到响应，则关闭连接
			 */
			if (idleEvent.state().equals(IdleState.READER_IDLE)) {

				Long lastTime = (Long) ctx.channel().attr(AttributeKey.valueOf(CIMConstant.HEARTBEAT_KEY)).get();
				if (lastTime != null && System.currentTimeMillis() - lastTime >= PONG_TIME_OUT) {
					ctx.channel().close();
				}

				ctx.channel().attr(AttributeKey.valueOf(CIMConstant.HEARTBEAT_KEY)).set(null);
			}
		}

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
