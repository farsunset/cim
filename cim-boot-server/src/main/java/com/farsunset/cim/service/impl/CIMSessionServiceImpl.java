/*
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


import com.farsunset.cim.repository.SessionRepository;
import com.farsunset.cim.sdk.server.handler.CIMNioSocketAcceptor;
import com.farsunset.cim.sdk.server.model.CIMSession;
import com.farsunset.cim.service.CIMSessionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class CIMSessionServiceImpl implements CIMSessionService {

 	@Resource
 	private CIMNioSocketAcceptor nioSocketAcceptor;

 	@Resource
	private SessionRepository sessionRepository;

	@Override
	public void save(CIMSession session) {
		sessionRepository.save(session);
	}

	/*
	 *
	 * @param account 用户id
	 * @return
	 */
	@Override
	public CIMSession get(String account) {
		 
		 CIMSession session = sessionRepository.get(account);

		 if (session != null){
			 session.setSession(nioSocketAcceptor.getManagedSession(session.getNid()));
		 }

		 return session;
	}
 
	@Override
	public void remove(String account) {
		sessionRepository.remove(account);
	}

	@Override
	public List<CIMSession> list() {
		return sessionRepository.findAll();
	}
	 
}
