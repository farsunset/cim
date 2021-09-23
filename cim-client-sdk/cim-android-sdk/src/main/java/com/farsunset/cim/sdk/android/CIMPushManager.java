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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import com.farsunset.cim.sdk.android.logger.CIMLogger;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.SentBody;

import java.util.Locale;
import java.util.UUID;

/**
 * CIM 功能接口
 */
public class CIMPushManager {


    protected static final String ACTION_CREATE_CIM_CONNECTION = "ACTION_CREATE_CIM_CONNECTION";

    protected static final String ACTION_DESTROY_CIM_SERVICE = "ACTION_DESTROY_CIM_SERVICE";

    protected static final String ACTION_ACTIVATE_PUSH_SERVICE = "ACTION_ACTIVATE_PUSH_SERVICE";

    protected static final String ACTION_SEND_REQUEST_BODY = "ACTION_SEND_REQUEST_BODY";

    protected static final String ACTION_CLOSE_CIM_CONNECTION = "ACTION_CLOSE_CIM_CONNECTION";

    protected static final String ACTION_SET_LOGGER_EATABLE = "ACTION_SET_LOGGER_EATABLE";

    protected static final String ACTION_SHOW_PERSIST_NOTIFICATION = "ACTION_SHOW_PERSIST_NOTIFICATION";

    protected static final String ACTION_HIDE_PERSIST_NOTIFICATION = "ACTION_HIDE_PERSIST_NOTIFICATION";

    protected static final String ACTION_CIM_CONNECTION_PONG = "ACTION_CIM_CONNECTION_PONG";

