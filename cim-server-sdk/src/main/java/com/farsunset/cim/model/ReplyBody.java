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
package com.farsunset.cim.model;

import com.farsunset.cim.constant.CIMConstant;
import com.farsunset.cim.model.proto.ReplyBodyProto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求应答对象
 *
 */
public class ReplyBody implements Serializable, Transportable {

	private static final long serialVersionUID = 1L;

	/**
	 * 请求key
	 */
	private String key;

	/**
	 * 返回码
	 */
	private String code;

	/**
	 * 返回说明
	 */
	private String message;

	/**
	 * 返回数据集合
	 */
	private final Map<String, String> data = new HashMap<>();

	private long timestamp;

	public ReplyBody() {
		timestamp = System.currentTimeMillis();
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void put(String k, String v) {
		data.put(k, v);
	}

	public String get(String k) {
		return data.get(k);
	}

	public void remove(String k) {
		data.remove(k);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCode(Number code) {
		this.code = code.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[ReplyBody]").append("\n");
		builder.append("key:").append(this.getKey()).append("\n");
		builder.append("timestamp:").append(timestamp).append("\n");
		builder.append("code:").append(code).append("\n");

		builder.append("data:{");
		data.forEach((k, v) -> builder.append("\n").append(k).append(":").append(v));
		builder.append("\n}");

		return builder.toString();
	}

	@JsonIgnore
	@Override
	public byte[] getBody() {
		ReplyBodyProto.Model.Builder builder = ReplyBodyProto.Model.newBuilder();
		builder.setCode(code);
		if (message != null) {
			builder.setMessage(message);
		}
		if (!data.isEmpty()) {
			builder.putAllData(data);
		}
		builder.setKey(key);
		builder.setTimestamp(timestamp);

		return builder.build().toByteArray();
	}

	@JsonIgnore
	@Override
	public byte getType() {
		return CIMConstant.DATA_TYPE_REPLY;
	}

}
