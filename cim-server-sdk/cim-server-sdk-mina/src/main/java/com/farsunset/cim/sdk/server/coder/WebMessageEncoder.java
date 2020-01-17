/*
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
package com.farsunset.cim.sdk.server.coder;

import com.farsunset.cim.sdk.server.model.HandshakerResponse;
import com.farsunset.cim.sdk.server.model.Transportable;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 服务端发送消息前编码
 */
public class WebMessageEncoder extends ProtocolEncoderAdapter {


	@Override
	public void encode(IoSession iosession, Object object, ProtocolEncoderOutput out) throws Exception {


		Transportable message = (Transportable) object;
		byte[] data = message.getBody();

		/*
		 * websocket的握手响应
		 */
		if (message instanceof HandshakerResponse) {
			IoBuffer buff = IoBuffer.allocate(data.length).setAutoExpand(true);
			buff.put(data);
			buff.flip();
			out.write(buff);

			return;
		}

		byte[] protobuf = new byte[data.length + 1];
		protobuf[0] = message.getType();
		System.arraycopy(data,0, protobuf, 1, data.length);

		byte[] binaryFrame = encodeDataFrame(protobuf);
		IoBuffer buffer = IoBuffer.allocate(binaryFrame.length);
		buffer.put(binaryFrame);
		buffer.flip();
		out.write(buffer);


	}

	/**
	 * 发送到websocket的数据需要进行相关格式转换 对传入数据进行无掩码转换
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] encodeDataFrame(byte[] data) {
		// 掩码开始位置
		int maskIndex;

		// 计算掩码开始位置
		if (data.length <= 125) {
			maskIndex = 2;
		} else if (data.length > 65536) {
			maskIndex = 10;
		} else {
			maskIndex = 4;
		}

		// 创建返回数据
		byte[] result = new byte[data.length + maskIndex];

		/*
		0x82 二进制帧   0x80 文本帧
		 */
		result[0] = (byte) 0x82;

		if (data.length <= 125) {
			result[1] = (byte) (data.length);
		} else if (data.length > 65536) {
			result[1] = 0x7F;
		} else {
			result[1] = 0x7E;
			result[2] = (byte) (data.length >> 8);
			result[3] = (byte) (data.length % 256);
		}

		// 将数据编码放到最后
		System.arraycopy(data, 0, result, maskIndex, data.length);

		return result;
	}

}
