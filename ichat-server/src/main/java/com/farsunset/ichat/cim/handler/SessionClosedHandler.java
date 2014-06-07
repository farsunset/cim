 
package com.farsunset.ichat.cim.handler;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.farsunset.cim.nio.handle.CIMRequestHandler;
import com.farsunset.cim.nio.mutual.ReplyBody;
import com.farsunset.cim.nio.mutual.SentBody;
import com.farsunset.cim.nio.session.DefaultSessionManager;
import com.farsunset.ichat.common.util.ContextHolder;

/**
 * 断开连接，清除session
 * 
 * @author
 */
public class SessionClosedHandler implements CIMRequestHandler {

	protected final Logger logger = Logger.getLogger(SessionClosedHandler.class);

	public ReplyBody process(IoSession ios, SentBody message) {

		DefaultSessionManager sessionManager  =  ((DefaultSessionManager) ContextHolder.getBean("defaultSessionManager"));

		if(ios.getAttribute("account")==null)
		{
			return null;
		}
		
	    String account = ios.getAttribute("account").toString();
		
	    sessionManager.removeSession(account);
	    
		return null;
	}
	
 
	
}