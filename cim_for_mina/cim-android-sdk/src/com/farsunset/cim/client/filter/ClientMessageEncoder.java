/**
 * probject:cim-android-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.client.filter;



import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import android.util.Log;

import com.farsunset.cim.client.constant.CIMConstant;
/**
 *  客户端消息发送前进行编码,可在此加密消息
 */
public class ClientMessageEncoder extends ProtocolEncoderAdapter {

	final static String TAG = ClientMessageEncoder.class.getSimpleName();
	@Override
	public void encode(IoSession iosession, Object message, ProtocolEncoderOutput out) throws Exception {

	        	IoBuffer buff = IoBuffer.allocate(320).setAutoExpand(true);
	        	buff.put(message.toString().getBytes(CIMConstant.UTF8));
				buff.put(CIMConstant.MESSAGE_SEPARATE);
				buff.flip();
				out.write(buff);
				 //打印出收到的消息
				Log.i(TAG,message.toString());
	}
	
	
	 
	
}
