/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.server.filter;
import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


import com.farsunset.cim.sdk.server.constant.CIMConstant;

/**
 * 服务端发送消息前编码，可在此加密消息
 */
public class ServerMessageEncoder extends MessageToByteEncoder<Object> {
	protected final Logger logger = Logger.getLogger(ServerMessageEncoder.class.getSimpleName());

	@Override
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		out.writeBytes(message.toString().getBytes(CIMConstant.UTF8));
		out.writeByte(CIMConstant.MESSAGE_SEPARATE);
		logger.debug(message);
	}

	 
	
	 

}
