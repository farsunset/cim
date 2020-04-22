/**
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * **************************************************************************************
 * *
 * Website : http://www.farsunset.com                           *
 * *
 * **************************************************************************************
 */
package com.farsunset.ichat.example.app;

public interface Constant {

    //服务端IP地址
    String CIM_SERVER_HOST = "192.168.22.103";

    //注意，这里的端口不是tomcat的端口，没改动就使用默认的23456
    int CIM_SERVER_PORT = 23456;

    interface MessageAction {
        //下线类型
        String ACTION_999 = "999";
    }
}
