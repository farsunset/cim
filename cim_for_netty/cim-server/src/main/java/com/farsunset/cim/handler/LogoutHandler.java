/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.handler;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.handler.CIMRequestHandler;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;
import com.farsunset.cim.sdk.server.session.DefaultSessionManager;
import com.farsunset.cim.util.ContextHolder;
 

/**
 * 退出连接实现
 */
public class LogoutHandler implements CIMRequestHandler {

	public ReplyBody process(CIMSession ios, SentBody message) {

		
		DefaultSessionManager sessionManager  =  ((DefaultSessionManager) ContextHolder.getBean("CIMSessionManager"));
		
		String account =ios.getAttribute(CIMConstant.SESSION_KEY).toString();
		ios.removeAttribute(CIMConstant.SESSION_KEY);
		ios.closeNow();
		sessionManager.remove(account);
		 
		return null;
	}
	
	
}