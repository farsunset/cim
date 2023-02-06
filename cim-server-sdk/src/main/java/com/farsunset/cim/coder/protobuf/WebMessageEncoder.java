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
package com.farsunset.cim.coder.protobuf;

import com.farsunset.cim.model.Transportable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * websocket发送消息前编码
 */
public class WebMessageEncoder extends MessageToMessageEncoder<Transportable> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Transportable data, List<Object> out){
		byte[] body = data.getBody();
		ByteBufAllocator allocator = ctx.channel().config().getAllocator();
		ByteBuf buffer = allocator.buffer(body.length + 1);
		buffer.writeByte(data.getType().getValue());
		buffer.writeBytes(body);
		out.add(new BinaryWebSocketFrame(buffer));
	}
}
