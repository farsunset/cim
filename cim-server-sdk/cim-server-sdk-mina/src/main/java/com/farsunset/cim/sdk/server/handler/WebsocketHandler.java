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
package com.farsunset.cim.sdk.server.handler;

import com.farsunset.cim.sdk.server.model.CIMSession;
import com.farsunset.cim.sdk.server.model.HandshakerResponse;
import com.farsunset.cim.sdk.server.model.SentBody;
import org.apache.mina.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 处理websocket握手请求，返回响应的报文给浏览器
 */
public class WebsocketHandler implements CIMRequestHandler {

	private final static String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

	@Override
	public void process(CIMSession session, SentBody body) {
		session.setChannel(CIMSession.CHANNEL_BROWSER);
		String secKey = body.get("key") + GUID;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(secKey.getBytes(StandardCharsets.ISO_8859_1), 0, secKey.length());
			byte[] sha1Hash = md.digest();

			session.write(new HandshakerResponse(new String(Base64.encodeBase64(sha1Hash))));
		} catch (Exception ignore) {}
	}
}
