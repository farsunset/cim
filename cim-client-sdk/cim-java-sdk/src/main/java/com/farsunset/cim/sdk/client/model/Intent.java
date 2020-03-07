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
package com.farsunset.cim.sdk.client.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * java |android 客户端请求结构
 *
 */
public class Intent implements Serializable {

	private static final long serialVersionUID = 1L;

	private String action;

	private final HashMap<String, Object> data = new HashMap<String, Object>();

	public Intent() {
	}

	public Intent(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void putExtra(String key, Object value) {
		data.put(key, value);
	}

	public Object getExtra(String key) {
		return data.get(key);
	}

	public long getLongExtra(String key, long defValue) {
		Object v = getExtra(key);
		try {
			return Long.parseLong(v.toString());
		} catch (Exception e) {
			return defValue;
		}
	}
}
