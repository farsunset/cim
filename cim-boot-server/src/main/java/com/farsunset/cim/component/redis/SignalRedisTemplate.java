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
package com.farsunset.cim.component.redis;

import com.farsunset.cim.constants.Constants;
import com.farsunset.cim.entity.Session;
import com.farsunset.cim.model.Message;
import com.farsunset.cim.util.JSONUtils;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SignalRedisTemplate extends StringRedisTemplate {

	public SignalRedisTemplate(LettuceConnectionFactory connectionFactory) {
		super(connectionFactory);
		connectionFactory.setValidateConnection(true);
	}

	/**
	 * 消息发送到 集群中的每个实例，获取对应长连接进行消息写入
	 * @param message
	 */
	public void push(Message message) {
		super.convertAndSend(Constants.PUSH_MESSAGE_INNER_QUEUE, JSONUtils.toJSONString(message));
	}

	/**
	 * 消息发送到 集群中的每个实例，解决多终端在线冲突问题
	 * @param session
	 */
	public void bind(Session session) {
		super.convertAndSend(Constants.BIND_MESSAGE_INNER_QUEUE, JSONUtils.toJSONString(session));
	}
}
