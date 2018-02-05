/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.sdk.server.filter;

import java.util.List;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.filter.decoder.AppMessageDecoder;
import com.farsunset.cim.sdk.server.filter.decoder.WebMessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 服务端接收消息路由解码，通过消息类型分发到不同的真正解码器
 */
public class ServerMessageDecoder extends ByteToMessageDecoder {

	private WebMessageDecoder webMessageDecoder;
	private AppMessageDecoder appMessageDecoder;

	public ServerMessageDecoder() {
		webMessageDecoder = new WebMessageDecoder();
		appMessageDecoder = new AppMessageDecoder();

	}

	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf buffer, List<Object> queue) throws Exception {
		buffer.markReaderIndex();
		byte conetnType = buffer.readByte();
		buffer.resetReaderIndex();

		/**
		 * 原生SDK只会发送2种类型消息 1个心跳类型 另一个是sendbody，报文的第一个字节为消息类型,否则才是websocket的消息
		 */
		if (conetnType == CIMConstant.ProtobufType.C_H_RS || conetnType == CIMConstant.ProtobufType.SENTBODY) {
			appMessageDecoder.decode(arg0, buffer, queue);
		} else {
			webMessageDecoder.decode(arg0, buffer, queue);
		}

	}

}
