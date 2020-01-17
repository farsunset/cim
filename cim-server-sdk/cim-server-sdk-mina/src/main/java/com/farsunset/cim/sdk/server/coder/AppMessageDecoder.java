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


import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.HeartbeatResponse;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.model.proto.SentBodyProto;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * 原生app发送的消息解码器
 */
public class AppMessageDecoder extends ProtocolDecoderAdapter {


	@Override
	public void decode(IoSession session, IoBuffer iobuffer, ProtocolDecoderOutput out) throws Exception {
		

		if (iobuffer.remaining() < CIMConstant.DATA_HEADER_LENGTH) {
			return;
		}

		iobuffer.mark();

		byte type = iobuffer.get();
		byte lv = iobuffer.get();
		byte hv = iobuffer.get();

		int length = getContentLength(lv, hv);

		/*
		 *发生了断包情况如果消息体没有接收完整，则重置读取，等待下一次重新读取
		 */
		if (length > iobuffer.remaining()) {
			iobuffer.reset();
			return;
		}

		byte[] dataBytes = new byte[length];
		iobuffer.get(dataBytes, 0, length);

		Object message = mappingMessageObject(dataBytes, type);
		out.write(message);
	}

	public Object mappingMessageObject(byte[] data, byte type) throws Exception {

		if (CIMConstant.ProtobufType.C_H_RS == type) {
			return HeartbeatResponse.getInstance();
		}

		SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(data);
		SentBody body = new SentBody();
		body.setKey(bodyProto.getKey());
		body.setTimestamp(bodyProto.getTimestamp());
		body.putAll(bodyProto.getDataMap());
		return body;
	}

	/**
	 * 解析消息体长度
	 * @return
	 */
	private int getContentLength(byte lv, byte hv) {
		int l = (lv & 0xff);
		int h = (hv & 0xff);
		return (l | h << 8);
	}

}
