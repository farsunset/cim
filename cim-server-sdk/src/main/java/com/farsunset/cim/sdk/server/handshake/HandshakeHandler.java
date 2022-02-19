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
package com.farsunset.cim.sdk.server.handshake;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.function.Predicate;

/**
 * WS握手时鉴权
 */
@ChannelHandler.Sharable
public class HandshakeHandler extends ChannelInboundHandlerAdapter {

    /*
    客户端接收到的CloseEvent事件类型
    客户端可通过该code判断是握手鉴权失败
     */
    private static final int UNAUTHORIZED_CODE = 4001;

    private final Predicate<HandshakeEvent> handshakePredicate;

    public HandshakeHandler(Predicate<HandshakeEvent> handshakePredicate) {
        this.handshakePredicate = handshakePredicate;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        super.userEventTriggered(ctx, evt);

        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            doAuthentication(ctx, (WebSocketServerProtocolHandler.HandshakeComplete) evt);
        }

    }

    private void doAuthentication(ChannelHandlerContext context, WebSocketServerProtocolHandler.HandshakeComplete event) {

        if (handshakePredicate == null) {
            return;
        }

        /*
         * 鉴权不通过，关闭链接
         */
        if (!handshakePredicate.test(HandshakeEvent.of(event))) {
            context.channel().writeAndFlush(new CloseWebSocketFrame(UNAUTHORIZED_CODE,HttpResponseStatus.UNAUTHORIZED.reasonPhrase())).addListener(ChannelFutureListener.CLOSE);
        }
    }
}