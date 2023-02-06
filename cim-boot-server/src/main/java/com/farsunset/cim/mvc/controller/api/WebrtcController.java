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
 
import com.farsunset.cim.annotation.UID;
import com.farsunset.cim.component.push.DefaultMessagePusher;
import com.farsunset.cim.constants.MessageAction;
import com.farsunset.cim.model.Message;
import com.farsunset.cim.mvc.request.WebrtcRequest;
import com.farsunset.cim.mvc.response.ResponseEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/webrtc")
@Api(produces = "application/json", tags = "单人通话信令推送接口" )
public class WebrtcController {

	@Resource
	private DefaultMessagePusher defaultMessagePusher;

	@ApiOperation(httpMethod = "POST", value = "发起单人语音通话")
	@ApiImplicitParam(name = "targetId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value = {"/voice"})
	public ResponseEntity<Void> voice(@ApiParam(hidden = true) @UID String uid,@RequestParam String targetId) {
 

		Message message = new Message();
		message.setAction(MessageAction.ACTION_900);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}


	@ApiOperation(httpMethod = "POST", value = "发起单人视频通话")
	@ApiImplicitParam(name = "targetId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/video"})
	public ResponseEntity<Void> video(@ApiParam(hidden = true) @UID String uid,@RequestParam String targetId) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_901);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@ApiOperation(httpMethod = "POST", value = "接受通话")
	@ApiImplicitParam(name = "targetId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/accept"})
	public ResponseEntity<Void> accept(@ApiParam(hidden = true) @UID String uid,@RequestParam String targetId) {
		Message message = new Message();
		message.setAction(MessageAction.ACTION_902);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@ApiOperation(httpMethod = "POST", value = "拒绝通话")
	@ApiImplicitParam(name = "targetId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/reject"})
	public ResponseEntity<Void> reject(@ApiParam(hidden = true) @UID String uid,@RequestParam String targetId) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_903);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@ApiOperation(httpMethod = "POST", value = "反馈正忙")
	@ApiImplicitParam(name = "targetId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/busy"})
	public ResponseEntity<Void> busy(@ApiParam(hidden = true) @UID String uid, @RequestParam String targetId) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_904);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@ApiOperation(httpMethod = "POST", value = "挂断通话")
	@ApiImplicitParam(name = "targetId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/hangup"})
	public ResponseEntity<Void> hangup(@ApiParam(hidden = true) @UID String uid,@RequestParam String targetId) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_905);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@ApiOperation(httpMethod = "POST", value = "取消呼叫")
	@ApiImplicitParam(name = "targetId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/cancel"})
	public ResponseEntity<Void> cancel(@ApiParam(hidden = true) @UID String uid, @RequestParam String targetId) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_906);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@ApiOperation(httpMethod = "POST", value = "同步IceCandidate")
	@PostMapping(value = {"/transmit/ice"})
	public ResponseEntity<Void> ice(@ApiParam(hidden = true) @UID String uid,
									@RequestBody WebrtcRequest request
	) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_907);
		message.setSender(uid);
		message.setContent(request.getContent());
		message.setReceiver(request.getUid());
		defaultMessagePusher.push(message);
		return ResponseEntity.make();
	}

	@ApiOperation(httpMethod = "POST", value = "同步offer")
	@PostMapping(value =  {"/transmit/offer"})
	public ResponseEntity<Void> offer(@ApiParam(hidden = true) @UID String uid,
									  @RequestBody WebrtcRequest request
	) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_908);
		message.setSender(uid);
		message.setContent(request.getContent());
		message.setReceiver(request.getUid());
		defaultMessagePusher.push(message);
		return ResponseEntity.make();
	}

	@ApiOperation(httpMethod = "POST", value = "同步answer")
	@PostMapping(value =  {"/transmit/answer"})
	public ResponseEntity<Void> answer(@ApiParam(hidden = true) @UID String uid,
									   @RequestBody WebrtcRequest request
	) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_909);
		message.setSender(uid);
		message.setContent(request.getContent());
		message.setReceiver(request.getUid());
		defaultMessagePusher.push(message);
		return ResponseEntity.make();
	}
}
