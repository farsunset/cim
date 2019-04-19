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
package com.farsunset.cim.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.farsunset.cim.sdk.server.model.CIMSession;
import com.farsunset.cim.service.CIMSessionService;


/**
 * 单机，内存存储实现
 *
 */
@Service("memorySessionService")
public class MemorySessionServiceImpl implements CIMSessionService {

	private ConcurrentHashMap<String, CIMSession> sessionMap = new ConcurrentHashMap<String, CIMSession>();
	
	@Override
	public void save(CIMSession session) {
		sessionMap.put(session.getAccount(), session);
	}

	@Override
	public CIMSession get(String account) {
		return sessionMap.get(account);
	}
 
	@Override
	public void remove(String account) {
		sessionMap.remove(account);
	}

	@Override
	public List<CIMSession> list() {
		List<CIMSession> onlineList = new ArrayList<>();
		onlineList.addAll(sessionMap.values());
		return onlineList;
	}
	 
}
