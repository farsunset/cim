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
package com.farsunset.cim.handler;

import java.net.InetAddress;
import org.apache.log4j.Logger;

import com.farsunset.cim.push.SystemMessagePusher;
import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.handler.CIMRequestHandler;
import com.farsunset.cim.sdk.server.model.Message;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;
import com.farsunset.cim.sdk.server.session.DefaultSessionManager;
import com.farsunset.cim.util.ContextHolder;
import com.farsunset.cim.util.StringUtil;

/**
 * 账号绑定实现
 * 
 */
public class BindHandler implements CIMRequestHandler {

	protected final Logger logger = Logger.getLogger(BindHandler.class);

	public ReplyBody process(CIMSession newSession, SentBody message) {
		DefaultSessionManager sessionManager = ((DefaultSessionManager) ContextHolder.getBean("CIMSessionManager"));

		ReplyBody reply = new ReplyBody();
		reply.setCode(CIMConstant.ReturnCode.CODE_200);
		try {

			String account = message.get("account");
			newSession.setGid(StringUtil.getUUID());
			newSession.setAccount(account);
			newSession.setDeviceId(message.get("deviceId"));
			newSession.setHost(InetAddress.getLocalHost().getHostAddress());
			newSession.setChannel(message.get("channel"));
			newSession.setDeviceModel(message.get("device"));
			newSession.setClientVersion(message.get("version"));
			newSession.setSystemVersion(message.get("osVersion"));
			newSession.setBindTime(System.currentTimeMillis());
			newSession.setPackageName(message.get("packageName"));
			/**
			 * 由于客户端断线服务端可能会无法获知的情况，客户端重连时，需要关闭旧的连接
			 */
			CIMSession oldSession = sessionManager.get(account);

			// 如果是账号已经在另一台终端登录。则让另一个终端下线

			if (newSession.fromOtherDevice(oldSession)) {
				sendForceOfflineMessage(oldSession, account, newSession.getDeviceModel());
			}

			// 如果是重复连接，则直接返回
			if (newSession.equals(oldSession)) {

				oldSession.setStatus(CIMSession.STATUS_ENABLED);
				sessionManager.update(oldSession);
				reply.put("sid", oldSession.getGid());
				return reply;
			}

			closeQuietly(oldSession);

			// 第一次设置心跳时间为登录时间
			newSession.setBindTime(System.currentTimeMillis());
			newSession.setHeartbeat(System.currentTimeMillis());

			sessionManager.add(newSession);

		} catch (Exception e) {
			reply.setCode(CIMConstant.ReturnCode.CODE_500);
			e.printStackTrace();
		}
		logger.debug("bind :account:" + message.get("account") + "-----------------------------" + reply.getCode());
		reply.put("sid", newSession.getGid());
		return reply;
	}

	private void sendForceOfflineMessage(CIMSession oldSession, String account, String deviceModel) {

		Message msg = new Message();
		msg.setMid(String.valueOf(System.currentTimeMillis()));
		msg.setAction(CIMConstant.MessageAction.ACTION_999);// 强行下线消息类型
		msg.setReceiver(account);
		msg.setContent(deviceModel);

		closeQuietly(oldSession, msg);

		ContextHolder.getBean(SystemMessagePusher.class).push(msg);

	}

	// 同一设备切换网络时关闭旧的连接
	private void closeQuietly(CIMSession oldSession) {
		if (oldSession != null && oldSession.isConnected()) {
			oldSession.removeAttribute(CIMConstant.SESSION_KEY);
			oldSession.closeNow();
		}

	}

	// 不同设备同一账号登录时关闭旧的连接
	private void closeQuietly(CIMSession oldSession, Message msg) {
		if (oldSession.isConnected()) {
			oldSession.write(msg);
			oldSession.removeAttribute(CIMConstant.SESSION_KEY);
			oldSession.closeNow();
		}
	}

}
