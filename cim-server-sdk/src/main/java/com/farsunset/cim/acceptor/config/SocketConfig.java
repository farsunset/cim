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

import com.farsunset.cim.handler.CIMRequestHandler;

import java.time.Duration;


/**
 * TLV协议socket端口配置
 */
public class SocketConfig {

    private static final int DEFAULT_PORT = 23456;

    /**
    TVL协议socket端口
     */
    private Integer port;

    /**
     * socket消息处理器
     */
    private CIMRequestHandler outerRequestHandler;

    /**
    是否启用TVL协议socket
     */
    private boolean enable;

    /**
     长链接写空闲时间触发时间(s)
     心跳发送定时，每当x秒无数据下发写入，触发 服务端-->客户端 心跳事件
     */
    private Duration writeIdle = Duration.ofSeconds(45);

    /**
     长链接读空闲时间触发时间(s)
     心跳响应定时，每当readIdle - writeIdle秒无数据接收，触发心跳超时计数
     */
    private Duration readIdle = Duration.ofSeconds(60);


    /**
     长链接最大允许心跳响应超时次数
     达到该次数则 服务端断开链接
     */
    private int maxPongTimeout = 1;

    public Integer getPort() {
        return port == null || port <= 0 ? DEFAULT_PORT : port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public CIMRequestHandler getOuterRequestHandler() {
        return outerRequestHandler;
    }

    public void setOuterRequestHandler(CIMRequestHandler outerRequestHandler) {
        this.outerRequestHandler = outerRequestHandler;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Duration getWriteIdle() {
        return writeIdle;
    }

    public void setWriteIdle(Duration writeIdle) {
        this.writeIdle = writeIdle;
    }

    public Duration getReadIdle() {
        return readIdle;
    }

    public void setReadIdle(Duration readIdle) {
        this.readIdle = readIdle;
    }

    public int getMaxPongTimeout() {
        return maxPongTimeout;
    }

    public void setMaxPongTimeout(int maxPongTimeout) {
        this.maxPongTimeout = maxPongTimeout;
    }
}
