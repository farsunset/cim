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
package com.farsunset.cim.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.farsunset.cim.service.impl.CIMSessionServiceImpl;
@Controller
@RequestMapping("/console/session")
public class SessionController {

	@Autowired
	private CIMSessionServiceImpl sessionManager;
	
	@RequestMapping(value = "/list.action")
	public String list(Model model) {
		model.addAttribute("sessionList", sessionManager.queryAll());
		return "console/session/manage";
	}

	/*public void offline() throws IOException {

		String account = ServletActionContext.getRequest().getParameter("account");
		Message msg = new Message();
		msg.setAction(CIMConstant.MessageAction.ACTION_999);// 强行下线消息类型
		msg.setReceiver(account);

		// 向客户端 发送消息
		ContextHolder.getBean(SystemMessagePusher.class).push(msg);

	}*/
}
