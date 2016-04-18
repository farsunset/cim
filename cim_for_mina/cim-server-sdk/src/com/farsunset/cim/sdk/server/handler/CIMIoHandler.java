/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */   
package com.farsunset.cim.sdk.server.handler;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;

/**
 *  
 * 客户端请求的入口，所有请求都首先经过它分发处理
 */
public class CIMIoHandler extends IoHandlerAdapter {

	protected final Logger logger = Logger.getLogger(CIMIoHandler.class);
    private final static String CIMSESSION_CLOSED_HANDLER_KEY = "client_cimsession_closed";
	private HashMap<String, CIMRequestHandler> handlers = new HashMap<String, CIMRequestHandler>();


	 
	public void sessionCreated(IoSession session) throws Exception {
		logger.debug("sessionCreated()... from "+session.getRemoteAddress());
	}

	 
	public void sessionOpened(IoSession session) throws Exception {
		
	}

	 
	public void messageReceived(IoSession ios, Object message)
			throws Exception {

		/**
		 * flex 客户端安全策略请求，需要返回特定报文
		 */
		if(CIMConstant.FLEX_POLICY_REQUEST.equals(message))
		{
			ios.write(CIMConstant.FLEX_POLICY_RESPONSE);
			return ;
		}
		
		
		if(!(message instanceof SentBody))
		{
			return ;
		}
		CIMSession cimSession =new  CIMSession(ios);
		ReplyBody reply = new ReplyBody();
		SentBody body = (SentBody) message;
		String key = body.getKey();

		CIMRequestHandler handler = handlers.get(key);
		if (handler == null) {
			reply.setCode(CIMConstant.ReturnCode.CODE_405);
			reply.setCode("KEY ["+key+"] 服务端未定义");
		} else {
			reply = handler.process(cimSession, body);
		}
		
        if(reply!=null)
        {
        	reply.setKey(key);
        	cimSession.write(reply);
    		logger.info("-----------------------process done. reply: " + reply.toString());
        }
	}

	/**
	 */
	public void sessionClosed(IoSession ios) throws Exception {
		
		CIMSession cimSession =new  CIMSession(ios);
		try{
			logger.warn("sessionClosed()... from "+cimSession.getRemoteAddress());
			CIMRequestHandler handler = handlers.get(CIMSESSION_CLOSED_HANDLER_KEY);
			if(handler!=null && cimSession.containsAttribute(CIMConstant.SESSION_KEY))
			{
				handler.process(cimSession, null);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 */
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		logger.debug("sessionIdle()... from "+session.getRemoteAddress());
	}

	/**
	 */
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error("exceptionCaught()... from "+session.getRemoteAddress());
		logger.error(cause);
		cause.printStackTrace();
	}

	/**
	 */
	public void messageSent(IoSession session, Object message) throws Exception {
	}


	public HashMap<String, CIMRequestHandler> getHandlers() {
		return handlers;
	}


	public void setHandlers(HashMap<String, CIMRequestHandler> handlers) {
		this.handlers = handlers;
	}
	
	

}