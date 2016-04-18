 /**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.server.handler;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;


/**
 *客户端心跳实现
 * 
 * @author
 */
public class HeartbeatHandler implements CIMRequestHandler {


	public ReplyBody process(CIMSession session, SentBody message) {

		//收到心跳响应，清除发送心跳请求标记
		session.removeAttribute(CIMConstant.RequestKey.CLIENT_HEARTBEAT);
		return null;
	}
	
 
	
}