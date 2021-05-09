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

import com.farsunset.cim.service.SessionService;
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
@RequestMapping("/apns")
@Api(produces = "application/json", tags = "APNs推送相关")
public class APNsController {

	@ApiOperation(httpMethod = "POST", value = "开启apns")


	@ApiImplicitParams({
			@ApiImplicitParam(name = "deviceToken", value = "APNs的deviceToken", paramType = "query", dataTypeClass = String.class, required = true, example = ""),
			@ApiImplicitParam(name = "uid", value = "用户ID", paramType = "query", dataTypeClass = String.class,example = "0")
	})
	@PostMapping(value = "/open")
	public ResponseEntity<Void> open(@RequestParam String uid , @RequestParam String deviceToken) {

		sessionService.openApns(uid,deviceToken);

		return ResponseEntity.ok().build();
	}

	@Resource
	private SessionService sessionService;

	@ApiOperation(httpMethod = "POST", value = "关闭apns")
	@ApiImplicitParam(name = "uid", value = "用户ID", paramType = "query", dataTypeClass = String.class,example = "0")
	@PostMapping(value = "/close")
	public ResponseEntity<Void> close(@RequestParam String uid) {

		sessionService.closeApns(uid);

		return ResponseEntity.ok().build();
	}
}
