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
package com.farsunset.cim.sdk.server.filter.decoder;

import java.util.List;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.model.proto.SentBodyProto;
import com.farsunset.cim.sdk.server.session.CIMSession;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

/**
 * 服务端接收来自应用的消息解码
 */
public class AppMessageDecoder extends ByteToMessageDecoder {

	@Override
	public void decode(ChannelHandlerContext arg0, ByteBuf buffer, List<Object> queue) throws Exception {

		/**
		 * 消息头3位
		 */
		if (buffer.readableBytes() < CIMConstant.DATA_HEADER_LENGTH) {
			return;
		}

		buffer.markReaderIndex();

		byte conetnType = buffer.readByte();

		byte lv = buffer.readByte();// int 低位
		byte hv = buffer.readByte();// int 高位

		int conetnLength = getContentLength(lv, hv);

		// 如果消息体没有接收完整，则重置读取，等待下一次重新读取
		if (conetnLength <= buffer.readableBytes()) {
			byte[] dataBytes = new byte[conetnLength];
			buffer.readBytes(dataBytes);

			Object message = mappingMessageObject(dataBytes, conetnType);
			if (message != null) {
				arg0.channel().attr(AttributeKey.valueOf(CIMSession.PROTOCOL)).set(CIMSession.NATIVEAPP);
				queue.add(message);
				return;
			}
		}

		buffer.resetReaderIndex();
	}

	public Object mappingMessageObject(byte[] data, byte type) throws Exception {

		if (CIMConstant.ProtobufType.C_H_RS == type) {
			SentBody body = new SentBody();
			body.setKey(CIMConstant.CLIENT_HEARTBEAT);
			body.setTimestamp(System.currentTimeMillis());
			return body;
		}

		if (CIMConstant.ProtobufType.SENTBODY == type) {
			SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(data);
			SentBody body = new SentBody();
			body.setKey(bodyProto.getKey());
			body.setTimestamp(bodyProto.getTimestamp());
			body.putAll(bodyProto.getDataMap());

			return body;
		}
		return null;
	}

	/**
	 * 解析消息体长度
	 * 
	 * @param type
	 * @param length
	 * @return
	 */
	private int getContentLength(byte lv, byte hv) {
		int l = (lv & 0xff);
		int h = (hv & 0xff);
		return (l | (h <<= 8));
	}
}
