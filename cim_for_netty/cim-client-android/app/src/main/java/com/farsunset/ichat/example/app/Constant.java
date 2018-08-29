/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
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
    public static final String CIM_SERVER_HOST = "192.168.1.106";


    //注意，这里的端口不是tomcat的端口，CIM端口在服务端spring-cim.xml中配置的，没改动就使用默认的23456
    public static final int CIM_SERVER_PORT = 23456;

    public static interface MessageType {


        //用户之间的普通消息
        public static final String TYPE_0 = "0";


        //下线类型
        String TYPE_999 = "999";
    }


    public static interface MessageStatus {

        //消息未读
        public static final String STATUS_0 = "0";
        //消息已经读取
        public static final String STATUS_1 = "1";
    }

}
