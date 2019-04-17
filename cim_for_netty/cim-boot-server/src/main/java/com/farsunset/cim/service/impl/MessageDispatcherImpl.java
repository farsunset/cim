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

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.farsunset.cim.sdk.server.model.Message;
import com.farsunset.cim.service.MessageDispatcher;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class MessageDispatcherImpl implements MessageDispatcher{
	
	private static final Logger logger = Logger.getLogger(MessageDispatcherImpl.class.getName());
	
	@Value("${sys.message.dispatch.url}")
	private String dispatchUrl;

	@Override
	public void forward(final Message msg, final String ip) {
		String apiUrl = String.format(dispatchUrl, ip);
		try {
			String response = httpPost(apiUrl, msg);
			logger.info("消息转发目标服务器{" + ip + "},结果:" + response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe("消息转发目标服务器" + apiUrl + "message:" + e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	private String httpPost(String url, Message msg) throws Exception {

		OkHttpClient httpclient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
		FormBody.Builder build = new FormBody.Builder();

		build.add("mid", String.valueOf(msg.getMid()));
		build.add("action", msg.getAction());
		build.add("title", msg.getTitle());
		build.add("content", msg.getContent());
		build.add("sender", msg.getSender());
		build.add("receiver", msg.getReceiver());
		build.add("format", msg.getFormat());
		build.add("extra", msg.getExtra());
		build.add("timestamp", String.valueOf(msg.getTimestamp()));
		Request request = new Request.Builder().url(url).post(build.build()).build();

		Response response = httpclient.newCall(request).execute();
		String data = response.body().string();
        IOUtils.closeQuietly(response);
		return data;
	}


}
