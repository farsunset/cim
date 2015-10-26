/**
 * probject:cim-core
 * @version 1.5.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.server.filter;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.farsunset.cim.server.constant.CIMConstant;

/**
 * 服务端发送消息前编码，可在此加密消息
 */
public class ServerMessageEncoder extends ProtocolEncoderAdapter {

	protected final Logger logger = Logger.getLogger(ServerMessageEncoder.class);
	@Override
	public void encode(IoSession iosession, Object message, ProtocolEncoderOutput out) throws Exception {
		
		IoBuffer buff = IoBuffer.allocate(320).setAutoExpand(true);
		buff.put(message.toString().getBytes(CIMConstant.UTF8));
	    buff.put(CIMConstant.MESSAGE_SEPARATE);
	    buff.flip();
		out.write(buff);
		
		logger.debug(message);
	}
	
	 

}
