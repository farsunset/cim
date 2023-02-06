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
package com.farsunset.cim.constant;

/**
 * 数据类型
 */
public enum DataType {

    /**
     * 客户端发送的的心跳响应
     */
    PONG    (0),

    /**
     * 服务端端发送的心跳响应
     */
    PING    (1),

    /**
     * 服务端端发送的消息体
     */
    MESSAGE (2),

    /**
     * 客户端发送的请求体
     */
    SENT    (3),

    /**
     * 服务端端发送的响应体
     */
    REPLY   (4);

    private final byte value;

    DataType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
