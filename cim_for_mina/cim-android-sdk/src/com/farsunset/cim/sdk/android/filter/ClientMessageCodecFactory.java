/**
 * Copyright 2013-2033 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.sdk.android.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/** 
 * android客户端端消息 编码解码器
 */
public class ClientMessageCodecFactory implements ProtocolCodecFactory {

	/**
	 * 消息编码器
	 */
    private final ClientMessageEncoder encoder;
    /**
	 * 消息解码器
	 */
    private final ClientMessageDecoder decoder;

     
    public ClientMessageCodecFactory() {
        encoder = new ClientMessageEncoder();
        decoder = new ClientMessageDecoder();
    }
    
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

}
