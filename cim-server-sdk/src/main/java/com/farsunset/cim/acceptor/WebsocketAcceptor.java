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

import com.farsunset.cim.acceptor.config.WebsocketConfig;
import com.farsunset.cim.coder.json.TextMessageDecoder;
import com.farsunset.cim.coder.json.TextMessageEncoder;
import com.farsunset.cim.coder.protobuf.WebMessageDecoder;
import com.farsunset.cim.coder.protobuf.WebMessageEncoder;
import com.farsunset.cim.constant.WebsocketProtocol;
import com.farsunset.cim.handler.IllegalRequestHandler;
import com.farsunset.cim.handshake.HandshakeHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * websocket协议端口
 * 可针对原生app使用
 */
@ChannelHandler.Sharable
public class WebsocketAcceptor extends NioSocketAcceptor {

	private static final String JSON_BANNER = "\n\n" +
			"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
			"*                                                                                   *\n" +
			"*                                                                                   *\n" +
			"*              Websocket Server started on port {} for [JSON] mode.              *\n" +
			"*                                                                                   *\n" +
			"*                                                                                   *\n" +
			"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";

	private static final String PROTOBUF_BANNER = "\n\n" +
			"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
			"*                                                                                   *\n" +
			"*                                                                                   *\n" +
			"*             Websocket Server started on port {} for [protobuf] mode.           *\n" +
			"*                                                                                   *\n" +
			"*                                                                                   *\n" +
			"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";

	private final WebsocketConfig config;

	private final HandshakeHandler handshakeHandler;

	private final ChannelHandler illegalRequestHandler = new IllegalRequestHandler();

	public WebsocketAcceptor(WebsocketConfig config){
		super(config);
		this.config = config;
		this.handshakeHandler = new HandshakeHandler(config.getHandshakePredicate());
	}

	/**
	 * bind基于websocket协议的socket端口
	 */
	@Override
	public void bind(){

		if (!config.isEnable()){
			return;
		}

		ServerBootstrap bootstrap = createServerBootstrap();
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch){
				ch.pipeline().addLast(new HttpServerCodec());
				ch.pipeline().addLast(new ChunkedWriteHandler());
				ch.pipeline().addLast(new HttpObjectAggregator(4 * 1024));
				ch.pipeline().addLast(new WebSocketServerProtocolHandler(config.getPath(),true));
				ch.pipeline().addLast(handshakeHandler);
				if (config.getProtocol() == WebsocketProtocol.JSON){
					ch.pipeline().addLast(new TextMessageDecoder());
					ch.pipeline().addLast(new TextMessageEncoder());
				}else {
					ch.pipeline().addLast(new WebMessageDecoder());
					ch.pipeline().addLast(new WebMessageEncoder());
				}
				ch.pipeline().addLast(new IdleStateHandler(config.getReadIdle().getSeconds(), config.getWriteIdle().getSeconds(), 0, TimeUnit.SECONDS));
				ch.pipeline().addLast(loggingHandler);
				ch.pipeline().addLast(WebsocketAcceptor.this);
				ch.pipeline().addLast(illegalRequestHandler);
			}

		});

		ChannelFuture channelFuture = bootstrap.bind(config.getPort()).syncUninterruptibly();
		channelFuture.channel().newSucceededFuture().addListener(future -> {
			if (config.getProtocol() == WebsocketProtocol.JSON){
				logger.info(JSON_BANNER, config.getPort());
			}
			if (config.getProtocol() == WebsocketProtocol.PROTOBUF){
				logger.info(PROTOBUF_BANNER, config.getPort());
			}
		});
		channelFuture.channel().closeFuture().addListener(future -> this.destroy());
	}

}
