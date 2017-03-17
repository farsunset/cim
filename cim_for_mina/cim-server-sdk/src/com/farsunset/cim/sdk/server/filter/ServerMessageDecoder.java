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

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.HeartbeatResponse;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.model.proto.SentBodyProto;
/**
 *  服务端接收消息解码
 */
public class ServerMessageDecoder extends CumulativeProtocolDecoder {
	
	protected final Logger logger = Logger.getLogger(ServerMessageDecoder.class);
	@Override
	public boolean doDecode(IoSession iosession, IoBuffer iobuffer, ProtocolDecoderOutput out) throws Exception {
		/**
	     * 消息头3位
	     */
	    if(iobuffer.remaining() < CIMConstant.DATA_HEADER_LENGTH){
	    	return false;
	    }
	    
	    iobuffer.mark();
	    
	    byte conetnType = iobuffer.get();
	    byte lv =iobuffer.get();//int 低位
	    byte hv =iobuffer.get();//int 高位
	    
	    int conetnLength = getContentLength(lv,hv);
	    
	    
	    //如果消息体没有接收完整，则重置读取，等待下一次重新读取
	    if(conetnLength > iobuffer.remaining()){
	    	iobuffer.reset();
	    	return false;
	    }
	    
	    byte[] dataBytes = new byte[conetnLength]; 
	    iobuffer.get(dataBytes, 0, conetnLength); 
	    
	    Object message = mappingMessageObject(dataBytes,conetnType);
	    if(message != null){
	    	out.write(message);
	    }
	    return true;
	}
	
	public Object mappingMessageObject(byte[] data,byte type) throws Exception
	{
		
		if(CIMConstant.ProtobufType.C_H_RS == type)
		{
			HeartbeatResponse response = HeartbeatResponse.getInstance();
			logger.info(response.toString());
			return response;
		}
		
		if(CIMConstant.ProtobufType.SENTBODY == type)
		{
			SentBodyProto.Model bodyProto = SentBodyProto.Model.parseFrom(data);
	        SentBody body = new SentBody();
	        body.setKey(bodyProto.getKey());
	        body.setTimestamp(bodyProto.getTimestamp());
	        body.putAll(bodyProto.getDataMap());
	        logger.info(body.toString());
	        
	        return body;
		}
        return null;
	}

	/**
	 * 解析消息体长度
	 * @param type
	 * @param length
	 * @return
	 */
	private int getContentLength(byte lv,byte hv){
		 int l =  (lv & 0xff);
		 int h =  (hv & 0xff);
		 return (l| (h <<= 8));
	}
}
