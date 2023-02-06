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
import com.farsunset.cim.sdk.android.constant.BundleKey;
import com.farsunset.cim.sdk.android.constant.IntentAction;
import com.farsunset.cim.sdk.android.constant.RequestKey;
import com.farsunset.cim.sdk.android.constant.ServiceAction;
import com.farsunset.cim.sdk.android.logger.CIMLogger;
import com.farsunset.cim.sdk.android.model.SentBody;

import java.util.Locale;
import java.util.UUID;

/**
 * CIM客户端sdk功能接口
 */
public class CIMPushManager {


    /**
     * 初始化,连接服务端，在程序启动页或者 在Application里调用
     * @param context
     * @param host cim服务端IP或者域名
     * @param port cim服务端端口
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
        serviceIntent.setAction(ServiceAction.ACTION_CREATE_CIM_CONNECTION);
        startService(context, serviceIntent);

    }

    /**
     * 设置SDK日志打印开关
     * @param context
     * @param enable
     */
    public static void setLoggerEnable(Context context, boolean enable) {
        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(BundleKey.KEY_LOGGER_ENABLE, enable);
        serviceIntent.setAction(ServiceAction.ACTION_SET_LOGGER_EATABLE);
        startService(context, serviceIntent);
    }


    /**
     * 开启常驻通知栏
     * @param context
     * @param icon 通知图标
     * @param channel 通知channel
     * @param message 显示内容
     */
    public static void startForeground(Context context,int icon, String channel , String message) {
        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(BundleKey.KEY_NOTIFICATION_MESSAGE, message);
        serviceIntent.putExtra(BundleKey.KEY_NOTIFICATION_CHANNEL, channel);
        serviceIntent.putExtra(BundleKey.KEY_NOTIFICATION_ICON, icon);
        serviceIntent.setAction(ServiceAction.ACTION_SHOW_PERSIST_NOTIFICATION);
        startService(context, serviceIntent);
    }

    /**
     * 关闭常驻通知栏
     * @param context
     */
    public static void cancelForeground(Context context) {
        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.setAction(ServiceAction.ACTION_HIDE_PERSIST_NOTIFICATION);
        startService(context, serviceIntent);
    }

    /**
     * bind账户
     * 通知服务端 长连接和uid进行关联
     * @param context
     * @param uid 用户标识
     */
    public static void bind(Context context, long uid) {
        bind(context,String.valueOf(uid));
    }

    /**
     * bind账户
     * 通知服务端 长连接和uid进行关联
     * @param context
     * @param uid 用户标识
     */
    public static void bind(Context context, String uid) {

        if (isDestroyed(context)) {
            return;
        }

        sendBindRequest(context, uid);
    }

    /**
     * 通知服务端给当前长连接设置tag
     * @param context
     * @param tag 标识
     */
    public static void setTag(Context context, String tag) {

        SentBody sent = new SentBody();
        sent.setKey(RequestKey.CLIENT_SET_TAG);
        sent.put("tag", tag);
        sendRequest(context, sent);

    }

    /**
     * 通知服务端清除tag
     * @param context
     */
    public static void removeTag(Context context) {

        SentBody sent = new SentBody();
        sent.setKey(RequestKey.CLIENT_REMOVE_TAG);
        sendRequest(context, sent);

    }

    /**
     * 长连接发送一次心跳响应
     * @param context
     */
    public static void pong(Context context) {
        if (isDestroyed(context) || isStopped(context)) {
            return;
        }

        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.setAction(ServiceAction.ACTION_CIM_CONNECTION_PONG);
        startService(context, serviceIntent);
    }



    private static void sendBindRequest(Context context, String uid) {

        CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_MANUAL_STOP, false);
        CIMCacheManager.putString(context, CIMCacheManager.KEY_UID, uid);

        SentBody sent = new SentBody();
        sent.setKey(RequestKey.CLIENT_BIND);
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
     * 向服务端发送一次自定义业务请求
     * @param context
     * @param body 请求体
     */
    public static void sendRequest(Context context, SentBody body) {

        if (isDestroyed(context) || isStopped(context)) {
            return;
        }

        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(BundleKey.KEY_SEND_BODY, body);
        serviceIntent.setAction(ServiceAction.ACTION_SEND_REQUEST_BODY);
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
        serviceIntent.setAction(ServiceAction.ACTION_CLOSE_CIM_CONNECTION);
        startService(context, serviceIntent);

    }

    /**
     * 完全销毁CIM，一般用于完全退出程序，调用resume将不能恢复
     */
    public static void destroy(Context context) {

        CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_DESTROYED, true);
        CIMCacheManager.remove(context, CIMCacheManager.KEY_UID);

        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.setAction(ServiceAction.ACTION_DESTROY_CIM_SERVICE);
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

    /**
     * 获取sdk是否已经销毁的
     * @param context
     * @return
     */
    public static boolean isDestroyed(Context context) {
        return CIMCacheManager.getBoolean(context, CIMCacheManager.KEY_CIM_DESTROYED);
    }

    /**
     * 判断是否暂停接收消息
     * @param context
     * @return
     */
    public static boolean isStopped(Context context) {
        return CIMCacheManager.getBoolean(context, CIMCacheManager.KEY_MANUAL_STOP);
    }

    /**
     * 判断于服务端连接是否正常
     * @param context
     */
    public static boolean isConnected(Context context) {
        return CIMCacheManager.getBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE);
    }

    /**
     * 判断客户端网络连接是否正常
     * @param context
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 获取服务端网络信息
     * @param context
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    protected static void startService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent);
            return;
        }

        try {
            context.startForegroundService(intent);
        }catch (Exception ignore){
            context.sendBroadcast(new Intent(IntentAction.ACTION_CONNECTION_RECOVERY));
        }
    }


    private static String getVersionName(Context context) {

        try {
            PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return mPackageInfo.versionName;
        } catch (NameNotFoundException ignore) {}
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
