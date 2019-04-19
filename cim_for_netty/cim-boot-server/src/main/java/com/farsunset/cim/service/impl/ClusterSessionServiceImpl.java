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


import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.farsunset.cim.sdk.server.handler.CIMNioSocketAcceptor;
import com.farsunset.cim.sdk.server.model.CIMSession;
import com.farsunset.cim.service.CIMSessionService;


/**
 * 集群情况下，数据库或者redis存储实现
 * 自行实现存储管理
 *
 */
@Service("clusterSessionService")
public class ClusterSessionServiceImpl implements CIMSessionService {

 	@Resource
 	private CIMNioSocketAcceptor nioSocketAcceptor;
 	
	@Override
	public void save(CIMSession session) {
	}

	@Override
	public CIMSession get(String account) {
		 
		/*
		* CIMSession session = database.getSession(account);
		* session.setIoSession(nioSocketAcceptor.getManagedChannel().get(session.getNid()));
		* return session;
		*/
		
		return null;
	}
 
	@Override
	public void remove(String account) {
	}

	@Override
	public List<CIMSession> list() {
		return null;
	}
	 
}
