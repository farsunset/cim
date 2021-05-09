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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


@Component
public class KeyValueRedisTemplate extends StringRedisTemplate {

	public KeyValueRedisTemplate(RedisConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	public void set(String key ,String value) {
		super.boundValueOps(key).set(value);
	}

	public String get(String key) {
		return super.boundValueOps(key).get();
	}

	public String getDeviceToken(String uid){
		return super.boundValueOps(String.format(Constants.APNS_DEVICE_TOKEN,uid)).get();
	}

	public void openApns(String uid,String deviceToken){
		super.boundValueOps(String.format(Constants.APNS_DEVICE_TOKEN,uid)).set(deviceToken);
	}

	public void closeApns(String uid){
		super.delete(String.format(Constants.APNS_DEVICE_TOKEN,uid));
	}


}
