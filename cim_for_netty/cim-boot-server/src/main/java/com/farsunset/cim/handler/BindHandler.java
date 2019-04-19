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

import java.util.Objects;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.farsunset.cim.push.CIMMessagePusher;
import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.handler.CIMRequestHandler;
import com.farsunset.cim.sdk.server.model.Message;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.model.CIMSession;
import com.farsunset.cim.service.CIMSessionService;


/**
 * 账号绑定实现
 * 
 */

@Component
public class BindHandler implements CIMRequestHandler {

	protected final Logger logger = LoggerFactory.getLogger(BindHandler.class);

	@Resource
	private CIMSessionService memorySessionService;

	@Value("${server.host}")
	private String host;
	
	@Resource
	private CIMMessagePusher defaultMessagePusher;
	
	public void process(CIMSession newSession, SentBody body) {

		ReplyBody reply = new ReplyBody();
		reply.setKey(body.getKey());
		reply.setCode(CIMConstant.ReturnCode.CODE_200);
		reply.setTimestamp(System.currentTimeMillis());
		
		try {

			String account = body.get("account");
			newSession.setAccount(account);
			newSession.setDeviceId(body.get("deviceId"));
			newSession.setHost(host);
			newSession.setChannel(body.get("channel"));
			newSession.setDeviceModel(body.get("device"));
			newSession.setClientVersion(body.get("version"));
			newSession.setSystemVersion(body.get("osVersion"));
			newSession.setBindTime(System.currentTimeMillis());
			/*
			 * 由于客户端断线服务端可能会无法获知的情况，客户端重连时，需要关闭旧的连接
			 */
			CIMSession oldSession = memorySessionService.get(account);

			/*
			 * 如果是账号已经在另一台终端登录。则让另一个终端下线
			 */

			if (oldSession != null && fromOtherDevice(newSession,oldSession) && oldSession.isConnected()) {
				sendForceOfflineMessage(oldSession, account, newSession.getDeviceModel());
			}
			
			/*
			 * 有可能是同一个设备重复连接，则关闭旧的链接，这种情况一般是客户端断网，联网又重新链接上来，之前的旧链接没有来得及通过心跳机制关闭，在这里手动关闭
			 * 条件1，连接来自是同一个设备
			 * 条件2.2个连接都是同一台服务器
			 */
			
			if (oldSession != null && !fromOtherDevice(newSession,oldSession) && Objects.equals(oldSession.getHost(),host)) {
				closeQuietly(oldSession);
			}

			memorySessionService.save(newSession);
			

		} catch (Exception e) {
			reply.setCode(CIMConstant.ReturnCode.CODE_500);
			logger.error(e.getMessage());
		}
	 
		newSession.write(reply);
	}

	private boolean fromOtherDevice(CIMSession oldSession ,CIMSession newSession) {
			
			return !Objects.equals(oldSession.getDeviceId(), newSession.getDeviceId());
	}
	 
	private void sendForceOfflineMessage(CIMSession oldSession, String account, String deviceModel) {

		Message msg = new Message();
		msg.setAction(CIMConstant.MessageAction.ACTION_999);// 强行下线消息类型
		msg.setReceiver(account);
		msg.setSender("system");
		msg.setContent(deviceModel);
		msg.setId(System.currentTimeMillis());
		
		defaultMessagePusher.push(msg);
		
		closeQuietly(oldSession);

	}

	// 不同设备同一账号登录时关闭旧的连接
	private void closeQuietly(CIMSession oldSession) {
		if (oldSession.isConnected() && Objects.equals(host, oldSession.getHost())) {
			oldSession.setAttribute(CIMConstant.KEY_QUIETLY_CLOSE,true);
			oldSession.closeOnFlush();
		}
	}

}
