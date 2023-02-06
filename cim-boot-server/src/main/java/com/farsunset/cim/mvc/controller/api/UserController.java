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


import com.farsunset.cim.annotation.AccessToken;
import com.farsunset.cim.mvc.response.ResponseEntity;
import com.farsunset.cim.service.AccessTokenService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(produces = "application/json", tags = "用户登录接口" )
@Validated
public class UserController {

	@Resource
	private AccessTokenService accessTokenService;

	@ApiOperation(httpMethod = "POST", value = "模拟登录")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "telephone", value = "手机号码", paramType = "query", dataTypeClass = String.class),
			@ApiImplicitParam(name = "password", value = "密码", paramType = "query", dataTypeClass = String.class),
	})
	@PostMapping(value = "/login")
	public ResponseEntity<?> login(@RequestParam String telephone) {


		Map<String,Object> body = new HashMap<>();
		body.put("id",Long.parseLong(telephone));
		body.put("name","测试用户");
		body.put("telephone","telephone");

		ResponseEntity<Map<String,Object>> result = new ResponseEntity<>();

		result.setData(body);

		result.setToken(accessTokenService.generate(telephone));
		result.setTimestamp(System.currentTimeMillis());
		return result;
	}


	@ApiOperation(httpMethod = "GET", value = "退出登录")
	@GetMapping(value = "/logout")
	public ResponseEntity<Void> logout(@ApiParam(hidden = true) @AccessToken String token) {
		accessTokenService.delete(token);
		return ResponseEntity.make();
	}

}
