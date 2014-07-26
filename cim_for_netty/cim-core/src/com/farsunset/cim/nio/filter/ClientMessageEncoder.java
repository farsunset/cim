package com.farsunset.cim.nio.filter;



import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.farsunset.cim.nio.constant.CIMConstant;

/**
 *  客户端消息发送前进行编码,可在此加密消息
 *  @author 3979434@qq.com
 *
 */
public class ClientMessageEncoder extends OneToOneEncoder {
 
	@Override
	protected Object encode(ChannelHandlerContext arg0, Channel arg1,Object message) throws Exception {
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();//(2)
		buf.writeBytes(message.toString().getBytes());
		buf.writeByte(CIMConstant.MESSAGE_SEPARATE);
		return buf;
	}
	
	
	 
	
}
