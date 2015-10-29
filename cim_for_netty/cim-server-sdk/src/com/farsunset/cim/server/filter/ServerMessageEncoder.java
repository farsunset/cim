package com.farsunset.cim.server.filter;


 
import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


import com.farsunset.cim.server.constant.CIMConstant;

/**
 * 服务端发送消息前编码，可在此加密消息
 *  @author 3979434@qq.com
 *
 */
public class ServerMessageEncoder extends MessageToByteEncoder<Object> {
	protected final Logger logger = Logger.getLogger(ServerMessageEncoder.class.getSimpleName());

	@Override
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		out.writeBytes(message.toString().getBytes(CIMConstant.ENCODE_UTF8));
		out.writeByte(CIMConstant.MESSAGE_SEPARATE);
		logger.debug(message);
	}

	 
	
	 

}
