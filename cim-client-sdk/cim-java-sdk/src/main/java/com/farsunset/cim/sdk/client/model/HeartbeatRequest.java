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

import com.farsunset.cim.sdk.client.constant.CIMConstant;

/**
 * 服务端心跳请求
 *
 */
public class HeartbeatRequest implements Serializable, Protobufable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "SERVER_HEARTBEAT_REQUEST";
	private static final String CMD_HEARTBEAT_RESPONSE = "SR";

	private static final HeartbeatRequest object = new HeartbeatRequest();

	private HeartbeatRequest() {

	}

	public static HeartbeatRequest getInstance() {
		return object;
	}

	@Override
	public byte[] getByteArray() {
		return CMD_HEARTBEAT_RESPONSE.getBytes();
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public byte getType() {
		return CIMConstant.ProtobufType.S_H_RQ;
	}

}
