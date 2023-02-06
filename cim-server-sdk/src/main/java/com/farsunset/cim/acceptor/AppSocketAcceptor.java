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
import com.farsunset.cim.coder.protobuf.AppMessageDecoder;
import com.farsunset.cim.coder.protobuf.AppMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * tlv协议端口
 * 可针对原生app使用
 */

@ChannelHandler.Sharable
public class AppSocketAcceptor extends NioSocketAcceptor {


	public AppSocketAcceptor(SocketConfig config){
		super(config);
	}


	/**
	 * bind基于tlv协议的socket端口
	 */
	@Override
	public void bind(){

		if (!socketConfig.isEnable()){
			return;
		}

		ServerBootstrap bootstrap = createServerBootstrap();
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch){
				ch.pipeline().addLast(new AppMessageDecoder());
				ch.pipeline().addLast(new AppMessageEncoder());
				ch.pipeline().addLast(loggingHandler);
				ch.pipeline().addLast(new IdleStateHandler(socketConfig.getReadIdle().getSeconds(), socketConfig.getWriteIdle().getSeconds(), 0, TimeUnit.SECONDS));
				ch.pipeline().addLast(AppSocketAcceptor.this);
			}
		});

		ChannelFuture channelFuture = bootstrap.bind(socketConfig.getPort()).syncUninterruptibly();
		channelFuture.channel().newSucceededFuture().addListener(future -> {
			String logBanner = "\n\n" +
					"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
					"*                                                                                   *\n" +
					"*                                                                                   *\n" +
					"*                   App Socket Server started on port {}.                        *\n" +
					"*                                                                                   *\n" +
					"*                                                                                   *\n" +
					"* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n";
			logger.info(logBanner, socketConfig.getPort());
		});
		channelFuture.channel().closeFuture().addListener(future -> this.destroy());
	}

}
