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
package com.farsunset.cim.handshake;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.List;

/**
 * websocket客户端握手请求信息
 * 用于在握手阶段鉴权
 */
public class HandshakeEvent {

    private final String uri;

    private final HttpHeaders header;

    public HandshakeEvent(String uri, HttpHeaders header) {
        this.uri = uri;
        this.header = header;
    }

    public String getHeader(String name){
        return header.get(name);
    }

    public List<String> getHeaders(String name){
        return header.getAll(name);
    }

    public Integer getIntHeader(String name){
        return header.getInt(name);
    }

    public String getParameter(String name){
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        List<String> valueList = decoder.parameters().get(name);
        return valueList == null || valueList.isEmpty() ? null : valueList.get(0);
    }

    public List<String> getParameters(String name){
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        return decoder.parameters().get(name);
    }

    public String getUri() {
        return uri;
    }

    public static HandshakeEvent of(WebSocketServerProtocolHandler.HandshakeComplete event){
        return new HandshakeEvent(event.requestUri(),event.requestHeaders());
    }
}
