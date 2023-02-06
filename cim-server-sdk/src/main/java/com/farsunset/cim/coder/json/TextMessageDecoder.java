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
package com.farsunset.cim.coder.json;

import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.constant.DataType;
import com.farsunset.cim.exception.ReadInvalidTypeException;
import com.farsunset.cim.model.Pong;
import com.farsunset.cim.model.SentBody;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * websocket 协议 json消息体解码
 */
public class TextMessageDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {

    private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    @Override
    protected void decode(ChannelHandlerContext context, TextWebSocketFrame frame, List<Object> list) throws Exception {

        context.channel().attr(ChannelAttr.PING_COUNT).set(null);

        String text = frame.text();

        TransmitBody protocol = OBJECT_MAPPER.readValue(text, TransmitBody.class);

        if (protocol.getType() == DataType.PONG.getValue()){
            list.add(Pong.getInstance());
            return;
        }

        if (protocol.getType() == DataType.SENT.getValue()){
            SentBody body = OBJECT_MAPPER.readValue(protocol.getContent(), SentBody.class);
            list.add(body);
            return;
        }

        throw new ReadInvalidTypeException(protocol.getType());
    }
}