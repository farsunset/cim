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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.farsunset.cim.sdk.client.constant.CIMConstant;
import com.farsunset.cim.sdk.client.model.Intent;
import com.farsunset.cim.sdk.client.model.Message;
import com.farsunset.cim.sdk.client.model.ReplyBody;
import com.farsunset.cim.sdk.client.model.SentBody;

/**
 * 消息入口，所有消息都会经过这里
 */
public class CIMEventBroadcastReceiver {
	private static CIMEventBroadcastReceiver receiver;
	private CIMEventListener listener;

	private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, r -> {
		Thread thread = new Thread(r);
		thread.setName("cim-reconnect-");
		return thread;
	});

	private CIMEventBroadcastReceiver(){

	}
	public static CIMEventBroadcastReceiver getInstance() {
		if (receiver == null) {
			receiver = new CIMEventBroadcastReceiver();
		}
		return receiver;
	}

	public void setGlobalCIMEventListener(CIMEventListener ls) {
		listener = ls;
	}

	public void onReceive(Intent intent) {

		/*
		 * cim断开服务器事件
		 */
		if (intent.getAction().equals(CIMConstant.IntentAction.ACTION_CONNECTION_CLOSED)) {
			onInnerConnectionClosed();
		}

		/*
		 * cim连接服务器失败事件
		 */
		if (intent.getAction().equals(CIMConstant.IntentAction.ACTION_CONNECT_FAILED)) {
			long interval = intent.getLongExtra("interval", CIMConstant.RECONNECT_INTERVAL_TIME);
			onInnerConnectFailed(interval);
		}

		/*
		 * cim连接服务器成功事件
		 */
		if (intent.getAction().equals(CIMConstant.IntentAction.ACTION_CONNECT_FINISHED)) {
			onInnerConnectFinished();
		}

		/*
		 * 收到推送消息事件
		 */
		if (intent.getAction().equals(CIMConstant.IntentAction.ACTION_MESSAGE_RECEIVED)) {
			onInnerMessageReceived((Message) intent.getExtra(Message.class.getName()));
		}

		/*
		 * 获取收到replyBody成功事件
		 */
		if (intent.getAction().equals(CIMConstant.IntentAction.ACTION_REPLY_RECEIVED)) {
			listener.onReplyReceived((ReplyBody) intent.getExtra(ReplyBody.class.getName()));
		}

		/*
		 * 获取sendBody发送成功事件
		 */
		if (intent.getAction().equals(CIMConstant.IntentAction.ACTION_SEND_FINISHED)) {
			onInnerSendFinished((SentBody) intent.getExtra(SentBody.class.getName()));
		}

		/*
		 * 重新连接，如果断开的话
		 */
		if (intent.getAction().equals(CIMConstant.IntentAction.ACTION_CONNECTION_RECOVERY)) {
			CIMPushManager.connect();
		}
	}

	private void onInnerConnectionClosed() {

		listener.onConnectionClosed();

		CIMCacheManager.getInstance().putBoolean(CIMCacheManager.KEY_CIM_CONNECTION_STATE, false);
		CIMPushManager.connect();

	}

	private void onInnerConnectFailed(long interval) {

		executorService.schedule((Runnable) CIMPushManager::connect,interval, TimeUnit.MICROSECONDS);

		listener.onConnectFailed();
	}

	private void onInnerConnectFinished() {
		CIMCacheManager.getInstance().putBoolean(CIMCacheManager.KEY_CIM_CONNECTION_STATE, true);

		boolean autoBind = CIMPushManager.autoBindDeviceId();

		listener.onConnectFinished(autoBind);
	}


	private void onInnerMessageReceived(Message message) {
		if (isForceOfflineMessage(message.getAction())) {
			CIMPushManager.stop();
		}

		listener.onMessageReceived(message);
	}

	private boolean isForceOfflineMessage(String action) {
		return CIMConstant.MessageAction.ACTION_999.equals(action);
	}

	private void onInnerSendFinished(SentBody body) {
		listener.onSendFinished(body);
	}

}
