/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.sdk.android;

import android.net.NetworkInfo;

import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;

/**
 *CIM 主要事件接口
 */
public interface CIMEventListener
{


    /**
     * 当收到服务端推送过来的消息时调用
     * @param message
     */
    void onMessageReceived(Message message);

    /**
     * 当调用CIMPushManager.sendRequest()向服务端发送请求，获得相应时调用
     * @param replybody
     */
    void onReplyReceived(ReplyBody replybody);

    /**
     * 当调用CIMPushManager.sendRequest()向服务端发送请求成功时
     * @param body
     */
    void onSentSuccessed(SentBody body);

    
    /**
     * 当手机网络发生变化时调用
     * @param networkinfo
     */
    void onNetworkChanged(NetworkInfo networkinfo);
    
    
    /**
     * 当连接服务器成功时回调
     * @param hasAutoBind  : true 已经自动绑定账号到服务器了，不需要再手动调用bindAccount
     */
    void onConnectionSuccessed(boolean hasAutoBind);
    
    /**
     * 当断开服务器连接的时候回调
     * 
     */
    void onConnectionClosed();
    
    /**
     * 当连接服务器失败的时候回调
     * 
     */
    void onConnectionFailed();
    
    /**
     * 监听器在容器里面的排序。值越大则越先接收
     */
    int getEventDispatchOrder();
}

