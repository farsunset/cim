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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.handler.CIMNioSocketAcceptor;
import com.farsunset.cim.sdk.server.model.HeartbeatResponse;
import com.farsunset.cim.sdk.server.model.SentBody;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 服务端接收来自websocket消息解码
 */
public class WebMessageDecoder extends ByteToMessageDecoder {
	public static final byte MASK = 0x1;// 1000 0000
	public static final byte HAS_EXTEND_DATA = 126;
	public static final byte HAS_EXTEND_DATA_CONTINUE = 127;
	public static final byte PAYLOADLEN = 0x7F;// 0111 1111
	public static final Pattern SEC_KEY_PATTERN = Pattern.compile("^(Sec-WebSocket-Key:).+",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	protected final Logger logger = Logger.getLogger(WebMessageDecoder.class);

	@Override
	public void decode(ChannelHandlerContext arg0, ByteBuf iobuffer, List<Object> queue) throws Exception {
		/**
		 * 消息头2位
		 */
		if (iobuffer.readableBytes() < CIMConstant.WS_DATA_HEADER_LENGTH) {
			return;
		}

		iobuffer.markReaderIndex();

		byte head = iobuffer.readByte();// 第二个字节
		byte datalength = (byte) (head & PAYLOADLEN);// 得到第二个字节后七位的值
		int length = 0;
		if (datalength < HAS_EXTEND_DATA) {// 第一种是消息内容少于126存储消息长度
			length = datalength;
		} else if (datalength == HAS_EXTEND_DATA) {// 第二种是消息长度大于等于126且少于UINT16的情况此值为126
			if (iobuffer.readableBytes() < 2) {
				iobuffer.resetReaderIndex();
				return;
			}
			byte[] extended = new byte[2];
			iobuffer.readBytes(extended);
			int shift = 0;
			length = 0;
			for (int i = extended.length - 1; i >= 0; i--) {
				length = length + ((extended[i] & 0xFF) << shift);
				shift += 8;
			}
		} else if (datalength == HAS_EXTEND_DATA_CONTINUE) {// 第三种是消息长度大于UINT16的情况下此值为127
			if (iobuffer.readableBytes() < 4) {
				iobuffer.resetReaderIndex();
				return;
			}
			byte[] extended = new byte[4];
			iobuffer.readBytes(extended);
			int shift = 0;
			length = 0;
			for (int i = extended.length - 1; i >= 0; i--) {
				length = length + ((extended[i] & 0xFF) << shift);
				shift += 8;
			}
		}

		int ismask = head >> 7 & MASK;// 得到第二个字节第一位的值
		if ((ismask == 1 && iobuffer.readableBytes() < 4 + length)
				|| (ismask == 0 && iobuffer.readableBytes() < length)) {// 有掩码
			iobuffer.resetReaderIndex();
			return;
		}
		iobuffer.resetReaderIndex();

		decodeDataBody(iobuffer, queue);
	}

	public void decodeDataBody(ByteBuf iobuffer, List<Object> queue) {
		iobuffer.readByte();
		byte head = iobuffer.readByte();
		byte datalength = (byte) (head & PAYLOADLEN);
		if (datalength < HAS_EXTEND_DATA) {
		} else if (datalength == HAS_EXTEND_DATA) {
			iobuffer.readBytes(new byte[2]);
		} else if (datalength == HAS_EXTEND_DATA_CONTINUE) {
			iobuffer.readBytes(new byte[4]);
		}

		int ismask = head >> 7 & MASK;
		byte[] data = null;
		if (ismask == 1) {// 有掩码
			// 获取掩码
			byte[] mask = new byte[4];
			iobuffer.readBytes(mask);

			data = new byte[iobuffer.readableBytes()];
			iobuffer.readBytes(data);
			for (int i = 0; i < data.length; i++) {
				// 数据进行异或运算
				data[i] = (byte) (data[i] ^ mask[i % 4]);
			}
			handleSentBodyAndHeartPing(data, queue);
		} else {
			data = new byte[iobuffer.readableBytes()];
			iobuffer.readBytes(data);
			handleWebsocketHandshake(data, queue);
		}
	}

	private void handleWebsocketHandshake(byte[] data, List<Object> queue) {
		String message = null;
		try {
			message = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SentBody body = new SentBody();
		body.setKey(CIMNioSocketAcceptor.WEBSOCKET_HANDLER_KEY);

		Matcher m = SEC_KEY_PATTERN.matcher(message);
		if (m.find()) {
			String foundstring = m.group();
			body.put("key", foundstring.split(":")[1].trim());
		}
		queue.add(body);
	}

	public void handleSentBodyAndHeartPing(byte[] data, List<Object> queue) {
		String message = null;
		try {
			message = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * 只处理心跳响应以及，sentbody消息
		 */
		if (HeartbeatResponse.CMD_HEARTBEAT_RESPONSE.equals(message)) {
			HeartbeatResponse response = HeartbeatResponse.getInstance();
			logger.info(response.toString());
			queue.add(response);
		} else if (data.length > 2) {
			SentBody body = JSON.parseObject(message, SentBody.class);
			logger.info(body.toString());
			queue.add(body);
		}

	}
}
