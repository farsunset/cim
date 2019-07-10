/**
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

import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import com.farsunset.cim.sdk.android.coder.CIMLogger;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.SentBody;

/**
 * CIM 功能接口
 */
public class CIMPushManager {

	protected static String ACTION_ACTIVATE_PUSH_SERVICE = "ACTION_ACTIVATE_PUSH_SERVICE";

	protected static String ACTION_CREATE_CIM_CONNECTION = "ACTION_CREATE_CIM_CONNECTION";

	protected static String ACTION_SEND_REQUEST_BODY = "ACTION_SEND_REQUEST_BODY";

	protected static String ACTION_CLOSE_CIM_CONNECTION = "ACTION_CLOSE_CIM_CONNECTION";

	protected static String ACTION_SET_LOGGER_EANABLE = "ACTION_SET_LOGGER_EANABLE";

	protected static String KEY_SEND_BODY = "KEY_SEND_BODY";

	protected static String KEY_CIM_CONNECTION_STATUS = "KEY_CIM_CONNECTION_STATUS";

	/**
	 * 初始化,连接服务端，在程序启动页或者 在Application里调用
	 * 
	 * @param context
	 * @param ip
	 * @param port
	 */
	public static void connect(Context context, String host, int port) {

		if(TextUtils.isEmpty(host) || port == 0) {
			CIMLogger.getLogger().invalidHostPort(host, port);
			return;
		}
		

		CIMCacheManager.putString(context, CIMCacheManager.KEY_CIM_SERVIER_HOST, host);
		CIMCacheManager.putInt(context, CIMCacheManager.KEY_CIM_SERVIER_PORT, port);
		
		CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_DESTROYED, false);
		CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_MANUAL_STOP, false);
		
		CIMCacheManager.remove(context, CIMCacheManager.KEY_ACCOUNT);
 

		Intent serviceIntent = new Intent(context, CIMPushService.class);
		serviceIntent.setAction(ACTION_CREATE_CIM_CONNECTION);
		startService(context,serviceIntent);
		
	}

	public static void setLoggerEnable(Context context,boolean enable) {
		Intent serviceIntent = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(CIMPushService.KEY_LOGGER_ENABLE, enable);
		serviceIntent.setAction(ACTION_SET_LOGGER_EANABLE);
		startService(context,serviceIntent);
	}
	

	/**
	 * 设置一个账号登录到服务端
	 * 
	 * @param account
	 *            用户唯一ID
	 */
	public static void bindAccount(Context context, String account) {

		if (isDestoryed(context) || account == null || account.trim().length() == 0) {
			return;
		}

		sendBindRequest(context, account);

	}

	private static void sendBindRequest(Context context, String account) {

		CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_MANUAL_STOP, false);
		CIMCacheManager.putString(context, CIMCacheManager.KEY_ACCOUNT, account);

		String deviceId = CIMCacheManager.getString(context, CIMCacheManager.KEY_DEVICE_ID);
		if (TextUtils.isEmpty(deviceId)) {
			deviceId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
			CIMCacheManager.putString(context, CIMCacheManager.KEY_DEVICE_ID, deviceId);
		}

		SentBody sent = new SentBody();
		sent.setKey(CIMConstant.RequestKey.CLIENT_BIND);
		sent.put("account", account);
		sent.put("deviceId", deviceId);
		sent.put("channel", "android");
		sent.put("device", android.os.Build.MODEL);
		sent.put("version", getVersionName(context));
		sent.put("osVersion", android.os.Build.VERSION.RELEASE);
		sent.put("packageName", context.getPackageName());
		sendRequest(context, sent);
	}

	protected static boolean autoBindAccount(Context context) {

		String account = CIMCacheManager.getString(context, CIMCacheManager.KEY_ACCOUNT);
		if (account == null || account.trim().length() == 0 || isDestoryed(context)) {
			return false;
		}

		sendBindRequest(context, account);

		return true;
	}

	/**
	 * 发送一个CIM请求
	 * 
	 * @param context
	 * @body
	 */
	public static void sendRequest(Context context, SentBody body) {

		if (isDestoryed(context) || isStoped(context)) {
			return;
		}

		Intent serviceIntent = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(KEY_SEND_BODY, body);
		serviceIntent.setAction(ACTION_SEND_REQUEST_BODY);
		startService(context,serviceIntent);

	}

	/**
	 * 停止接受推送，将会退出当前账号登录，端口与服务端的连接
	 * 
	 * @param context
	 */
	public static void stop(Context context) {

		if (isDestoryed(context)) {
			return;
		}

		CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_MANUAL_STOP, true);

		Intent serviceIntent = new Intent(context, CIMPushService.class);
		serviceIntent.setAction(ACTION_CLOSE_CIM_CONNECTION);
		startService(context,serviceIntent);

	}

	/**
	 * 完全销毁CIM，一般用于完全退出程序，调用resume将不能恢复
	 * 
	 * @param context
	 */
	public static void destroy(Context context) {

		CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_DESTROYED, true);
		CIMCacheManager.putString(context, CIMCacheManager.KEY_ACCOUNT, null);

		context.stopService(new Intent(context, CIMPushService.class));

	}

	/**
	 * 重新恢复接收推送，重新连接服务端，并登录当前账号
	 * 
	 * @param context
	 */
	public static void resume(Context context) {

		if (isDestoryed(context)) {
			return;
		}

		autoBindAccount(context);
	}

	public static boolean isDestoryed(Context context) {
		return CIMCacheManager.getBoolean(context, CIMCacheManager.KEY_CIM_DESTROYED);
	}
	
	public static boolean isStoped(Context context) {
		return CIMCacheManager.getBoolean(context, CIMCacheManager.KEY_MANUAL_STOP);
	}
	
	public static boolean isConnected(Context context) {
		return CIMCacheManager.getBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE);
	}
	
	public static boolean isNetworkConnected(Context context) {
		 NetworkInfo networkInfo =  getNetworkInfo(context);
		 return networkInfo != null && networkInfo.isConnected();
	}

	public static NetworkInfo getNetworkInfo(Context context) {
		 return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
	}
	

	public static void startService(Context context,Intent intent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(intent);
        } else {
        	context.startService(intent);
        }
	}
	
	
	
	private static String getVersionName(Context context) {
		String versionName = null;
		try {
			PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionName = mPackageInfo.versionName;
		} catch (NameNotFoundException ignore) {
		}
		return versionName;
	}
	
	

}
