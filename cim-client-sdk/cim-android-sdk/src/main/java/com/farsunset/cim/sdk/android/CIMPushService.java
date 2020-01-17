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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
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
import com.farsunset.cim.sdk.android.coder.CIMLogger;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.SentBody;

/**
 * 与服务端连接服务
 * 
 * @author 3979434
 *
 */
public class CIMPushService extends Service  {
	public final static String KEY_DELAYED_TIME = "KEY_DELAYED_TIME";
	public final static String KEY_LOGGER_ENABLE = "KEY_LOGGER_ENABLE";

	private final static int NOTIFICATION_ID = Integer.MAX_VALUE;
	
	private CIMConnectorManager manager;
	private KeepAliveBroadcastReceiver keepAliveReceiver;
    private ConnectivityManager connectivityManager;
	@Override
	public void onCreate() {
		manager = CIMConnectorManager.getManager(this.getApplicationContext());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			keepAliveReceiver = new KeepAliveBroadcastReceiver();
			registerReceiver(keepAliveReceiver, keepAliveReceiver.getIntentFilter());
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

			connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			
			connectivityManager.registerDefaultNetworkCallback(networkCallback);

		}
	}
	
	ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
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

	Handler connectHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message message) {
			connect();
		}
	};

	Handler notificationHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message message) {
			stopForeground(true);
		}
	};
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationChannel channel = new NotificationChannel(getClass().getSimpleName(),getClass().getSimpleName(), NotificationManager.IMPORTANCE_LOW);
			channel.enableLights(false);
			channel.enableVibration(false);
			channel.setSound(null, null);
		    notificationManager.createNotificationChannel(channel);
		    Notification notification = new Notification.Builder(this, channel.getId())
		    		.setContentTitle("Push service")
		    		.setContentText("Push service is running")
		    		.build();
	        startForeground(NOTIFICATION_ID,notification);
		}

		String action = intent == null ? CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE : intent.getAction();

		if (CIMPushManager.ACTION_CREATE_CIM_CONNECTION.equals(action)) {
			connect(intent.getLongExtra(KEY_DELAYED_TIME, 0));
		}

		if (CIMPushManager.ACTION_SEND_REQUEST_BODY.equals(action)) {
			manager.send((SentBody) intent.getSerializableExtra(CIMPushManager.KEY_SEND_BODY));
		}

		if (CIMPushManager.ACTION_CLOSE_CIM_CONNECTION.equals(action)) {
			manager.closeSession();
		}

		if (CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action)) {
			handleKeepAlive();
		}

		if (CIMPushManager.ACTION_SET_LOGGER_EATABLE.equals(action)) {
			boolean enable = intent.getBooleanExtra(KEY_LOGGER_ENABLE, true);
			CIMLogger.getLogger().debugMode(enable);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			notificationHandler.sendEmptyMessageDelayed(0, 1000);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void connect(long delayMillis) {

		if(delayMillis <= 0) {
			connect();
			return;
		}
		
		connectHandler.sendEmptyMessageDelayed(0, delayMillis);

	}

	private void connect() {

		if(CIMPushManager.isDestroyed(this) || CIMPushManager.isStopped(this)) {
			return;
		}
		
		String host = CIMCacheManager.getString(this, CIMCacheManager.KEY_CIM_SERVER_HOST);
		int port = CIMCacheManager.getInt(this, CIMCacheManager.KEY_CIM_SERVER_PORT);
	
		if(host == null || host.trim().length() == 0 || port <= 0) {
			Log.e(this.getClass().getSimpleName(), "Invalid hostname or port. host:" + host  + " port:" + port);
			return;
		}
		
		manager.connect(host, port);

	}
 
	private void handleKeepAlive() {
		
		if (manager.isConnected()) {
			CIMLogger.getLogger().connectState(true);
			return;
		}
		
		connect();
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		manager.destroy();
		connectHandler.removeMessages(0);
		notificationHandler.removeMessages(0);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			unregisterReceiver(keepAliveReceiver);
			
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			connectivityManager.unregisterNetworkCallback(networkCallback);
		}
	}

	public class KeepAliveBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
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
