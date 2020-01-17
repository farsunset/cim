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
package com.farsunset.cim.sdk.server.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * java |android 客户端请求结构
 *
 */
public class SentBody implements Serializable {

	private static final long serialVersionUID = 1L;

	private String key;

	private HashMap<String, String> data = new HashMap<String, String>();

	private long timestamp;

	public String getKey() {
		return key;
	}

	public String get(String k) {
		return data.get(k);
	}

	public int getInt(String k) {
		return Integer.parseInt(data.get(k));
	}

	public long getLong(String k) {
		return Long.parseLong(data.get(k));
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void remove(String k) {
		data.remove(k);
	}

	public void put(String k, String v) {
		if (v != null && k != null) {
			data.put(k, v);
		}
	}

	public void putAll(Map<String, String> map) {
		data.putAll(map);
	}

	public Set<String> getKeySet() {
		return data.keySet();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("#SentBody#").append("\n");
		;
		buffer.append("key:").append(key).append("\n");
		buffer.append("timestamp:").append(timestamp).append("\n");

		buffer.append("data:{");
		for (String key : getKeySet()) {
			buffer.append("\n").append(key).append(":").append(this.get(key));
		}
		buffer.append("}");

		return buffer.toString();
	}

}
