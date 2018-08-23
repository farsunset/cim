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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.handler.CIMNioSocketAcceptor;
import com.farsunset.cim.sdk.server.model.HeartbeatResponse;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.model.proto.SentBodyProto;
import com.farsunset.cim.sdk.server.session.CIMSession;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Websocket协议消息解码
 */
public class WebMessageDecoder extends MessageDecoderAdapter {
	public static final byte MASK = 0x1;// 1000 0000
	public static final byte HAS_EXTEND_DATA = 126;
	public static final byte HAS_EXTEND_DATA_CONTINUE = 127;
	public static final byte PAYLOADLEN = 0x7F;// 0111 1111
	
	public static final byte TAG_MASK = 0x0F;// 0000 1111  > 15

    private static final byte OPCODE_BINARY = 0x2;
    private static final byte OPCODE_CLOSE = 0x8;

    
	public static final Pattern SEC_KEY_PATTERN = Pattern.compile("^(Sec-WebSocket-Key:).+",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	public static final Pattern UPGRADE_PATTERN = Pattern.compile("^(Upgrade:).+",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	public static final String HANDSHAKE_FRAME = "HANDSHAKE_FRAME";


	@Override
	public MessageDecoderResult decodable(IoSession arg0, IoBuffer iobuffer) {
		
		/**
		 * 如果是Websocket客户端，则不作处理，由WebMessageDecoder进行处理
		 */
		if(Objects.equals(arg0.getAttribute(CIMSession.PROTOCOL), CIMSession.WEBSOCKET)) {
			return OK;
		}
		
		if(Objects.equals(arg0.getAttribute(CIMSession.PROTOCOL), CIMSession.NATIVEAPP)) {
			return NOT_OK;
		}
		
		/**
		 * 判断是否是websocket连接发送的数据
		 */
		String data = new String(iobuffer.array());
		boolean handShake = getSecWebSocketKey(data) != null && Objects.equals(getUpgradeProtocol(data),CIMSession.WEBSOCKET);
		if(handShake) {
			arg0.setAttribute(HANDSHAKE_FRAME, handShake);
			return OK;
		} 
		 
		return NOT_OK;
	}

	@Override
	public MessageDecoderResult decode(IoSession iosession, IoBuffer in, ProtocolDecoderOutput out) throws InvalidProtocolBufferException{
		
		/**
		 * 判断是否是握手请求
		 */
		if(Objects.equals(iosession.getAttribute(HANDSHAKE_FRAME), true)) {
			
			handleHandshake(iosession,in, out);

			return OK;
		}
		
		
		in.mark();
		
		/**
		 * 接下来判断fin标志位是否是1 如果是0 则等待消息接收完成
		 */
		byte tag = in.get();
		int frameFin = tag  > 0 ?  0 : 1; //有符号byte 第一位为1则为负数 第一位为0则为正数，以此 判断fin字段是 0 还是 1
		if(frameFin == 0) {
			in.reset();
			return NEED_DATA;
		}
		
		/**
		 * 获取帧类型，因为使用了protobuf，所以只支持二进制帧 OPCODE_BINARY，以及客户端关闭连接帧通知 OPCODE_CLOSE
		 */
		int frameOqcode = tag & TAG_MASK;
		
		if(OPCODE_BINARY == frameOqcode) {
			
			byte head = in.get();
			byte datalength = (byte) (head & PAYLOADLEN);
			int realLength = 0;
			
			/**
			 *Payload len，7位或者7+16位或者7+64位，表示数据帧中数据大小，这里有好几种情况。
			 *如果值为0-125，那么该值就是payload data的真实长度。
			 *如果值为126，那么该7位后面紧跟着的2个字节就是payload data的真实长度。
			 *如果值为127，那么该7位后面紧跟着的8个字节就是payload data的真实长度。
			 */
			if (datalength == HAS_EXTEND_DATA) {
				realLength = in.getShort();
			} else if (datalength == HAS_EXTEND_DATA_CONTINUE) {
				realLength = (int) in.getLong();
			}else {
				realLength = datalength;
			}

			boolean masked = (head >> 7 & MASK) == 1;
			if (masked) {// 有掩码
				// 获取掩码
				byte[] mask = new byte[4];
				in.get(mask);
				
				byte[] data = new byte[realLength];
				in.get(data);
				for (int i = 0; i < realLength; i++) {
					// 数据进行异或运算
					data[i] = (byte) (data[i] ^ mask[i % 4]);
				}
				
				handleMessage(data,out);
			}
			
		}else if(OPCODE_CLOSE == frameOqcode) {
        	handleClose(iosession,in);
		}else {
			//忽略其他类型的消息
			in.get(new byte[in.remaining()]);
		}
 
		return OK;
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
		
		/**
		 * 重要，握手响应之后，删除标志HANDSHAKE_FRAME,并标记当前session的协议为websocket
		 */
		iosession.removeAttribute(HANDSHAKE_FRAME);
		iosession.setAttribute(CIMSession.PROTOCOL,CIMSession.WEBSOCKET);

		SentBody body = new SentBody();
		body.setKey(CIMNioSocketAcceptor.WEBSOCKET_HANDLER_KEY);
		body.setTimestamp(System.currentTimeMillis());
		body.put("key", getSecWebSocketKey(message));
		out.write(body);
	}
   
   private void handleClose(IoSession iosession, IoBuffer in) {
		
		in.get(new byte[in.remaining()]);
		iosession.closeOnFlush();
	}
   
	public void handleMessage(byte[] data, ProtocolDecoderOutput out) throws InvalidProtocolBufferException{
		byte type = data[0];

		/**
		 * 只处理心跳响应以及，sentbody消息
		 */
		if (type == CIMConstant.ProtobufType.C_H_RS) {
			HeartbeatResponse response = HeartbeatResponse.getInstance();
			out.write(response);
		}

		if (type == CIMConstant.ProtobufType.SENTBODY) {
			
			int length = getContentLength( data[1], data[2]);
			byte[] protobuf = new byte[length];
			System.arraycopy(data, CIMConstant.DATA_HEADER_LENGTH, protobuf, 0, length);
			
			SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(protobuf);
			SentBody body = new SentBody();
			body.setKey(bodyProto.getKey());
			body.setTimestamp(bodyProto.getTimestamp());
			body.putAll(bodyProto.getDataMap());
			out.write(body);
			
		}

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
