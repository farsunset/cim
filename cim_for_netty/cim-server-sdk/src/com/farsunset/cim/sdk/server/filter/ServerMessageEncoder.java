/**
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
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

import java.util.Objects;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.HandshakerResponse;
import com.farsunset.cim.sdk.server.model.feature.EncodeFormatable;
import com.farsunset.cim.sdk.server.model.CIMSession;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;

/**
 * 服务端发送消息前编码
 */
public class ServerMessageEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(final ChannelHandlerContext ctx, final Object object, ByteBuf out) throws Exception {
		Object protocol = ctx.channel().attr(AttributeKey.valueOf(CIMSession.PROTOCOL)).get();
 
		/**
		 * websocket的握手响应
		 */
		if (Objects.equals(CIMSession.WEBSOCKET, protocol) && object instanceof HandshakerResponse) {
			out.writeBytes(object.toString().getBytes());
			return;

		}

		/**
		 * websocket的业务数据 
		 */
		if (Objects.equals(CIMSession.WEBSOCKET, protocol) && object instanceof EncodeFormatable) {
			EncodeFormatable data = (EncodeFormatable) object;
			byte[] body = data.getProtobufBody();
			byte[] header = createHeader(data.getDataType(), body.length);

			byte[] protobuf = new byte[body.length + CIMConstant.DATA_HEADER_LENGTH];
			System.arraycopy(header, 0, protobuf, 0, header.length);
			System.arraycopy(body,0, protobuf, header.length, body.length);
			
			byte[] binaryFrame = encodeDataFrame(protobuf);
			out.writeBytes(binaryFrame);

			return;
		}

		/**
		 * 非websocket的数据传输使用Protobuf编码数据格式
		 */
		if (Objects.equals(CIMSession.NATIVEAPP, protocol) && object instanceof EncodeFormatable) {

			EncodeFormatable data = (EncodeFormatable) object;
			byte[] body = data.getProtobufBody();
			byte[] header = createHeader(data.getDataType(), body.length);

			out.writeBytes(header);
			out.writeBytes(body);

		}

	}

	/**
	 * 发送到websocket的数据需要进行相关格式转换 对传入数据进行无掩码转换
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] encodeDataFrame(byte[] data) {
		// 掩码开始位置
		int maskIndex = 2;

		// 计算掩码开始位置
		if (data.length <= 125) {
			maskIndex = 2;
		} else if (data.length > 65536) {
			maskIndex = 10;
		} else if (data.length > 125) {
			maskIndex = 4;
		}

		// 创建返回数据
		byte[] result = new byte[data.length + maskIndex];

		// 开始计算ws-frame
		// frame-fin + frame-rsv1 + frame-rsv2 + frame-rsv3 + frame-opcode
		result[0] = (byte) 0x82; // 0x82 二进制帧 0x80 文本帧

		// frame-masked+frame-payload-length
		// 从第9个字节开始是 1111101=125,掩码是第3-第6个数据
		// 从第9个字节开始是 1111110>=126,掩码是第5-第8个数据
		if (data.length <= 125) {
			result[1] = (byte) (data.length);
		} else if (data.length > 65536) {
			result[1] = 0x7F; // 127
		} else if (data.length > 125) {
			result[1] = 0x7E; // 126
			result[2] = (byte) (data.length >> 8);
			result[3] = (byte) (data.length % 256);
		}

		// 将数据编码放到最后
		for (int i = 0; i < data.length; i++) {
			result[i + maskIndex] = data[i];
		}

		return result;
	}

	private byte[] createHeader(byte type, int length) {
		byte[] header = new byte[CIMConstant.DATA_HEADER_LENGTH];
		header[0] = type;
		header[1] = (byte) (length & 0xff);
		header[2] = (byte) ((length >> 8) & 0xff);
		return header;
	}
}
