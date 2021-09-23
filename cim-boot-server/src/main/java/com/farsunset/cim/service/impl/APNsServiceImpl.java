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
package com.farsunset.cim.service.impl;

import cn.teaey.apns4j.Apns4j;
import cn.teaey.apns4j.network.ApnsChannel;
import cn.teaey.apns4j.network.ApnsChannelFactory;
import cn.teaey.apns4j.network.ApnsGateway;
import cn.teaey.apns4j.protocol.ApnsPayload;
import com.farsunset.cim.config.properties.APNsProperties;
import com.farsunset.cim.sdk.server.model.Message;
import com.farsunset.cim.service.APNsService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class APNsServiceImpl implements APNsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(APNsServiceImpl.class);

	private final ApnsChannelFactory apnsChannelFactory;

	@Autowired
	public APNsServiceImpl(APNsProperties properties){
		InputStream stream = getClass().getResourceAsStream(properties.getP12File());
		apnsChannelFactory = Apns4j.newChannelFactoryBuilder()
				.keyStoreMeta(stream)
				.keyStorePwd(properties.getP12Password())
				.apnsGateway(properties.isDebug() ? ApnsGateway.DEVELOPMENT : ApnsGateway.PRODUCTION)
				.build();
	}


	@SuppressWarnings("deprecation")
	@Override
	public void push(Message message, String deviceToken) {

		if(StringUtils.isBlank(deviceToken)) {
			return ;
		}

		ApnsChannel apnsChannel = apnsChannelFactory.newChannel();
		ApnsPayload apnsPayload = new ApnsPayload();

		apnsPayload.alert("您有一条新的消息");

		apnsPayload.sound("default");
		apnsPayload.badge(1);
		apnsPayload.extend("id",message.getId());
		apnsPayload.extend("action",message.getAction());
		apnsPayload.extend("content",message.getContent());
		apnsPayload.extend("sender",message.getSender());
		apnsPayload.extend("receiver",message.getReceiver());
		apnsPayload.extend("format",message.getFormat());
		apnsPayload.extend("extra",message.getExtra());
		apnsPayload.extend("timestamp",message.getTimestamp());

		try {
			apnsChannel.send(deviceToken, apnsPayload);
			LOGGER.info("APNs push done.\ndeviceToken : {} \napnsPayload : {}",deviceToken,apnsPayload.toJsonString());
		}catch(Exception exception) {
			LOGGER.error("APNs push failed",exception);
		}finally {
			IOUtils.closeQuietly(apnsChannel);
		}
	}
}
