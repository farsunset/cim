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
package com.farsunset.cim.sdk.client;

import java.util.HashMap;

class CIMCacheManager {

	private static final HashMap<String, String> CIM_CONFIG_INFO = new HashMap<String, String>();

	public static final String KEY_MANUAL_STOP = "KEY_MANUAL_STOP";

	public static final String KEY_CIM_DESTROYED = "KEY_CIM_DESTROYED";

	public static final String KEY_CIM_SERVER_HOST = "KEY_CIM_SERVER_HOST";

	public static final String KEY_CIM_SERVER_PORT = "KEY_CIM_SERVER_PORT";

	public static final String KEY_CIM_CONNECTION_STATE = "KEY_CIM_CONNECTION_STATE";

	static CIMCacheManager toolkit;

	public static CIMCacheManager getInstance() {
		if (toolkit == null) {
			toolkit = new CIMCacheManager();
		}
		return toolkit;
	}

	public void remove(String key) {
		CIM_CONFIG_INFO.remove(key);
	}

	public void putString(String key, String value) {
		CIM_CONFIG_INFO.put(key, value);

	}

	public String getString(String key) {
		return CIM_CONFIG_INFO.get(key);
	}

	public void putBoolean(String key, boolean value) {
		putString(key, Boolean.toString(value));
	}

	public boolean getBoolean(String key) {
		String value = getString(key);
		return Boolean.parseBoolean(value);
	}

	public void putInt(String key, int value) {
		putString(key, String.valueOf(value));
	}

	public int getInt(String key) {
		String value = getString(key);
		return value == null ? 0 : Integer.parseInt(value);
	}

}
