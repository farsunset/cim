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
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.model.proto.SentBodyProto;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Websocket协议消息解码
 */
public class WebMessageDecoder extends ProtocolDecoderAdapter {
	public static final byte MASK = 0x1;
	public static final byte HAS_EXTEND_DATA = 126;
	public static final byte HAS_EXTEND_DATA_CONTINUE = 127;
	public static final byte PAYLOADLEN = 0x7F;
	
	public static final byte TAG_MASK = 0x0F;
    private static final byte OPCODE_BINARY = 0x2;
    private static final byte OPCODE_CLOSE = 0x8;

    
	public static final Pattern SEC_KEY_PATTERN = Pattern.compile("^(Sec-WebSocket-Key:).+",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	public static final Pattern UPGRADE_PATTERN = Pattern.compile("^(Upgrade:).+",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	private static final String SOURCE = "source";
	private static final String WEBSOCKET = "websocket";


	@Override
	public void decode(IoSession iosession, IoBuffer in, ProtocolDecoderOutput out) throws InvalidProtocolBufferException{
		
		/*
		 * 判断是否是握手请求
		 */

		if(isHandShakeRequest(iosession,in)) {
			handleHandshake(iosession,in, out);
			return;
		}

		
		in.mark();
		
		/*
		 * 接下来判断fin标志位是否是1 如果是0 则等待消息接收完成
		 */
		byte tag = in.get();
		int frameFin = tag  > 0 ?  0 : 1;
		if(frameFin == 0) {
			in.reset();
			return;
		}
		
		/*
		 * 获取帧类型，因为使用了protobuf，所以只支持二进制帧 OPCODE_BINARY，以及客户端关闭连接帧通知 OPCODE_CLOSE
		 */
		int frameCode = tag & TAG_MASK;
		
		if(OPCODE_BINARY == frameCode) {
			
			byte head = in.get();
			byte dataLength = (byte) (head & PAYLOADLEN);
			int realLength;
			
			/*
			 *Payload len，7位或者7+16位或者7+64位，表示数据帧中数据大小，这里有好几种情况。
			 *如果值为0-125，那么该值就是payload data的真实长度。
			 *如果值为126，那么该7位后面紧跟着的2个字节就是payload data的真实长度。
			 *如果值为127，那么该7位后面紧跟着的8个字节就是payload data的真实长度。
			 */
			if (dataLength == HAS_EXTEND_DATA) {
				realLength = in.getShort();
			} else if (dataLength == HAS_EXTEND_DATA_CONTINUE) {
				realLength = (int) in.getLong();
			}else {
				realLength = dataLength;
			}

			boolean masked = (head >> 7 & MASK) == 1;
			if (masked) {
				byte[] mask = new byte[4];
				in.get(mask);
				
				byte[] data = new byte[realLength];
				in.get(data);
				for (int i = 0; i < realLength; i++) {
					data[i] = (byte) (data[i] ^ mask[i % 4]);
				}
				
				handleMessage(data,out);
			}
			
		}else if(OPCODE_CLOSE == frameCode) {
        	handleClose(iosession,in);
		}else {
			in.get(new byte[in.remaining()]);
		}
 
	}

	private boolean isHandShakeRequest(IoSession iosession, IoBuffer buffer){
		if(Objects.equals(iosession.getAttribute(SOURCE),WEBSOCKET)) {
			return false;
		}

		buffer.mark();
		String data = new String(buffer.array());
		boolean handShake = getSecWebSocketKey(data) != null && Objects.equals(getUpgradeProtocol(data),WEBSOCKET);
		buffer.reset();
		return handShake;
	}

	
	
	/**
	 * 通过正则获取websocket握手消息中的Sec-WebSocket-Key
	 * @param message
	 * @return
	 */
	private String getSecWebSocketKey(String message) {
		Matcher m = SEC_KEY_PATTERN.matcher(message);
		if (m.find()) {
			return m.group().split(":")[1].trim();
		}
		return null;
	}

	/**
	 * 通过正则获取websocket握手消息中的 Upgrade 值 预期为websocket
	 * @param message
	 * @return
	 */
	private String getUpgradeProtocol(String message) {
		Matcher m = UPGRADE_PATTERN.matcher(message);
		if (m.find()) {
			return m.group().split(":")[1].trim();
		}
		return null;
	}

	
   private void handleHandshake(IoSession iosession, IoBuffer in, ProtocolDecoderOutput out) {
		
		byte[] data = new byte[in.remaining()];
		in.get(data);
		String message = new String(data);
		
		/*
		 * 重要，握手响应之后，删除标志HANDSHAKE_FRAME,并标记当前session的协议为websocket
		 */
	    iosession.setAttribute(SOURCE,WEBSOCKET);
		SentBody body = new SentBody();
		body.setKey(CIMConstant.CLIENT_WEBSOCKET_HANDSHAKE);
		body.setTimestamp(System.currentTimeMillis());
		body.put("key", getSecWebSocketKey(message));
		out.write(body);
	}
   
   private void handleClose(IoSession iosession, IoBuffer in) {
		in.get(new byte[in.remaining()]);
		iosession.closeOnFlush();
	}
   
	public void handleMessage(byte[] data, ProtocolDecoderOutput out) throws InvalidProtocolBufferException{

		SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(data);
		SentBody body = new SentBody();
		body.setKey(bodyProto.getKey());
		body.setTimestamp(bodyProto.getTimestamp());
		body.putAll(bodyProto.getDataMap());
		out.write(body);

	}

}
