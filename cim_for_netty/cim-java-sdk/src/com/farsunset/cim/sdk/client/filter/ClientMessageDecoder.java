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
package com.farsunset.cim.sdk.client.filter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.farsunset.cim.sdk.client.constant.CIMConstant;
import com.farsunset.cim.sdk.client.model.HeartbeatRequest;
import com.farsunset.cim.sdk.client.model.Message;
import com.farsunset.cim.sdk.client.model.ReplyBody;
import com.farsunset.cim.sdk.model.proto.MessageProto;
import com.farsunset.cim.sdk.model.proto.ReplyBodyProto;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 客户端消息解码
 */
public class ClientMessageDecoder extends ByteToMessageDecoder {
	protected final Logger logger = LoggerFactory.getLogger(ClientMessageDecoder.class.getSimpleName());

	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf buffer, List<Object> queue) throws Exception {

		/**
		 * 消息头3位
		 */
		if (buffer.readableBytes() < CIMConstant.DATA_HEADER_LENGTH) {
			return;
		}

		buffer.markReaderIndex();

		buffer.markReaderIndex();

		byte conetnType = buffer.readByte();

		byte lv = buffer.readByte();// int 低位
		byte hv = buffer.readByte();// int 高位

		int conetnLength = getContentLength(lv, hv);

		// 如果消息体没有接收完整，则重置读取，等待下一次重新读取
		if (conetnLength > buffer.readableBytes()) {
			buffer.resetReaderIndex();
			return;
		}

		byte[] dataBytes = new byte[conetnLength];
		buffer.readBytes(dataBytes);

		Object message = mappingMessageObject(dataBytes, conetnType);

		if (message != null) {
			queue.add(message);
		}

	}

	private Object mappingMessageObject(byte[] bytes, byte type) throws InvalidProtocolBufferException {

		if (CIMConstant.ProtobufType.S_H_RQ == type) {
			HeartbeatRequest request = HeartbeatRequest.getInstance();
			logger.info(request.toString());
			return request;
		}

		if (CIMConstant.ProtobufType.REPLYBODY == type) {
			ReplyBodyProto.Model bodyProto = ReplyBodyProto.Model.parseFrom(bytes);
			ReplyBody body = new ReplyBody();
			body.setKey(bodyProto.getKey());
			body.setTimestamp(bodyProto.getTimestamp());
			body.putAll(bodyProto.getDataMap());
			body.setCode(bodyProto.getCode());
			body.setMessage(bodyProto.getMessage());

			logger.info(body.toString());

			return body;
		}

		if (CIMConstant.ProtobufType.MESSAGE == type) {
			MessageProto.Model bodyProto = MessageProto.Model.parseFrom(bytes);
			Message message = new Message();
			message.setMid(bodyProto.getMid());
			message.setAction(bodyProto.getAction());
			message.setContent(bodyProto.getContent());
			message.setSender(bodyProto.getSender());
			message.setReceiver(bodyProto.getReceiver());
			message.setTitle(bodyProto.getTitle());
			message.setExtra(bodyProto.getExtra());
			message.setTimestamp(bodyProto.getTimestamp());
			message.setFormat(bodyProto.getFormat());

			logger.info(message.toString());
			return message;
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
