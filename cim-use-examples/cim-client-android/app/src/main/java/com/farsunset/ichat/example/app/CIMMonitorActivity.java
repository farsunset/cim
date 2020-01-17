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


import android.app.Activity;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.farsunset.cim.sdk.android.CIMEventListener;
import com.farsunset.cim.sdk.android.CIMListenerManager;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;

public abstract class CIMMonitorActivity extends Activity implements CIMEventListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CIMListenerManager.registerMessageListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        CIMListenerManager.removeMessageListener(this);

    }

    @Override
    public void onRestart() {
        super.onRestart();
        CIMListenerManager.registerMessageListener(this);
    }

    @Override
    public void onMessageReceived(Message arg0) {
    }

    @Override
    public void onNetworkChanged(NetworkInfo info) {
    }

    /**
     * 与服务端断开连接时回调,不要在里面做连接服务端的操作
     */
    @Override
    public void onConnectionClosed() {
    }

    @Override
    public void onConnectFailed() {

    }

    @Override
    public int getEventDispatchOrder() {
        return 0;
    }

    /**
     * 连接服务端成功时回调
     */

    @Override
    public void onConnectFinished(boolean arg0) {
    }


    @Override
    public void onReplyReceived(ReplyBody arg0) {
    }

    @Override
    public void onSendFinished(SentBody sentBody) {

    }
}
