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

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import com.alibaba.fastjson.JSON;
import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.handler.CIMNioSocketAcceptor;
import com.farsunset.cim.sdk.server.model.HeartbeatResponse;
import com.farsunset.cim.sdk.server.model.SentBody;

/**
 * 服务端接收消息解码
 */
public class WebMessageDecoder extends MessageDecoderAdapter {
	public static final byte MASK = 0x1;// 1000 0000
	public static final byte HAS_EXTEND_DATA = 126;
	public static final byte HAS_EXTEND_DATA_CONTINUE = 127;
	public static final byte PAYLOADLEN = 0x7F;// 0111 1111
	public static final Pattern SEC_KEY_PATTERN = Pattern.compile("^(Sec-WebSocket-Key:).+",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	protected final Logger logger = Logger.getLogger(WebMessageDecoder.class);

	@Override
	public MessageDecoderResult decodable(IoSession arg0, IoBuffer iobuffer) {
		if (iobuffer.remaining() < 2) {
			return NEED_DATA;
		}

		 /**
	     * 原生SDK只会发送2种类型消息 1个心跳类型 另一个是sendbody，报文的第一个字节为消息类型
	     * 如果非原生sdk发出的消息，则认为是websocket发送的消息
	     * websocket发送的消息 第一个字节不可能等于C_H_RS或者SENTBODY
	     */
		byte conetnType = iobuffer.get();
		if (conetnType == CIMConstant.ProtobufType.C_H_RS || conetnType == CIMConstant.ProtobufType.SENTBODY) {
			return NOT_OK;
		}

		byte head = iobuffer.get();// 第二个字节
		byte datalength = (byte) (head & PAYLOADLEN);// 得到第二个字节后七位的值
		int length = 0;
		if (datalength < HAS_EXTEND_DATA) {// 第一种是消息内容少于126存储消息长度
			length = datalength;
		} else if (datalength == HAS_EXTEND_DATA) {// 第二种是消息长度大于等于126且少于UINT16的情况此值为126
			if (iobuffer.remaining() < 2) {
				return NEED_DATA;
			}
			byte[] extended = new byte[2];
			iobuffer.get(extended);
			int shift = 0;
			length = 0;
			for (int i = extended.length - 1; i >= 0; i--) {
				length = length + ((extended[i] & 0xFF) << shift);
				shift += 8;
			}
		} else if (datalength == HAS_EXTEND_DATA_CONTINUE) {// 第三种是消息长度大于UINT16的情况下此值为127
			if (iobuffer.remaining() < 4) {
				return NEED_DATA;
			}
			byte[] extended = new byte[4];
			iobuffer.get(extended);
			int shift = 0;
			length = 0;
			for (int i = extended.length - 1; i >= 0; i--) {
				length = length + ((extended[i] & 0xFF) << shift);
				shift += 8;
			}
		}

		int ismask = head >> 7 & MASK;// 得到第二个字节第一位的值
		if ((ismask == 1 && iobuffer.remaining() < 4 + length) || (ismask == 0 && iobuffer.remaining() < length)) {// 有掩码
			return NEED_DATA;
		}
		return OK;
	}

	@Override
	public MessageDecoderResult decode(IoSession iosession, IoBuffer in, ProtocolDecoderOutput out)throws Exception {
		in.get();
		byte head = in.get();
		byte datalength = (byte) (head & PAYLOADLEN);
		if (datalength < HAS_EXTEND_DATA) {
		} else if (datalength == HAS_EXTEND_DATA) {
			in.get(new byte[2]);
		} else if (datalength == HAS_EXTEND_DATA_CONTINUE) {
			in.get(new byte[4]);
		}

		int ismask = head >> 7 & MASK;
		byte[] data = null;
		if (ismask == 1) {// 有掩码
			// 获取掩码
			byte[] mask = new byte[4];
			in.get(mask);
			
			data = new byte[in.remaining()];
			in.get(data);
			for (int i = 0; i < data.length; i++) {
				// 数据进行异或运算
				data[i] = (byte) (data[i] ^ mask[i % 4]);
			}
			handleSentBodyAndHeartPing(data,out);
		} else {
			data = new byte[in.remaining()];
			in.get(data);
			handleWebsocketHandshake(new String(data, "UTF-8"),out);
		}
		return OK;
	}
	
	private void handleWebsocketHandshake(String message,ProtocolDecoderOutput out) {
		SentBody body = new SentBody();
		body.setKey(CIMNioSocketAcceptor.WEBSOCKET_HANDLER_KEY);
		
		Matcher m = SEC_KEY_PATTERN.matcher(message);
		if (m.find()) {
			String foundstring = m.group();
			body.put("key", foundstring.split(":")[1].trim());
		}
		out.write(body);
	}

	
	public void handleSentBodyAndHeartPing(byte[] data,ProtocolDecoderOutput out) throws UnsupportedEncodingException   {
		String message = new String(data, "UTF-8");
	 
		/**
		 * 只处理心跳响应以及，sentbody消息
		 */
		if (HeartbeatResponse.CMD_HEARTBEAT_RESPONSE.equals(message)) {
			HeartbeatResponse response = HeartbeatResponse.getInstance();
			logger.info(response.toString());
			out.write(response);
		}else if(data.length > 2)
		{
			SentBody body = JSON.parseObject(message, SentBody.class);
			logger.info(body.toString());
			out.write(body);
		}
 
	}
}
