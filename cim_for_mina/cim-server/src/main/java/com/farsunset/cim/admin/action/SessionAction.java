/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim.admin.action;


import java.io.IOException;

import org.apache.struts2.ServletActionContext;

import com.farsunset.cim.push.SystemMessagePusher;
import com.farsunset.cim.sdk.server.constant.CIMConstant;
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
		  msg.setAction(CIMConstant.MessageAction.ACTION_999);//强行下线消息类型
		  msg.setReceiver(account);
		  
		 //向客户端 发送消息
	     ContextHolder.getBean(SystemMessagePusher.class).push(msg);
	 
	}
}
