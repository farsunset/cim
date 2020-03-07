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
package com.farsunset.cim.sdk.client;

import com.farsunset.cim.sdk.client.model.Intent;
import com.farsunset.cim.sdk.client.model.SentBody;

/**
 * 与服务端连接服务
 */
public class CIMPushService {

	private final CIMConnectorManager manager;

	private static CIMPushService service;

	public static CIMPushService getInstance() {
		if (service == null) {
			service = new CIMPushService();
		}
		return service;
	}

	public CIMPushService() {
		manager = CIMConnectorManager.getManager();
	}

	public void onStartCommand(Intent intent) {

		String action = intent == null ? CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE : intent.getAction();

		if (CIMPushManager.ACTION_CREATE_CIM_CONNECTION.equals(action)) {
			String host = CIMCacheManager.getInstance().getString(CIMCacheManager.KEY_CIM_SERVER_HOST);
			int port = CIMCacheManager.getInstance().getInt(CIMCacheManager.KEY_CIM_SERVER_PORT);
			manager.connect(host, port);
		}

		if (CIMPushManager.ACTION_SEND_REQUEST_BODY.equals(action)) {
			manager.send((SentBody) intent.getExtra(SentBody.class.getName()));
		}

		if (CIMPushManager.ACTION_CLOSE_CIM_CONNECTION.equals(action)) {
			manager.closeSession();
		}

		if (CIMPushManager.ACTION_DESTROY.equals(action)) {
			manager.destroy();
		}

		if (CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action) && !manager.isConnected()) {

			String host = CIMCacheManager.getInstance().getString(CIMCacheManager.KEY_CIM_SERVER_HOST);
			int port = CIMCacheManager.getInstance().getInt(CIMCacheManager.KEY_CIM_SERVER_PORT);
			manager.connect(host, port);
		}
	}

}
