/**
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
package com.farsunset.cim.util;

public interface Constants {

	public class Common {
		public final static String STATUS_1 = "1";
		public final static String STATUS_0 = "0";
		public final static String STATUS_2 = "2";
		public final static String STATUS_3 = "3";
		public final static String TYPR_1 = "1";
		public final static String TYPR_2 = "2";
		public final static String TYPR_9 = "9";

	}

	public class User {
		public final static Integer SUCCESS_CODE = 1;
		public final static Integer ERROR_CODE = 0;
		public final static String User = "User";

	}

	public class RequestParam {
		public final static String CURRENTPAGE = "currentPage";
		public final static String TYPE = "type";
		public static final String SIZE = "size";

	}

	public class Number {

		public final static Integer INT_0 = 0;
		public final static Integer INT_10 = 10;
		public final static Integer INT_403 = 403;

	}

	public static interface MessageType {

		// 用户之间的普通消息
		public static final String TYPE_0 = "0";

		// 系统向用户发送的普通消息
		public static final String TYPE_2 = "2";

	}

}
