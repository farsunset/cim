package com.farsunset.cim.nio.filter;



import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.farsunset.cim.nio.constant.CIMConstant;
/**
 *  客户端消息发送前进行编码,可在此加密消息
 *  @author 3979434@qq.com
 *
 */
public class ClientMessageEncoder extends ProtocolEncoderAdapter {


	@Override
	public void encode(IoSession iosession, Object message, ProtocolEncoderOutput out) throws Exception {

	        	IoBuffer buff = IoBuffer.allocate(320).setAutoExpand(true);
				//buff.putString( message.toString(), charset.newEncoder());
	        	buff.put(message.toString().getBytes("UTF-8"));
				buff.put(CIMConstant.MESSAGE_SEPARATE);
				buff.flip();
				out.write(buff);
	}
	
	
	 
	
}
