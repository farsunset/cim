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
package com.farsunset.cim.sdk.android.constant;

public interface CIMConstant {

    long RECONNECT_INTERVAL_TIME = 5000L;

    /*
     * 消息头长度为3个字节，第一个字节为消息类型，第二，第三字节 转换int后为消息长度
     */
    int DATA_HEADER_LENGTH = 3;

    /*
      被其他设备登录挤下线消息
     */
    String ACTION_999 = "999";

}
