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
package com.farsunset.cim.sdk.android.coder;


import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.Ping;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.proto.MessageProto;
import com.farsunset.cim.sdk.android.model.proto.ReplyBodyProto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientMessageDecoder {

    /**
     *
     * @param headerBuffer 读取到的消息头
     * @param socketChannel
     * @return
     */
    public Object doDecode(ByteBuffer headerBuffer , SocketChannel socketChannel) throws IOException {

        headerBuffer.position(0);

        byte type = headerBuffer.get();
        byte lv = headerBuffer.get();
        byte hv = headerBuffer.get();

        headerBuffer.clear();

        /*
         * 先通过消息头拿到消息的长度，然后进行定长读取
         * 解决消息的断包和粘包情况
         */

        int dataLength = getContentLength(lv, hv);

        ByteBuffer bodyBuffer = ByteBuffer.allocate(dataLength);

        /*
         * 如果读取的消息长度不够，则进行等待后续消息到来
         * 当读取的消息长度(bodyBuffer.position() == dataLength)时意味着一个完整的消息已经接收完成
         */
        do {
            socketChannel.read(bodyBuffer);
        } while (bodyBuffer.position() != dataLength);


        /*
         消息读取完成后，通过type来解析成对应的消息体
         */
        if (CIMConstant.ProtobufType.S_H_RQ == type) {
            return Ping.getInstance();
        }

        if (CIMConstant.ProtobufType.REPLY_BODY == type) {
            ReplyBodyProto.Model bodyProto = ReplyBodyProto.Model.parseFrom(bodyBuffer.array());
            ReplyBody body = new ReplyBody();
            body.setKey(bodyProto.getKey());
            body.setTimestamp(bodyProto.getTimestamp());
            body.putAll(bodyProto.getDataMap());
            body.setCode(bodyProto.getCode());
            body.setMessage(bodyProto.getMessage());
            return body;
        }

        MessageProto.Model bodyProto = MessageProto.Model.parseFrom(bodyBuffer.array());
        Message message = new Message();
        message.setId(bodyProto.getId());
        message.setAction(bodyProto.getAction());
        message.setContent(bodyProto.getContent());
        message.setSender(bodyProto.getSender());
        message.setReceiver(bodyProto.getReceiver());
        message.setTitle(bodyProto.getTitle());
        message.setExtra(bodyProto.getExtra());
        message.setTimestamp(bodyProto.getTimestamp());
        message.setFormat(bodyProto.getFormat());
        return message;
    }

    /**
     * 解析消息体长度
     */
    private int getContentLength(byte lv, byte hv) {
        int l = (lv & 0xff);
        int h = (hv & 0xff);
        return (l | h << 8);
    }

}
