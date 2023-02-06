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
package com.farsunset.cim.acceptor.config;

import com.farsunset.cim.constant.WebsocketProtocol;
import com.farsunset.cim.handshake.HandshakeEvent;

import java.util.function.Predicate;

/**
 * 基于websocket的服务配置
 */
public class WebsocketConfig extends SocketConfig{

    private static final int DEFAULT_PORT = 34567;

    private static final String DEFAULT_PATH = "/";

    private static final WebsocketProtocol DEFAULT_PROTOCOL = WebsocketProtocol.PROTOBUF;

    /**
     * websocket端点地址
     */
    private String path;

    /**
     * 消息体协议，JSON 或者 Protobuf
     */
    private WebsocketProtocol protocol;

    /**
     * websocket鉴权实现
     */
    private Predicate<HandshakeEvent> handshakePredicate;


    @Override
    public Integer getPort() {
        return super.getPort() == null || super.getPort() <= 0 ? DEFAULT_PORT : super.getPort();
    }

    public String getPath() {
        return path == null ? DEFAULT_PATH : path;
    }

    public WebsocketProtocol getProtocol() {
        return protocol == null ? DEFAULT_PROTOCOL : protocol;
    }

    public Predicate<HandshakeEvent> getHandshakePredicate() {
        return handshakePredicate;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setProtocol(WebsocketProtocol protocol) {
        this.protocol = protocol;
    }


    public void setHandshakePredicate(Predicate<HandshakeEvent> handshakePredicate) {
        this.handshakePredicate = handshakePredicate;
    }

}
