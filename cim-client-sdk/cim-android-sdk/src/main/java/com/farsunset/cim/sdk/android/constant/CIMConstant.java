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

    long RECONNECT_INTERVAL_TIME = 30 * 1000;

    /*
     * 消息头长度为3个字节，第一个字节为消息类型，第二，第三字节 转换int后为消息长度
     */
    int DATA_HEADER_LENGTH = 3;

    interface ProtobufType {

        /*
        客户端->服务端 发送的心跳响应
        */
        byte C_H_RS = 0;

        /*
        服务端->客户端 发送的心跳请求
        */
        byte S_H_RQ = 1;

        byte MESSAGE = 2;

        byte SENT_BODY = 3;

        byte REPLY_BODY = 4;
    }

    interface RequestKey {

        String CLIENT_BIND = "client_bind";

    }

    interface MessageAction {

        /*
        被其他设备登录挤下线消息
         */
        String ACTION_999 = "999";
    }

    interface IntentAction {

        /*
         消息广播action
         */
        String ACTION_MESSAGE_RECEIVED = "com.farsunset.cim.MESSAGE_RECEIVED";

        /*
         发送sendBody成功广播
         */
        String ACTION_SEND_FINISHED = "com.farsunset.cim.SEND_FINISHED";

        /*
         链接意外关闭广播
         */
        String ACTION_CONNECTION_CLOSED = "com.farsunset.cim.CONNECTION_CLOSED";

        /*
         链接失败广播
         */
        String ACTION_CONNECT_FAILED = "com.farsunset.cim.CONNECT_FAILED";

        /*
         链接成功广播
         */
        String ACTION_CONNECT_FINISHED = "com.farsunset.cim.CONNECT_FINISHED";

        /*
         发送sendBody成功后获得replayBody回应广播
         */
        String ACTION_REPLY_RECEIVED = "com.farsunset.cim.REPLY_RECEIVED";

        /*
         网络变化广播
         */
        String ACTION_NETWORK_CHANGED = "com.farsunset.cim.NETWORK_CHANGED";

        /*
         重试连接
         */
        String ACTION_CONNECTION_RECOVERY = "com.farsunset.cim.CONNECTION_RECOVERY";
    }

}
