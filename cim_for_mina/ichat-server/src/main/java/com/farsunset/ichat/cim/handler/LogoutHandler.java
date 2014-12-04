 
package com.farsunset.ichat.cim.handler;

import com.farsunset.cim.nio.constant.CIMConstant;
import com.farsunset.cim.nio.handler.CIMRequestHandler;
import com.farsunset.cim.nio.mutual.ReplyBody;
import com.farsunset.cim.nio.mutual.SentBody;
import com.farsunset.cim.nio.session.CIMSession;
import com.farsunset.cim.nio.session.DefaultSessionManager;
import com.farsunset.ichat.common.util.ContextHolder;
 

/**
 * 退出连接实现
 * 
 *  @author 3979434@qq.com 
 */
public class LogoutHandler implements CIMRequestHandler {

	public ReplyBody process(CIMSession ios, SentBody message) {

		
		DefaultSessionManager sessionManager  =  ((DefaultSessionManager) ContextHolder.getBean("defaultSessionManager"));
		
		String account =ios.getAttribute(CIMConstant.SESSION_KEY).toString();
		ios.removeAttribute(CIMConstant.SESSION_KEY);
		ios.close(true);
	
		sessionManager.removeSession(account);
		 
		return null;
	}
	
	
}