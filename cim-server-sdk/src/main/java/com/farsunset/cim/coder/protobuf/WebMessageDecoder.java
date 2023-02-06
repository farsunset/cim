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
package com.farsunset.cim.coder.protobuf;

import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.constant.DataType;
import com.farsunset.cim.exception.ReadInvalidTypeException;
import com.farsunset.cim.model.Pong;
import com.farsunset.cim.model.SentBody;
import com.farsunset.cim.model.proto.SentBodyProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class WebMessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {

    @Override
    protected void decode(ChannelHandlerContext context, BinaryWebSocketFrame frame, List<Object> list) throws Exception {

        context.channel().attr(ChannelAttr.PING_COUNT).set(null);

        ByteBuf buffer = frame.content();

        byte type = buffer.readByte();

        if (DataType.PONG.getValue() == type) {
            list.add(Pong.getInstance());
            return;
        }

        if (DataType.SENT.getValue() == type) {
            list.add(getBody(buffer));
            return;
        }

        throw new ReadInvalidTypeException(type);

    }

    protected SentBody getBody(ByteBuf buffer) throws IOException {

        InputStream inputStream = new ByteBufInputStream(buffer);

        SentBodyProto.Model proto = SentBodyProto.Model.parseFrom(inputStream);

        SentBody body = new SentBody();
        body.setData(proto.getDataMap());
        body.setKey(proto.getKey());
        body.setTimestamp(proto.getTimestamp());

        return body;
    }
}
