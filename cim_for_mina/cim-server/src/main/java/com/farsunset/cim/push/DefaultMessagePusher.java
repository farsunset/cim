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
package com.farsunset.cim.push;

import com.farsunset.cim.sdk.server.model.Message;
import com.farsunset.cim.sdk.server.session.CIMSession;
import com.farsunset.cim.sdk.server.session.DefaultSessionManager;

/**
 * 消息发送实现类
 * 
 */
public class DefaultMessagePusher implements CIMMessagePusher {

	private DefaultSessionManager sessionManager;

	public void setSessionManager(DefaultSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	/**
	 * 向用户发送消息
	 * 
	 * @param msg
	 */
	public void push(Message msg) {
		CIMSession session = sessionManager.get(msg.getReceiver());

		/*
		 * 服务器集群时，可以在此 判断当前session是否连接于本台服务器，如果是，继续往下走，如果不是，将此消息发往当前session连接的服务器并
		 * return if(session!=null&&!session.isLocalhost()){//判断当前session是否连接于本台服务器，如不是
		 * //发往目标服务器处理
		 * MessageDispatcher.execute(MessageUtil.transform(msg),session.getHost());
		 * return; }
		 */

		if (session != null && session.isConnected()) {

			session.write(msg);
			return;
		}

		// 如果用户标示了APNS ON 说明这是ios设备需要使用apns发送
		if (session != null && session.getApnsAble() == CIMSession.APNS_ON) {

			apnsPush(1, msg.getContent(), session.getDeviceId());
		}

	}

	/**
	 * 引入javaapns相关jar
	 * 
	 * @param badge
	 * @param content
	 * @param token
	 */
	private void apnsPush(int badge, String content, String token) {
		/*
		 * String password = "password"; String keystore = "p12 文件 绝对路径"; boolean
		 * isDebug = true;
		 * 
		 * PushNotificationPayload payload = PushNotificationPayload.complex(); try {
		 * payload.addAlert(content); payload.addBadge(1); payload.addSound("default");
		 * Push.payload(payload, keystore, password, isDebug, token);
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 */
	}

}
