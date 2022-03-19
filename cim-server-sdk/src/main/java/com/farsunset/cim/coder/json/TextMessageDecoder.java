package com.farsunset.cim.coder.json;

import com.farsunset.cim.constant.CIMConstant;
import com.farsunset.cim.constant.ChannelAttr;
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

        if (protocol.getType() == CIMConstant.DATA_TYPE_PONG){
            list.add(Pong.getInstance());
            return;
        }

        if (protocol.getType() == CIMConstant.DATA_TYPE_SENT){
            SentBody body = OBJECT_MAPPER.readValue(protocol.getContent(), SentBody.class);
            list.add(body);
        }
    }
}