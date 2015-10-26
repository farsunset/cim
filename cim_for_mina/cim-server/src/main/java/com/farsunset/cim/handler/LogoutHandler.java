 
package com.farsunset.cim.handler;

import com.farsunset.cim.server.constant.CIMConstant;
import com.farsunset.cim.server.handler.CIMRequestHandler;
import com.farsunset.cim.server.model.ReplyBody;
import com.farsunset.cim.server.model.SentBody;
import com.farsunset.cim.server.session.CIMSession;
import com.farsunset.cim.server.session.DefaultSessionManager;
import com.farsunset.cim.util.ContextHolder;
 

/**
 * 退出连接实现
 * 
 *  @author 3979434@qq.com 
 */
public class LogoutHandler implements CIMRequestHandler {

	public ReplyBody process(CIMSession ios, SentBody message) {

		
		DefaultSessionManager sessionManager  =  ((DefaultSessionManager) ContextHolder.getBean("CIMSessionManager"));
		
		String account =ios.getAttribute(CIMConstant.SESSION_KEY).toString();
		ios.removeAttribute(CIMConstant.SESSION_KEY);
		ios.close(true);
	
		sessionManager.removeSession(account);
		 
		return null;
	}
	
	
}