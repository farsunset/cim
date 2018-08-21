/**
 * Copyright 2013-2033 Xia Jun(3979434@qq.com).
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

import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.farsunset.cim.sdk.server.model.Message;
import com.farsunset.cim.service.ApnsService;
import com.farsunset.cim.util.ApnsPayloadCompat;

import cn.teaey.apns4j.Apns4j;
import cn.teaey.apns4j.network.ApnsChannel;
import cn.teaey.apns4j.network.ApnsChannelFactory;
import cn.teaey.apns4j.network.ApnsGateway;

@Service
public class JavaApnsServiceImpl implements ApnsService {
	private final Logger logger = Logger.getLogger(JavaApnsServiceImpl.class.getName());
	@Value("${apple.apns.p12.password}")
	private String password;
	@Value("${apple.apns.p12.file}")
	private String p12Path;
	@Value("${apple.apns.debug}")
	private boolean isDebug;

	@SuppressWarnings("deprecation")
	@Override
	public void push(Message message, String deviceToken) {

		if(StringUtils.isBlank(deviceToken)) {
			return ;
		}
		 
		InputStream stream = getClass().getResourceAsStream(p12Path);
		ApnsChannelFactory apnsChannelFactory = Apns4j.newChannelFactoryBuilder()
		.keyStoreMeta(stream)
		.keyStorePwd(password)
		.apnsGateway(isDebug ? ApnsGateway.DEVELOPMENT : ApnsGateway.PRODUCTION)
		.build();

		ApnsChannel apnsChannel = apnsChannelFactory.newChannel();

		try {
			ApnsPayloadCompat apnsPayload = new ApnsPayloadCompat();
			apnsPayload.setAction(message.getAction());
			apnsPayload.setContent(message.getContent());
			apnsPayload.setSender(message.getSender());
			apnsPayload.setFormat(message.getFormat());
			apnsPayload.setReceiver(message.getReceiver());
			apnsChannel.send(deviceToken, apnsPayload);
			
			logger.info(deviceToken +"\r\ndata:" +apnsPayload.toJsonString());
		}catch(Exception e) {
			e.printStackTrace();
			logger.severe(e.getLocalizedMessage());
		}finally {
			apnsChannel.close();
			IOUtils.closeQuietly(stream);
		}
	}
}
