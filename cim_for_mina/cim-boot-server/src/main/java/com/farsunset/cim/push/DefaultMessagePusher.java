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

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.farsunset.cim.sdk.server.model.Message;
import com.farsunset.cim.sdk.server.session.CIMSession;
import com.farsunset.cim.sdk.server.session.DefaultSessionManager;
import com.farsunset.cim.service.ApnsService;
import com.farsunset.cim.service.impl.MessageDispatcherImpl;

/**
 * 消息发送实现类
 * 
 */
@Component
public class DefaultMessagePusher implements CIMMessagePusher {

	@Value("${server.host}")
	private String host;
	
	@Autowired
	private DefaultSessionManager sessionManager;
 
	@Autowired
	private MessageDispatcherImpl messageDispatcher;
	

	@Autowired
	private ApnsService apnsService;
	
	
	/**
	 * 向用户发送消息
	 * 
	 * @param msg
	 */
	public void push(Message message) {
		CIMSession session = sessionManager.get(message.getReceiver());

		/**
		 * 服务器集群时，可以在此 判断当前session是否连接于本台服务器，如果是，继续往下走，如果不是，将此消息发往当前session连接的服务器并
		 */
		if (session.isConnected() && !Objects.equals(host, session.getHost())) {
			messageDispatcher.forward(message, session.getHost());
			return;
		}

		/**
		 * 如果是在当前服务器则直接推送
		 */
		if (session.isConnected() && Objects.equals(host, session.getHost())) {
			session.write(message);
			return;
		}
		
		/**
		 * ios设备流程特别处理，如果长链接断开了，并且ApnsAble为打开状态的话优走apns
		 */
		if (Objects.equals(session.getChannel(), CIMSession.CHANNEL_IOS) && Objects.equals(session.getApnsAble(), CIMSession.APNS_ON)) {
			apnsService.push(message, session.getDeviceId());
		}

	}

}
