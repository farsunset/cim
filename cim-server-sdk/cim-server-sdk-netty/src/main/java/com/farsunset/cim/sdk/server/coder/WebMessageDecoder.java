package com.farsunset.cim.sdk.server.coder;

import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.model.proto.SentBodyProto;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class WebMessageDecoder extends SimpleChannelInboundHandler<Object> {

    private final static String URI = "ws://localhost:%d";

    private static final ConcurrentHashMap<String, WebSocketServerHandshaker> handShakerMap = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(WebMessageDecoder.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws InvalidProtocolBufferException {

        if (msg instanceof FullHttpRequest) {
            handleHandshakeRequest(ctx, (FullHttpRequest) msg);
        }
        if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleHandshakeRequest(ChannelHandlerContext ctx, FullHttpRequest req) {

        int port = ((InetSocketAddress)ctx.channel().localAddress()).getPort();

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(String.format(URI,port), null, false);

        WebSocketServerHandshaker handShaker = wsFactory.newHandshaker(req);

        handShakerMap.put(ctx.channel().id().asLongText(), handShaker);

        handShaker.handshake(ctx.channel(), req);
    }



    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws InvalidProtocolBufferException {

        if (frame instanceof CloseWebSocketFrame) {
            handlerCloseWebSocketFrame(ctx, (CloseWebSocketFrame) frame);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            handlerPingWebSocketFrame(ctx, (PingWebSocketFrame) frame);
            return;
        }

        handlerBinaryWebSocketFrame(ctx, (BinaryWebSocketFrame) frame);
    }

    private void handlerCloseWebSocketFrame(ChannelHandlerContext ctx, CloseWebSocketFrame frame){
        WebSocketServerHandshaker handShaker = handShakerMap.get(ctx.channel().id().asLongText());
        handShaker.close(ctx.channel(), frame.retain());
    }

    private void handlerPingWebSocketFrame(ChannelHandlerContext ctx, PingWebSocketFrame frame){
        ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
    }

    private void handlerBinaryWebSocketFrame(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) throws InvalidProtocolBufferException {
        byte[] data = new byte[frame.content().readableBytes()];
        frame.content().readBytes(data);

        SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(data);
        SentBody body = new SentBody();
        body.setKey(bodyProto.getKey());
        body.setTimestamp(bodyProto.getTimestamp());
        body.putAll(bodyProto.getDataMap());
        ctx.fireChannelRead(body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();
        LOGGER.warn("Exception caught",cause);
    }
}
