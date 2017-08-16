/**
 * Copyright 2013-2033 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.sdk.android.filter;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import android.util.Log;

import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.HeartbeatRequest;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.proto.MessageProto;
import com.farsunset.cim.sdk.android.model.proto.ReplyBodyProto;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 *  客户端消息解码
 */
public class ClientMessageDecoder extends CumulativeProtocolDecoder {

	final static String TAG = ClientMessageDecoder.class.getSimpleName();
	

	@Override
	public boolean doDecode(IoSession iosession, IoBuffer iobuffer,
			ProtocolDecoderOutput out) throws Exception {

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
	    if(message!=null){
	    	out.write(message);
	    }
	    
		return true;
	}

    private Object mappingMessageObject(byte[]  bytes,byte type) throws InvalidProtocolBufferException {
		
    	
    	  
		if(CIMConstant.ProtobufType.S_H_RQ == type)
		{
			HeartbeatRequest request = HeartbeatRequest.getInstance();
			Log.i(TAG,request.toString());
			return request;
		}
		
		if(CIMConstant.ProtobufType.REPLYBODY == type)
		{
			ReplyBodyProto.Model bodyProto = ReplyBodyProto.Model.parseFrom(bytes);
			ReplyBody body = new ReplyBody();
	        body.setKey(bodyProto.getKey());
	        body.setTimestamp(bodyProto.getTimestamp());
	        body.putAll(bodyProto.getDataMap());
	        body.setCode(bodyProto.getCode());
	        body.setMessage(bodyProto.getMessage());
	        
	        Log.i(TAG,body.toString());
	        
	        return body;
		}
		
		if(CIMConstant.ProtobufType.MESSAGE == type)
		{
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
			
			Log.i(TAG,message.toString());
	        return message;
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
