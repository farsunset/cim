/**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.client.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/** 
 * android客户端端消息 编码解码器， 可以在
 * 关于消息加密与加密， 可在 encoder时进行消息加密，在ServerMessageCodecFactory的 decoder时对消息解密
 */
public class ClientMessageCodecFactory implements ProtocolCodecFactory {

    private final ClientMessageEncoder encoder;

    private final ClientMessageDecoder decoder;
 
    public ClientMessageCodecFactory() {
        encoder = new ClientMessageEncoder();
        decoder = new ClientMessageDecoder();
    }

   
    public ProtocolEncoder getEncoder(IoSession session) {
        return encoder;
    }

   
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

}
