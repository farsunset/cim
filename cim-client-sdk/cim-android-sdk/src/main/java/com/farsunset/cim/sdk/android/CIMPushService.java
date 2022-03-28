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

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.logger.CIMLogger;
import com.farsunset.cim.sdk.android.model.Pong;
import com.farsunset.cim.sdk.android.model.SentBody;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 与服务端连接服务
 *
 * @author 3979434
 */
public class CIMPushService extends Service {

    static final String KEY_SEND_BODY = "KEY_SEND_BODY";
    static final String KEY_DELAYED_TIME = "KEY_DELAYED_TIME";
    static final String KEY_LOGGER_ENABLE = "KEY_LOGGER_ENABLE";
    static final String KEY_NOTIFICATION_MESSAGE = "KEY_NOTIFICATION_MESSAGE";
    static final String KEY_NOTIFICATION_CHANNEL = "KEY_NOTIFICATION_CHANNEL";
    static final String KEY_NOTIFICATION_ICON = "KEY_NOTIFICATION_ICON";

    private static final String TRANSIENT_NTC_CHANNEL_ID = "CIM_PUSH_TRANSIENT_NTC_ID";
    private static final String PERSIST_NTC_CHANNEL_ID = "CIM_PUSH_PERSIST_NTC_ID";

    private static final int NOTIFICATION_ID = Integer.MAX_VALUE;

    private static final int PERSIST_NOTIFICATION_ID = Integer.MIN_VALUE;

    private CIMConnectorManager connectorManager;
    private KeepAliveBroadcastReceiver keepAliveReceiver;
    private ConnectivityManager connectivityManager;
    private NotificationManager notificationManager;
    private final AtomicBoolean persistHolder = new AtomicBoolean(false);


    @Override
    public void onCreate() {
        connectorManager = CIMConnectorManager.getManager(this.getApplicationContext());
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keepAliveReceiver = new KeepAliveBroadcastReceiver();
            registerReceiver(keepAliveReceiver, keepAliveReceiver.getIntentFilter());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            connectivityManager = getSystemService(ConnectivityManager.class);

            connectivityManager.registerDefaultNetworkCallback(networkCallback);

        }
    }

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            Intent intent = new Intent();
            intent.setPackage(getPackageName());
            intent.setAction(CIMConstant.IntentAction.ACTION_NETWORK_CHANGED);
            sendBroadcast(intent);
        }

        @Override
        public void onUnavailable() {
            Intent intent = new Intent();
            intent.setPackage(getPackageName());
            intent.setAction(CIMConstant.IntentAction.ACTION_NETWORK_CHANGED);
            sendBroadcast(intent);
        }

    };

    private final Handler connectHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {
            prepareConnect();
        }
    };

    private final Handler notificationHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {
            if (persistHolder.get()){
                return;
            }
            stopForeground(true);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent == null ? CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE : intent.getAction();

        if (!persistHolder.get()) {
            createNotification();
        }

        if (CIMPushManager.ACTION_CREATE_CIM_CONNECTION.equals(action)) {
            this.prepareConnect(intent.getLongExtra(KEY_DELAYED_TIME, 0));
        }

        if (CIMPushManager.ACTION_SEND_REQUEST_BODY.equals(action)) {
            connectorManager.send((SentBody) intent.getSerializableExtra(KEY_SEND_BODY));
        }

        if (CIMPushManager.ACTION_CLOSE_CIM_CONNECTION.equals(action)) {
            connectorManager.close();
        }

        if (CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action)) {
            handleKeepAlive();
        }

        if (CIMPushManager.ACTION_DESTROY_CIM_SERVICE.equals(action)) {
            connectorManager.close();
            this.stopSelf();
        }

        if (CIMPushManager.ACTION_CIM_CONNECTION_PONG.equals(action)) {
            connectorManager.send(Pong.getInstance());
        }

        if (CIMPushManager.ACTION_SET_LOGGER_EATABLE.equals(action)) {
            boolean enable = intent.getBooleanExtra(KEY_LOGGER_ENABLE, true);
            CIMLogger.getLogger().debugMode(enable);
        }

        if (CIMPushManager.ACTION_SHOW_PERSIST_NOTIFICATION.equals(action)) {
            createPersistNotification(intent.getStringExtra(KEY_NOTIFICATION_CHANNEL),
                    intent.getStringExtra(KEY_NOTIFICATION_MESSAGE),
                    intent.getIntExtra(KEY_NOTIFICATION_ICON,0));
            persistHolder.set(true);
        }

        if (CIMPushManager.ACTION_HIDE_PERSIST_NOTIFICATION.equals(action)) {
            stopForeground(true);
            persistHolder.set(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHandler.sendEmptyMessageDelayed(0, 200);
        }

        return super.onStartCommand(intent,flags,startId);
    }

    private void prepareConnect(long delayMillis) {

        if (delayMillis <= 0) {
            this.prepareConnect();
            return;
        }


        connectHandler.removeMessages(0);

        connectHandler.sendEmptyMessageDelayed(0, delayMillis);
    }

    private void prepareConnect() {

        if (CIMPushManager.isDestroyed(this) || CIMPushManager.isStopped(this)) {
            return;
        }

        String host = CIMCacheManager.getString(this, CIMCacheManager.KEY_CIM_SERVER_HOST);
        int port = CIMCacheManager.getInt(this, CIMCacheManager.KEY_CIM_SERVER_PORT);

        if (host == null || host.trim().length() == 0 || port <= 0) {
            Log.e(this.getClass().getSimpleName(), "Invalid hostname or port. host:" + host + " port:" + port);
            return;
        }

        connectorManager.connect(host, port);

    }

    private void handleKeepAlive() {

        CIMLogger.getLogger().connectState(true, CIMPushManager.isStopped(this), CIMPushManager.isDestroyed(this));

        if (connectorManager.isConnected()) {
            connectorManager.sendHeartbeat();
            return;
        }

        this.prepareConnect();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {

        connectHandler.removeMessages(0);

        stopForeground(true);

        persistHolder.set(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            unregisterReceiver(keepAliveReceiver);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    private void createNotification() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        if (notificationManager.getNotificationChannel(TRANSIENT_NTC_CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(TRANSIENT_NTC_CHANNEL_ID, getClass().getSimpleName(), NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new Notification.Builder(this,TRANSIENT_NTC_CHANNEL_ID)
                .setContentTitle(CIMPushService.class.getSimpleName())
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }


    private void createPersistNotification(String channelName ,String message,int icon) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(PERSIST_NTC_CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(PERSIST_NTC_CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            channel.setShowBadge(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }


        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.setPackage(getPackageName());

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder = new Notification.Builder(this,PERSIST_NTC_CHANNEL_ID);
        }else {
            builder = new Notification.Builder(this);
        }

        builder.setAutoCancel(false)
                .setOngoing(false)
                .setSmallIcon(icon)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE))
                .setContentTitle(channelName)
                .setContentText(message);

        startForeground(PERSIST_NOTIFICATION_ID, builder.build());
    }

    private class KeepAliveBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleKeepAlive();
        }

        public IntentFilter getIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            return intentFilter;
        }

    }
}
