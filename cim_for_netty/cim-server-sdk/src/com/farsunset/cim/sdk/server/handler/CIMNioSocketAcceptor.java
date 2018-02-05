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
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.filter.ServerMessageDecoder;
import com.farsunset.cim.sdk.server.filter.ServerMessageEncoder;
import com.farsunset.cim.sdk.server.model.HeartbeatRequest;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

@Sharable
public class CIMNioSocketAcceptor extends SimpleChannelInboundHandler<SentBody> {
	public final static String WEBSOCKET_HANDLER_KEY = "client_websocket_handshake";
	private final static String CIMSESSION_CLOSED_HANDLER_KEY = "client_cimsession_closed";
	private Logger logger = Logger.getLogger(CIMNioSocketAcceptor.class);
	private HashMap<String, CIMRequestHandler> handlers = new HashMap<String, CIMRequestHandler>();
	private int port;

	// 连接空闲时间
	public static final int READ_IDLE_TIME = 150;// 秒

	// 连接空闲时间
	public static final int WRITE_IDLE_TIME = 120;// 秒

	public static final int PING_TIME_OUT = 30;// 心跳响应 超时为30秒

	public void bind() throws IOException {

		/**
		 * 预制websocket握手请求的处理
		 */
		handlers.put(WEBSOCKET_HANDLER_KEY, new WebsocketHandler());
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ServerMessageDecoder());
				ch.pipeline().addLast(new ServerMessageEncoder());
				ch.pipeline().addLast(new IdleStateHandler(READ_IDLE_TIME, WRITE_IDLE_TIME, 0));
				ch.pipeline().addLast(CIMNioSocketAcceptor.this);
			}
		});

		bootstrap.bind(port);
	}

	public void channelRegistered(ChannelHandlerContext ctx) {
		logger.info("sessionCreated()... from " + ctx.channel().remoteAddress() + " nid:"
				+ ctx.channel().id().asShortText());
	}

	protected void channelRead0(ChannelHandlerContext ctx, SentBody body) throws Exception {

		CIMSession cimSession = new CIMSession(ctx.channel());

		CIMRequestHandler handler = handlers.get(body.getKey());
		if (handler == null) {

			ReplyBody reply = new ReplyBody();
			reply.setKey(body.getKey());
			reply.setCode(CIMConstant.ReturnCode.CODE_404);
			reply.setMessage("KEY:" + body.getKey() + "  not defined on server");
			cimSession.write(reply);

		} else {
			ReplyBody reply = handler.process(cimSession, body);
			if (reply != null) {
				reply.setKey(body.getKey());
				cimSession.write(reply);
			}
		}

	}

	/**
	 */
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		CIMSession cimSession = new CIMSession(ctx.channel());

		logger.warn("sessionClosed()... from " + ctx.channel().remoteAddress() + " nid:" + cimSession.getNid()
				+ ",isConnected:" + ctx.channel().isActive());
		CIMRequestHandler handler = handlers.get(CIMSESSION_CLOSED_HANDLER_KEY);
		if (handler != null) {
			handler.process(cimSession, null);
		}
	}

	/**
	 */
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state().equals(IdleState.WRITER_IDLE)) {
			ctx.channel().attr(AttributeKey.valueOf(CIMConstant.HEARTBEAT_KEY)).set(System.currentTimeMillis());
			ctx.channel().writeAndFlush(HeartbeatRequest.getInstance());
			logger.debug(IdleState.WRITER_IDLE + "... from " + ctx.channel().remoteAddress() + " nid:"
					+ ctx.channel().id().asShortText());

		}

		// 如果心跳请求发出30秒内没收到响应，则关闭连接
		if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state().equals(IdleState.READER_IDLE)) {

			logger.debug(IdleState.READER_IDLE + "... from " + ctx.channel().remoteAddress() + " nid:"
					+ ctx.channel().id().asShortText());
			Long lastTime = (Long) ctx.channel().attr(AttributeKey.valueOf(CIMConstant.HEARTBEAT_KEY)).get();
			if (lastTime != null && System.currentTimeMillis() - lastTime >= PING_TIME_OUT) {
				ctx.channel().close();
			}

			ctx.channel().attr(AttributeKey.valueOf(CIMConstant.HEARTBEAT_KEY)).set(null);
		}
	}

	/**
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

		logger.error("exceptionCaught()... from " + ctx.channel().remoteAddress() + " isConnected:"
				+ ctx.channel().isActive() + " nid:" + ctx.channel().id().asShortText(), cause);
		ctx.channel().close();
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setHandlers(HashMap<String, CIMRequestHandler> handlers) {
		this.handlers = handlers;
	}

}
