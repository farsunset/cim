/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.admin.action;


import java.io.IOException;

import org.apache.struts2.ServletActionContext;

import com.farsunset.cim.push.SystemMessagePusher;
import com.farsunset.cim.sdk.server.model.Message;
import com.farsunset.cim.sdk.server.session.DefaultSessionManager;
import com.farsunset.cim.util.ContextHolder;
import com.opensymphony.xwork2.ActionSupport;
public class SessionAction extends ActionSupport {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	 
	 
 
	  
	   
	 public String list()  
	 {  
		 ServletActionContext.getRequest().setAttribute("sessionList", ((DefaultSessionManager) ContextHolder.getBean("CIMSessionManager")).queryAll());
		  
		  return "list";
	}
 
	 public void offline() throws IOException  
	 {  
		 
		 String account = ServletActionContext.getRequest().getParameter("account");
		  Message msg = new Message();
		  msg.setType("999");//强行下线消息类型
		  msg.setReceiver(account);
		  
		 //向客户端 发送消息
	     ContextHolder.getBean(SystemMessagePusher.class).push(msg);
	 
	}
}
