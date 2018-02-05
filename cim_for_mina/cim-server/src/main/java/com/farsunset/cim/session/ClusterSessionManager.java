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
package com.farsunset.cim.session;

import java.util.List;
import com.farsunset.cim.sdk.server.session.CIMSession;
import com.farsunset.cim.sdk.server.session.SessionManager;

/**
 * 集群 session管理实现示例， 各位可以自行实现 AbstractSessionManager接口来实现自己的 session管理 服务器集群时
 * 须要将CIMSession 信息存入数据库或者nosql 等 第三方存储空间中，便于所有服务器都可以访问
 */
public class ClusterSessionManager implements SessionManager {

	public CIMSession get(String account) {

		// 这里查询数据库
		/*
		 * CIMSession session = database.getSession(account);
		 * session.setIoSession(ContextHolder.getBean(CIMNioSocketAcceptor.class).
		 * getManagedSessions().get(session.getNid())); return session;
		 */
		return null;
	}

	@Override
	public List<CIMSession> queryAll() {
		return null;
	}

	@Override
	public void remove(String account) {

	}

	@Override
	public void update(CIMSession session) {

	}

	@Override
	public void add(CIMSession arg0) {

	}

}
