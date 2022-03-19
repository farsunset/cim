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
package com.farsunset.cim.mvc.controller.api;

import com.farsunset.cim.component.push.DefaultMessagePusher;
import com.farsunset.cim.model.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/message")
@Api(produces = "application/json", tags = "消息相关接口" )
public class MessageController  {

	@Resource
	private DefaultMessagePusher defaultMessagePusher;

	@ApiOperation(httpMethod = "POST", value = "发送消息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sender", value = "发送者UID", paramType = "query", dataTypeClass = String.class, required = true, example = ""),
			@ApiImplicitParam(name = "receiver", value = "接收者UID", paramType = "query", dataTypeClass = String.class, required = true, example = ""),
			@ApiImplicitParam(name = "action", value = "消息动作", paramType = "query", dataTypeClass = String.class, required = true, example = ""),
			@ApiImplicitParam(name = "title", value = "消息标题", paramType = "query", dataTypeClass = String.class, example = ""),
			@ApiImplicitParam(name = "content", value = "消息内容", paramType = "query", dataTypeClass = String.class,  example = ""),
			@ApiImplicitParam(name = "format", value = "消息格式", paramType = "query", dataTypeClass = String.class,  example = ""),
			@ApiImplicitParam(name = "extra", value = "扩展字段", paramType = "query", dataTypeClass = String.class, example = ""),
	})
	@PostMapping(value = "/send")
	public ResponseEntity<Long> send(@RequestParam String sender ,
									 @RequestParam String receiver ,
									 @RequestParam String action ,
									 @RequestParam(required = false) String title ,
									 @RequestParam(required = false) String content ,
									 @RequestParam(required = false) String format ,
									 @RequestParam(required = false) String extra)  {

		Message message = new Message();
		message.setSender(sender);
		message.setReceiver(receiver);
		message.setAction(action);
		message.setContent(content);
		message.setFormat(format);
		message.setTitle(title);
		message.setExtra(extra);

		message.setId(System.currentTimeMillis());

		defaultMessagePusher.push(message);

		return ResponseEntity.ok(message.getId());
	}


}
