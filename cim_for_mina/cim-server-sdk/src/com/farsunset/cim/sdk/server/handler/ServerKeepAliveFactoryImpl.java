/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.server.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import com.farsunset.cim.sdk.server.constant.CIMConstant;

public class ServerKeepAliveFactoryImpl implements KeepAliveMessageFactory {
	@Override
	public Object getRequest(IoSession arg0) {
		return CIMConstant.CMD_HEARTBEAT_REQUEST;
	}

	@Override
	public Object getResponse(IoSession arg0, Object arg1) {
		return null;
	}

	@Override
	public boolean isRequest(IoSession arg0, Object arg1) {
		return false;
	}

	@Override
	public boolean isResponse(IoSession arg0, Object arg1) {
		return CIMConstant.CMD_HEARTBEAT_RESPONSE.equalsIgnoreCase(arg1.toString());
	}

}
