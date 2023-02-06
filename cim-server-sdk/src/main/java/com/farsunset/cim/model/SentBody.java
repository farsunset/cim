/*
 * Copyright 2013-2022 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * java |android 客户端请求结构
 *
 */
public class SentBody implements Serializable {

	private static final long serialVersionUID = 1L;

	private String key;

	private final Map<String, String> data = new HashMap<>();

	private long timestamp;

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data.clear();
		this.data.putAll(data);
	}

	public String getKey() {
		return key;
	}

	public String get(String key) {
		return data.get(key);
	}

	public String get(String key,String defaultValue) {
		return data.getOrDefault(key,defaultValue);
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[SentBody]").append("\n");
		builder.append("key       :").append(this.getKey()).append("\n");
		builder.append("timestamp :").append(timestamp).append("\n");
		builder.append("data      :").append("\n{");
		data.forEach((k, v) -> builder.append("\n").append(k).append(":").append(v));
		builder.append(data.isEmpty() ? "" : "\n").append("}");
		return builder.toString();
	}

}