    /**
     * 初始化,连接服务端，在程序启动页或者 在Application里调用
     */
    public static void connect(Context context, String host, int port) {

        if (TextUtils.isEmpty(host) || port == 0) {
            CIMLogger.getLogger().invalidHostPort(host, port);
            return;
        }

        CIMCacheManager.putString(context, CIMCacheManager.KEY_CIM_SERVER_HOST, host);
        CIMCacheManager.putInt(context, CIMCacheManager.KEY_CIM_SERVER_PORT, port);
        CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_DESTROYED, false);
        CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_MANUAL_STOP, false);
        CIMCacheManager.remove(context, CIMCacheManager.KEY_UID);


        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.setAction(ACTION_CREATE_CIM_CONNECTION);
        startService(context, serviceIntent);

    }

    public static void setLoggerEnable(Context context, boolean enable) {
        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(CIMPushService.KEY_LOGGER_ENABLE, enable);
        serviceIntent.setAction(ACTION_SET_LOGGER_EATABLE);
        startService(context, serviceIntent);
    }

    public static void startForeground(Context context,int icon, String channel , String message) {
        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(CIMPushService.KEY_NOTIFICATION_MESSAGE, message);
        serviceIntent.putExtra(CIMPushService.KEY_NOTIFICATION_CHANNEL, channel);
        serviceIntent.putExtra(CIMPushService.KEY_NOTIFICATION_ICON, icon);
        serviceIntent.setAction(ACTION_SHOW_PERSIST_NOTIFICATION);
        startService(context, serviceIntent);
    }

    public static void cancelForeground(Context context) {
        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.setAction(ACTION_HIDE_PERSIST_NOTIFICATION);
        startService(context, serviceIntent);
    }

    /**
     * 设置一个账号登录到服务端
     */

    public static void bind(Context context, long uid) {
        bind(context,String.valueOf(uid));
    }
    public static void bind(Context context, String uid) {

        if (isDestroyed(context)) {
            return;
        }

        sendBindRequest(context, uid);
    }

    public static void setTag(Context context, String tag) {

        SentBody sent = new SentBody();
        sent.setKey(CIMConstant.RequestKey.CLIENT_SET_TAG);
        sent.put("tag", tag);
        sendRequest(context, sent);

    }

    public static void removeTag(Context context) {

        SentBody sent = new SentBody();
        sent.setKey(CIMConstant.RequestKey.CLIENT_REMOVE_TAG);
        sendRequest(context, sent);

    }

    public static void pong(Context context) {
        if (isDestroyed(context) || isStopped(context)) {
            return;
        }

        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.setAction(ACTION_CIM_CONNECTION_PONG);
        startService(context, serviceIntent);
    }

    private static void sendBindRequest(Context context, String uid) {

        CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_MANUAL_STOP, false);
        CIMCacheManager.putString(context, CIMCacheManager.KEY_UID, uid);

        SentBody sent = new SentBody();
        sent.setKey(CIMConstant.RequestKey.CLIENT_BIND);
        sent.put("uid", String.valueOf(uid));
        sent.put("channel", "android");
        sent.put("deviceId", getDeviceId(context));
        sent.put("deviceName", Build.MODEL);
        sent.put("appVersion", getVersionName(context));
        sent.put("osVersion", Build.VERSION.RELEASE);
        sent.put("packageName", context.getPackageName());
        sent.put("language", getLanguage());
        sent.setTimestamp(System.currentTimeMillis());
        sendRequest(context, sent);
    }

    protected static boolean autoBindAccount(Context context) {

        String uid = CIMCacheManager.getString(context, CIMCacheManager.KEY_UID);

        if (uid == null || isDestroyed(context)) {
            return false;
        }

        sendBindRequest(context, uid);

        return true;
    }

    /**
     * 发送一个CIM请求
     */
    public static void sendRequest(Context context, SentBody body) {

        if (isDestroyed(context) || isStopped(context)) {
            return;
        }

        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(CIMPushService.KEY_SEND_BODY, body);
        serviceIntent.setAction(ACTION_SEND_REQUEST_BODY);
        startService(context, serviceIntent);

    }

    /**
     * 停止接受推送，将会退出当前账号登录，端口与服务端的连接
     */
    public static void stop(Context context) {

        if (isDestroyed(context)) {
            return;
        }

        CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_MANUAL_STOP, true);

        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.setAction(ACTION_CLOSE_CIM_CONNECTION);
        startService(context, serviceIntent);

    }

    /**
     * 完全销毁CIM，一般用于完全退出程序，调用resume将不能恢复
     */
    public static void destroy(Context context) {

        CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_DESTROYED, true);
        CIMCacheManager.remove(context, CIMCacheManager.KEY_UID);

        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.setAction(ACTION_DESTROY_CIM_SERVICE);
        startService(context, serviceIntent);

    }

    /**
     * 重新恢复接收推送，重新连接服务端，并登录当前账号
     */
    public static void resume(Context context) {

        if (isDestroyed(context)) {
            return;
        }

        autoBindAccount(context);
    }

    public static boolean isDestroyed(Context context) {
        return CIMCacheManager.getBoolean(context, CIMCacheManager.KEY_CIM_DESTROYED);
    }

    public static boolean isStopped(Context context) {
        return CIMCacheManager.getBoolean(context, CIMCacheManager.KEY_MANUAL_STOP);
    }

    public static boolean isConnected(Context context) {
        return CIMCacheManager.getBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE);
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }


    public static void startService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    private static String getVersionName(Context context) {

        try {
            PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return mPackageInfo.versionName;
        } catch (NameNotFoundException ignore) {
        }
        return null;
    }

    private static String getDeviceId(Context context) {

        String currDeviceId = CIMCacheManager.getString(context, CIMCacheManager.KEY_DEVICE_ID);

        if (!TextUtils.isEmpty(currDeviceId)) {
            return currDeviceId;
        }

        String deviceId = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        CIMCacheManager.putString(context, CIMCacheManager.KEY_DEVICE_ID, deviceId);

        return deviceId;
    }

    private static String getLanguage(){

        Locale locale = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? LocaleList.getDefault().get(0) : Locale.getDefault();

        return locale.getLanguage() + "-" + locale.getCountry();
    }
}
