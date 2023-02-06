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
package com.farsunset.cim.mvc.interceptor;

import com.farsunset.cim.annotation.AccessToken;
import com.farsunset.cim.annotation.UID;
import com.farsunset.cim.service.AccessTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 在此鉴权获得UID
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

	private static final String HEADER_TOKEN = "access-token";

	@Resource
	private AccessTokenService accessTokenService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		String token = request.getHeader(HEADER_TOKEN);

		String uid = accessTokenService.getUid(token);

		/*
		 * 直接拒绝无token的接口调用请求或者token没有查询到对应的登录用户
		 */
		if (uid == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return false;
		}

		request.setAttribute(UID.class.getName(), uid);
		request.setAttribute(AccessToken.class.getName(), token);

		return true;

	}
}
