 
package com.farsunset.cim.nio.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/** 
 * 服务端消息 编码解码器， 可以在
 * 关于消息加密与加密， 可在 encoder时进行消息加密，在ClientMessageCodecFactory的 decoder时对消息解密
 * @author 3979434@qq.com
 */
public class ServerMessageCodecFactory implements ProtocolCodecFactory {

    private final ServerMessageEncoder encoder;

    private final ServerMessageDecoder decoder;
 
    public ServerMessageCodecFactory() {
        encoder = new ServerMessageEncoder();
        decoder = new ServerMessageDecoder();
    }
 
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }
 
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

}
