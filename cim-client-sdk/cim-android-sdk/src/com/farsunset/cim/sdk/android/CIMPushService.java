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
import android.os.Message;

import java.util.concurrent.Semaphore;

import com.farsunset.cim.sdk.android.coder.CIMLogger;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.SentBody;

/**
 * 与服务端连接服务
 * 
 * @author 3979434
 *
 */
public class CIMPushService extends Service {
	public final static String KEY_DELAYED_TIME = "KEY_DELAYED_TIME";
	public final static String KEY_LOGGER_ENABLE = "KEY_LOGGER_ENABLE";

	private CIMConnectorManager manager;
	private KeepAliveBroadcastReceiver keepAliveReceiver;
	private Semaphore semaphore = new Semaphore(1, true);

	@Override
	public void onCreate() {
		manager = CIMConnectorManager.getManager(this.getApplicationContext());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			keepAliveReceiver = new KeepAliveBroadcastReceiver();
			registerReceiver(keepAliveReceiver, keepAliveReceiver.getIntentFilter());
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
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
				
			});

		}
	}

	Handler connectionHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message message) {
			String host = message.getData().getString(CIMCacheManager.KEY_CIM_SERVIER_HOST);
			int port = message.getData().getInt(CIMCacheManager.KEY_CIM_SERVIER_PORT, 0);
			manager.connect(host, port);
			semaphore.release();
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationChannel channel = new NotificationChannel(getClass().getSimpleName(),getClass().getSimpleName(), NotificationManager.IMPORTANCE_LOW);
			channel.enableLights(false);
			channel.enableVibration(false);
		    notificationManager.createNotificationChannel(channel);
		    Notification notification = new Notification.Builder(this, channel.getId()).build();
	        startForeground(this.hashCode(),notification);
		}

		intent = (intent == null ? new Intent(CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE) : intent);

		String action = intent.getAction();

		if (CIMPushManager.ACTION_CREATE_CIM_CONNECTION.equals(action)) {
			handleConnection(intent);
		}

		if (CIMPushManager.ACTION_SEND_REQUEST_BODY.equals(action)) {
			manager.send((SentBody) intent.getSerializableExtra(CIMPushManager.KEY_SEND_BODY));
		}

		if (CIMPushManager.ACTION_CLOSE_CIM_CONNECTION.equals(action)) {
			manager.closeSession();
		}

		if (CIMPushManager.ACTION_DESTORY.equals(action)) {
			manager.destroy();
			this.stopSelf();
		}

		if (CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action)) {
			handleKeepAlive();
		}

		if (CIMPushManager.ACTION_SET_LOGGER_EANABLE.equals(action)) {
			boolean enable = intent.getBooleanExtra(KEY_LOGGER_ENABLE, true);
			CIMLogger.getLogger().debugMode(enable);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			stopForeground(true);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void handleConnection(Intent intent) {

		long delayMillis = intent.getLongExtra(KEY_DELAYED_TIME, 0);

		if (delayMillis <= 0) {
			String host = intent.getStringExtra(CIMCacheManager.KEY_CIM_SERVIER_HOST);
			int port = intent.getIntExtra(CIMCacheManager.KEY_CIM_SERVIER_PORT, 0);
			manager.connect(host, port);
			return;
		}

		if (!semaphore.tryAcquire()) {
			return;
		}

		Message msg = connectionHandler.obtainMessage();
		msg.what = 0;
		msg.setData(intent.getExtras());
		connectionHandler.sendMessageDelayed(msg, delayMillis);

	}

	private void handleKeepAlive() {
		if (manager.isConnected()) {
			CIMLogger.getLogger().connectState(true);
			return;
		}

		boolean isManualStop = CIMCacheManager.getBoolean(getApplicationContext(), CIMCacheManager.KEY_MANUAL_STOP);
		boolean isDestroyed = CIMCacheManager.getBoolean(getApplicationContext(), CIMCacheManager.KEY_CIM_DESTROYED);

		CIMLogger.getLogger().connectState(false, isManualStop, isDestroyed);

		CIMPushManager.connect(this, 0);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			unregisterReceiver(keepAliveReceiver);
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
			intentFilter.addAction(Intent.ACTION_SCREEN_ON);
			intentFilter.addAction(Intent.ACTION_USER_PRESENT);
			return intentFilter;
		}

	}

}
