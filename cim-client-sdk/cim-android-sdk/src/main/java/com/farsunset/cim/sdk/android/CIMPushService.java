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
import com.farsunset.cim.sdk.android.constant.BundleKey;
import com.farsunset.cim.sdk.android.constant.IntentAction;
import com.farsunset.cim.sdk.android.constant.ServiceAction;
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

    private static final String TRANSIENT_NTC_CHANNEL_ID = "CIM_PUSH_TRANSIENT_NTC_ID";
    private static final String PERSIST_NTC_CHANNEL_ID = "CIM_PUSH_PERSIST_NTC_ID";

    private static final int NOTIFICATION_ID = Integer.MAX_VALUE;

    private CIMConnectManager connectManager;

    private KeepAliveBroadcastReceiver keepAliveReceiver;
    private ConnectivityManager connectivityManager;
    private NotificationManager notificationManager;
    private final AtomicBoolean persistHolder = new AtomicBoolean(false);

    @Override
    public void onCreate() {

        connectManager = new CIMConnectManager(this);

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
            sendBroadcast(new Intent(IntentAction.ACTION_NETWORK_CHANGED));
            handleKeepAlive();
        }

        @Override
        public void onLost(Network network) {
            sendBroadcast(new Intent(IntentAction.ACTION_NETWORK_CHANGED));
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

        Intent newIntent = intent == null ? new Intent(ServiceAction.ACTION_ACTIVATE_PUSH_SERVICE) : intent;

        String action = newIntent.getAction();

        createTransientNotification(newIntent);

        if (ServiceAction.ACTION_CREATE_CIM_CONNECTION.equals(action)) {
            this.prepareConnect(newIntent.getLongExtra(BundleKey.KEY_DELAYED_TIME, 0));
        }

        if (ServiceAction.ACTION_SEND_REQUEST_BODY.equals(action)) {
            connectManager.send((SentBody) newIntent.getSerializableExtra(BundleKey.KEY_SEND_BODY));
        }

        if (ServiceAction.ACTION_CLOSE_CIM_CONNECTION.equals(action)) {
            connectManager.close();
        }

        if (ServiceAction.ACTION_ACTIVATE_PUSH_SERVICE.equals(action)) {
            handleKeepAlive();
        }

        if (ServiceAction.ACTION_DESTROY_CIM_SERVICE.equals(action)) {
            connectManager.close();
            this.stopSelf();
        }

        if (ServiceAction.ACTION_CIM_CONNECTION_PONG.equals(action)) {
            connectManager.send(Pong.getInstance());
        }

        if (ServiceAction.ACTION_SET_LOGGER_EATABLE.equals(action)) {
            boolean enable = newIntent.getBooleanExtra(BundleKey.KEY_LOGGER_ENABLE, true);
            CIMLogger.getLogger().debugMode(enable);
        }

        if (ServiceAction.ACTION_SHOW_PERSIST_NOTIFICATION.equals(action)) {
            createPersistNotification(newIntent.getStringExtra(BundleKey.KEY_NOTIFICATION_CHANNEL),
                    newIntent.getStringExtra(BundleKey.KEY_NOTIFICATION_MESSAGE),
                    newIntent.getIntExtra(BundleKey.KEY_NOTIFICATION_ICON,0));
            persistHolder.set(true);
        }

        if (ServiceAction.ACTION_HIDE_PERSIST_NOTIFICATION.equals(action)) {
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

        connectManager.connect(host, port);

    }

    private void handleKeepAlive() {

        CIMLogger.getLogger().connectState(true, CIMPushManager.isStopped(this), CIMPushManager.isDestroyed(this));

        if (connectManager.isConnected()) {
            connectManager.pong();
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

        connectHandler.removeMessages(0);

        stopForeground(true);

        persistHolder.set(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            unregisterReceiver(keepAliveReceiver);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(PERSIST_NTC_CHANNEL_ID) != null) {
            notificationManager.deleteNotificationChannel(TRANSIENT_NTC_CHANNEL_ID);
        }

    }

    private void createTransientNotification(Intent intent) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || persistHolder.get()) {
            return;
        }


        if (ServiceAction.ACTION_SHOW_PERSIST_NOTIFICATION.equals(intent.getAction())
                || ServiceAction.ACTION_HIDE_PERSIST_NOTIFICATION.equals(intent.getAction())) {
            return;
        }

        if (notificationManager.getNotificationChannel(PERSIST_NTC_CHANNEL_ID) != null) {

            int icon = CIMCacheManager.getInt(this,CIMCacheManager.KEY_NTC_CHANNEL_ICON);
            String title = CIMCacheManager.getString(this,CIMCacheManager.KEY_NTC_CHANNEL_NAME);
            String message = CIMCacheManager.getString(this,CIMCacheManager.KEY_NTC_CHANNEL_MESSAGE);

            Notification notification = makeNotification(PERSIST_NTC_CHANNEL_ID,icon,title,message);

            startForeground(NOTIFICATION_ID,  notification);
            return;
        }


        if (notificationManager.getNotificationChannel(TRANSIENT_NTC_CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(TRANSIENT_NTC_CHANNEL_ID, getClass().getSimpleName(), NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = makeNotification(TRANSIENT_NTC_CHANNEL_ID,0,CIMPushService.class.getSimpleName(),null);

        startForeground(NOTIFICATION_ID,  notification);

    }


    private void createPersistNotification(String channelName ,String message,int icon) {

        CIMCacheManager.putString(this,CIMCacheManager.KEY_NTC_CHANNEL_NAME,channelName);
        CIMCacheManager.putString(this,CIMCacheManager.KEY_NTC_CHANNEL_MESSAGE,message);
        CIMCacheManager.putInt(this,CIMCacheManager.KEY_NTC_CHANNEL_ICON,icon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(PERSIST_NTC_CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(PERSIST_NTC_CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            channel.setShowBadge(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = makeNotification(PERSIST_NTC_CHANNEL_ID,icon,channelName,message);

        startForeground(NOTIFICATION_ID,notification);
    }


    private Notification makeNotification(String channel,int icon,String title,String message){

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder = new Notification.Builder(this,channel);
        }else {
            builder = new Notification.Builder(this);
        }

        builder.setAutoCancel(false)
                .setOngoing(false)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(getPendingIntent())
                .setContentTitle(title)
                .setContentText(message);

        if (icon > 0){
            builder.setSmallIcon(icon);
        }

        return builder.build();
    }

    private PendingIntent getPendingIntent(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.setPackage(getPackageName());
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
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
            intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
            intentFilter.addAction(IntentAction.ACTION_CONNECTION_RECOVERY);
            return intentFilter;
        }

    }
}
