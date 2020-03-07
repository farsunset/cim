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
package com.farsunset.cim.sdk.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;

/*
 * 消息入口，所有消息都会经过这里
 */
public abstract class CIMEventBroadcastReceiver extends BroadcastReceiver {

    protected Context context;

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        String action = intent.getAction();

        /*
         * 操作事件广播，用于提高service存活率
         */
        if (Intent.ACTION_USER_PRESENT.equals(action)
                || Intent.ACTION_POWER_CONNECTED.equals(action)
                || Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            startPushService();
        }

        /*
         * 设备网络状态变化事件
         */
        if (CIMConstant.IntentAction.ACTION_NETWORK_CHANGED.equals(action)
                || ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

            onDevicesNetworkChanged();
        }

        /*
         * cim断开服务器事件
         */
        if (CIMConstant.IntentAction.ACTION_CONNECTION_CLOSED.equals(action)) {
            onInnerConnectionClosed();
        }

        /*
         * cim连接服务器失败事件
         */
        if (CIMConstant.IntentAction.ACTION_CONNECT_FAILED.equals(action)) {
            long interval = intent.getLongExtra("interval", CIMConstant.RECONNECT_INTERVAL_TIME);
            onInnerConnectFailed(interval);
        }

        /*
         * cim连接服务器成功事件
         */
        if (CIMConstant.IntentAction.ACTION_CONNECT_FINISHED.equals(action)) {
            onInnerConnectFinished();
        }

        /*
         * 收到推送消息事件
         */
        if (CIMConstant.IntentAction.ACTION_MESSAGE_RECEIVED.equals(action)) {
            onInnerMessageReceived((Message) intent.getSerializableExtra(Message.class.getName()), intent);
        }

        /*
         * 获取收到replyBody成功事件
         */
        if (CIMConstant.IntentAction.ACTION_REPLY_RECEIVED.equals(action)) {
            onReplyReceived((ReplyBody) intent.getSerializableExtra(ReplyBody.class.getName()));
        }


        /*
         * 获取sendBody发送成功事件
         */
        if (CIMConstant.IntentAction.ACTION_SEND_FINISHED.equals(action)) {
            onSentSucceed((SentBody) intent.getSerializableExtra(SentBody.class.getName()));
        }

        /*
         * 重新连接，如果断开的话
         */
        if (CIMConstant.IntentAction.ACTION_CONNECTION_RECOVERY.equals(action)) {
            connect(0);
        }
    }

    private void startPushService() {

        Intent intent = new Intent(context, CIMPushService.class);
        intent.setAction(CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

    }

    private void onInnerConnectionClosed() {
        CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE, false);

        if (CIMPushManager.isNetworkConnected(context)) {
            connect(0);
        }

        onConnectionClosed();
    }

    private void onInnerConnectFailed(long interval) {

        if (CIMPushManager.isNetworkConnected(context)) {

            onConnectFailed();

            connect(interval);
        }
    }

    private void onInnerConnectFinished() {
        CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE, true);

        boolean autoBind = CIMPushManager.autoBindAccount(context);
        onConnectFinished(autoBind);
    }

    private void onDevicesNetworkChanged() {

        if (CIMPushManager.isNetworkConnected(context)) {
            connect(0);
        }

        onNetworkChanged();
    }

    private void connect(long delay) {
        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(CIMPushService.KEY_DELAYED_TIME, delay);
        serviceIntent.setAction(CIMPushManager.ACTION_CREATE_CIM_CONNECTION);
        CIMPushManager.startService(context, serviceIntent);
    }

    private void onInnerMessageReceived(Message message, Intent intent) {
        if (isForceOfflineMessage(message.getAction())) {
            CIMPushManager.stop(context);
        }

        onMessageReceived(message, intent);
    }

    private boolean isForceOfflineMessage(String action) {
        return CIMConstant.MessageAction.ACTION_999.equals(action);
    }


    /**
     * 接收消息实现方法
     *
     * @param message
     * @param intent
     */
    public abstract void onMessageReceived(Message message, Intent intent);

    public void onNetworkChanged() {
        CIMListenerManager.notifyOnNetworkChanged(CIMPushManager.getNetworkInfo(context));
    }

    public void onConnectFinished(boolean hasAutoBind) {
        CIMListenerManager.notifyOnConnectFinished(hasAutoBind);
    }

    public void onConnectFailed() {
        CIMListenerManager.notifyOnConnectFailed();
    }

    public void onConnectionClosed() {
        CIMListenerManager.notifyOnConnectionClosed();
    }


    public void onReplyReceived(ReplyBody body) {
        CIMListenerManager.notifyOnReplyReceived(body);
    }

    public void onSentSucceed(SentBody body) {
        CIMListenerManager.notifyOnSendFinished(body);
    }
}
