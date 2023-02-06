/*
 * Copyright 2013-2022 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.acceptor;

import com.farsunset.cim.acceptor.config.SocketConfig;
import com.farsunset.cim.constant.CIMConstant;
import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.handler.LoggingHandler;
import com.farsunset.cim.model.Ping;
import com.farsunset.cim.model.SentBody;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

abstract class NioSocketAcceptor extends SimpleChannelInboundHandler<SentBody>{

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final ChannelHandler loggingHandler = new LoggingHandler();

	protected final SocketConfig socketConfig;

	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;

	protected NioSocketAcceptor(SocketConfig socketConfig){

		this.socketConfig = socketConfig;

		ThreadFactory bossThreadFactory = r -> {
			Thread thread = new Thread(r);
			thread.setName("nio-boss-" + thread.getId());
			return thread;
		};

		ThreadFactory workerThreadFactory = r -> {
			Thread thread = new Thread(r);
			thread.setName("nio-worker-" + thread.getId());
			return thread;
		};

		if (isLinuxSystem()){
			bossGroup = new EpollEventLoopGroup(bossThreadFactory);
			workerGroup = new EpollEventLoopGroup(workerThreadFactory);
		}else {
			bossGroup = new NioEventLoopGroup(bossThreadFactory);
			workerGroup = new NioEventLoopGroup(workerThreadFactory);
		}

	}

	/**
	 * 执行启动SOCKET服务
	 */
	public abstract void bind();


	/**
	 * 执行注销SOCKET服务
	 */
	public void destroy() {
		if(bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown() ) {
			try {bossGroup.shutdownGracefully();}catch(Exception ignore) {}
		}

		if(workerGroup != null && !workerGroup.isShuttingDown() && !workerGroup.isShutdown() ) {
			try {workerGroup.shutdownGracefully();}catch(Exception ignore) {}
		}
	}

	protected ServerBootstrap createServerBootstrap(){
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.channel(isLinuxSystem() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
		return bootstrap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, SentBody body) {
		/*
		 * 由业务层去处理其他的sentBody
		 */
		if (socketConfig.getOuterRequestHandler() != null){
			socketConfig.getOuterRequestHandler().process(ctx.channel(), body);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.channel().attr(ChannelAttr.ID).set(ctx.channel().id().asShortText());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {

		if (ctx.channel().attr(ChannelAttr.UID) == null){
			return;
		}

		if (socketConfig.getOuterRequestHandler() == null){
			return;
		}

		SentBody body = new SentBody();
		body.setKey(CIMConstant.CLIENT_CONNECT_CLOSED);
		socketConfig.getOuterRequestHandler().process(ctx.channel(), body);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt){

		if (! (evt instanceof IdleStateEvent)){
			return;
		}

		IdleStateEvent idleEvent = (IdleStateEvent) evt;

		String uid = ctx.channel().attr(ChannelAttr.UID).get();

		/*
		 * 关闭未认证的连接
		 */
		if (idleEvent.state() == IdleState.WRITER_IDLE && uid == null) {
			ctx.close();
			return;
		}

		/*
		 * 已经认证的连接发送心跳请求
		 */
		if (idleEvent.state() == IdleState.WRITER_IDLE && uid != null) {

			Integer pingCount = ctx.channel().attr(ChannelAttr.PING_COUNT).get();
			ctx.channel().attr(ChannelAttr.PING_COUNT).set(pingCount == null ? 1 : pingCount + 1);

			ctx.channel().writeAndFlush(Ping.getInstance());

			return;
		}

		/*
		 * 如果心跳请求发出（readIdle-writeIdle）秒内没收到响应，则关闭连接
		 */
		Integer pingCount = ctx.channel().attr(ChannelAttr.PING_COUNT).get();
		if (idleEvent.state() == IdleState.READER_IDLE && pingCount != null && pingCount >= socketConfig.getMaxPongTimeout()) {
			ctx.close();
			logger.info("{} pong timeout.",ctx.channel());
		}
	}

	private boolean isLinuxSystem(){
		String osName = System.getProperty("os.name").toLowerCase();
		return osName.contains("linux");
	}

}
