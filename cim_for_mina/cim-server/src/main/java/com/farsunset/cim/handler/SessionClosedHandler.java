/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.handler;

import org.apache.log4j.Logger;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.handler.CIMRequestHandler;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;
import com.farsunset.cim.sdk.server.session.DefaultSessionManager;
import com.farsunset.cim.util.ContextHolder;

/**
 * 断开连接，清除session
 * 
 */
public class SessionClosedHandler implements CIMRequestHandler {

	protected final Logger logger = Logger.getLogger(SessionClosedHandler.class);

	public ReplyBody process(CIMSession ios, SentBody message) {

        DefaultSessionManager sessionManager  =  ((DefaultSessionManager) ContextHolder.getBean("CIMSessionManager"));
		
		String account =ios.getAttribute(CIMConstant.SESSION_KEY).toString();
		ios.removeAttribute(CIMConstant.SESSION_KEY);
		ios.closeNow();
		sessionManager.remove(account);
	    
		return null;
	}
	
 
	
}