/**
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.handler;


import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.handler.CIMRequestHandler;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;
import com.farsunset.cim.service.impl.CIMSessionServiceImpl;


/**
 * 断开连接，清除session
 * 
 */

@Component
public class SessionClosedHandler implements CIMRequestHandler {

	protected final Logger logger = LoggerFactory.getLogger(SessionClosedHandler.class);

	@Resource
	private CIMSessionServiceImpl sessionManager;
	
	public void process(CIMSession ios, SentBody message) {

		Object account = ios.getAttribute(CIMConstant.SESSION_KEY);
		if (account == null) {
			return;
		}

		ios.removeAttribute(CIMConstant.SESSION_KEY);
		sessionManager.remove(account.toString());
	}

}
