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
package com.farsunset.cim.sdk.android.model;

import java.io.Serializable;

/**
 * 消息对象
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型，用户自定义消息类别
     */
    private long id;

    /**
     * 消息类型，用户自定义消息类别
     */
    private String action;
    /**
     * 消息标题
     */
    private String title;
    /**
     * 消息类容，于action 组合为任何类型消息，content 根据 format 可表示为 text,json ,xml数据格式
     */
    private String content;

    /**
     * 消息发送者账号
     */
    private String sender;
    /**
     * 消息发送者接收者
     */
    private String receiver;

    /**
     * content 内容格式
     */
    private String format;

    /**
     * 附加内容 内容
     */
    private String extra;

    private long timestamp;

    public Message() {
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append("#Message#").append("\n");
        buffer.append("id:").append(id).append("\n");
        buffer.append("action:").append(action).append("\n");
        buffer.append("title:").append(title).append("\n");
        buffer.append("content:").append(content).append("\n");
        buffer.append("extra:").append(extra).append("\n");
        buffer.append("sender:").append(sender).append("\n");
        buffer.append("receiver:").append(receiver).append("\n");
        buffer.append("format:").append(format).append("\n");
        buffer.append("timestamp:").append(timestamp);
        return buffer.toString();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isNotEmpty(String txt) {
        return txt != null && txt.trim().length() != 0;
    }

}
