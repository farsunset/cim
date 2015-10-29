package com.farsunset.cim.client.filter;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import android.util.Log;

import com.farsunset.cim.client.constant.CIMConstant;

/**
 *  客户端消息发送前进行编码,可在此加密消息
 *  @author 3979434@qq.com
 *
 */
public class ClientMessageEncoder extends MessageToByteEncoder<Object>  {
 
	@Override
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		out.writeBytes(message.toString().getBytes(CIMConstant.UTF8));
		out.writeByte(CIMConstant.MESSAGE_SEPARATE);
		Log.i(ClientMessageEncoder.class.getSimpleName(), message.toString());
	}
	
	
	 
	
}
