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
package com.farsunset.cim.coder.json;

import com.farsunset.cim.model.Ping;
import com.farsunset.cim.model.Transportable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * websocket 协议 json消息体编码
 */
public class TextMessageEncoder extends MessageToMessageEncoder<Transportable> {

	private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder()
			.serializationInclusion(JsonInclude.Include.NON_NULL)
			.build();

	@Override
	protected void encode(ChannelHandlerContext ctx, Transportable data, List<Object> out) throws JsonProcessingException {

		TransmitBody protocol = new TransmitBody();
		protocol.setType(data.getType().getValue());
		protocol.setContent(getBody(data));

		TextWebSocketFrame frame = new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(protocol));

		out.add(frame);
	}

	private String getBody(Transportable data) throws JsonProcessingException {
		if (data instanceof Ping){
			return null;
		}
		return OBJECT_MAPPER.writeValueAsString(data);
	}
}
