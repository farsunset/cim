package com.farsunset.cim.sdk.server.coder;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.constant.ChannelAttr;
import com.farsunset.cim.sdk.server.model.Pong;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.model.proto.SentBodyProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.io.InputStream;
import java.util.List;

public class WebMessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {

    @Override
    protected void decode(ChannelHandlerContext context, BinaryWebSocketFrame frame, List<Object> list) throws Exception {

        context.channel().attr(ChannelAttr.PING_COUNT).set(null);

        ByteBuf buffer = frame.content();

        byte type = buffer.readByte();

        if (CIMConstant.DATA_TYPE_PONG == type) {
            list.add(Pong.getInstance());
            return;
        }

        InputStream inputStream = new ByteBufInputStream(buffer);

        SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(inputStream);

        SentBody body = new SentBody();
        body.setKey(bodyProto.getKey());
        body.setTimestamp(bodyProto.getTimestamp());
        body.putAll(bodyProto.getDataMap());

        list.add(body);
    }
}
