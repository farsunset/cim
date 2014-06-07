package com.farsunset.ichat.cim.handler;


import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.farsunset.cim.nio.constant.CIMConstant;
import com.farsunset.cim.nio.handle.CIMRequestHandler;
import com.farsunset.cim.nio.mutual.Message;
import com.farsunset.cim.nio.mutual.ReplyBody;
import com.farsunset.cim.nio.mutual.SentBody;
import com.farsunset.cim.nio.session.DefaultSessionManager;
import com.farsunset.ichat.common.util.ContextHolder;

/**
 * 绑定账号到服务端实现
 * 
 * @author
 */
public class BindHandler implements CIMRequestHandler {

	protected final Logger logger = Logger.getLogger(BindHandler.class);

	public ReplyBody process(IoSession newSession, SentBody message) {

		
		ReplyBody reply = new ReplyBody();
		DefaultSessionManager sessionManager= ((DefaultSessionManager) ContextHolder.getBean("defaultSessionManager"));
		try { 
			
			String account = message.get("account");
			newSession.setAttribute("channel", message.get("channel"));
			newSession.setAttribute("deviceId", message.get("deviceId"));	
			newSession.setAttribute("device", message.get("device"));	
			
			/**
			 * 由于客户端断线服务端可能会无法获知的情况，客户端重连时，需要关闭旧的连接
			 */
			IoSession oldSession  = sessionManager.getSession(account);
			if(oldSession!=null)
			{
				
				//如果是账号已经在另一台终端登录。则让另一个终端下线
				if(oldSession.getAttribute("deviceId")!=null&&!oldSession.getAttribute("deviceId").equals(newSession.getAttribute("deviceId")))
				{
					oldSession.removeAttribute("account");
					Message msg = new Message();
					msg.setType(CIMConstant.MessageType.TYPE_999);//强行下线消息类型
					msg.setReceiver(account);
					
					oldSession.write(msg);
					oldSession.close(true);
					oldSession = null;
				}
				
			}
			if(oldSession==null)
			{
				//第一次设置心跳时间为登录时间
				newSession.setAttribute("heartbeat", System.currentTimeMillis());
				newSession.setAttribute("loginTime", System.currentTimeMillis());
				
				sessionManager.addSession(account, newSession);
				
				//设置在线状态
				reply.setCode(CIMConstant.ReturnCode.CODE_200);
				
			}
			
			

		} catch (Exception e) {
			reply.setCode(CIMConstant.ReturnCode.CODE_500);
			e.printStackTrace();
		}
		logger.debug("auth :account:" +message.get("account")+"-----------------------------" +reply.getCode());
		return reply;
	}
	
}