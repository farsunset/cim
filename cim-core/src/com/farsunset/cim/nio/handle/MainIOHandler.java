package com.farsunset.cim.nio.handle;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.farsunset.cim.nio.constant.CIMConstant;
import com.farsunset.cim.nio.mutual.ReplyBody;
import com.farsunset.cim.nio.mutual.SentBody;

/**
 *  
 * 客户端请求的入口，所有请求都首先经过它分发处理
 * @author farsunset (3979434@qq.com)
 */
public class MainIOHandler extends IoHandlerAdapter {

	protected final Logger logger = Logger.getLogger(HeartbeatHandler.class);

	private HashMap<String, CIMRequestHandler> handlers = new HashMap<String, CIMRequestHandler>();


	 
	public void sessionCreated(IoSession session) throws Exception {
		logger.warn("sessionCreated()... from "+session.getRemoteAddress().toString());
		
	}

	 
	public void sessionOpened(IoSession session) throws Exception {

	}

	 
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		logger.debug("message: " + message.toString());

		ReplyBody reply = new ReplyBody();
		SentBody body = (SentBody) message;
		String key = body.getKey();

		CIMRequestHandler handler = handlers.get(key);
		if (handler == null) {
			reply.setCode(CIMConstant.ReturnCode.CODE_405);
			reply.setMessage("KEY ["+key+"] 服务端未定义");
		} else {
			reply = handler.process(session, body);
		}
		
        if(reply!=null)
        {
        	reply.setKey(key);
    		session.write(reply);
    		logger.debug("-----------------------process done. reply: " + reply.toString());
        }
		

	}

	/**
	 */
	public void sessionClosed(IoSession session) throws Exception {
		try{
			logger.warn("sessionClosed()... from "+session.getRemoteAddress());
			CIMRequestHandler handler = handlers.get("sessionClosedHander");
			if(handler!=null && session.containsAttribute("account"))
			{
				handler.process(session, null);
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
		logger.warn("sessionIdle()... from "+session.getRemoteAddress().toString());
		if(!session.containsAttribute("account"))
		{
			session.close(true);
		}
	}

	/**
	 */
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error("exceptionCaught()... from "+session.getRemoteAddress());
		logger.error(cause);
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