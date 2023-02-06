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

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TokenRedisTemplate extends StringRedisTemplate {

	private static final String TOKEN_CACHE_PREFIX = "TOKEN_%s";


	public TokenRedisTemplate(RedisConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	public void save(String token, String uid) {

		String key = String.format(TOKEN_CACHE_PREFIX,token);

		super.boundValueOps(key).set(uid);
	}

	public String get(String token) {

		String key = String.format(TOKEN_CACHE_PREFIX,token);

		return super.boundValueOps(key).get();

	}

	public void remove(String token) {

		String key = String.format(TOKEN_CACHE_PREFIX,token);

		super.delete(key);
	}

}
