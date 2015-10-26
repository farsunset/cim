package com.farsunset.ichat.example.receiver;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.util.Log;

import com.farsunset.cim.client.android.CIMEventBroadcastReceiver;
import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.model.Message;
import com.farsunset.cim.client.model.ReplyBody;
import com.farsunset.ichat.example.R;
import com.farsunset.ichat.example.ui.SystemMessageActivity;
 
/**
 *  消息入口，所有消息都会经过这里
 * @author 3979434
 *
 */
public final class CustomCIMMessageReceiver extends CIMEventBroadcastReceiver {
 
	private NotificationManager notificationManager;
 
	
	
	//当收到消息时，会执行onMessageReceived，这里是消息第一入口
	@Override
	public void onMessageReceived(com.farsunset.cim.client.model.Message message) {
		
        
		for (int index = 0 ;index<CIMListenerManager.getCIMListeners().size();index++) {
			
			Log.i(this.getClass().getSimpleName(), "########################"+ (CIMListenerManager.getCIMListeners().get(index).getClass().getName() + ".onMessageReceived################"));
			
			CIMListenerManager.getCIMListeners().get(index).onMessageReceived(message);
		}
		
		//以开头的为动作消息，无须显示,如被强行下线消息Constant.TYPE_999
		if(message.getType().startsWith("9"))
		{
			return ;
		}
		if(isInBackground(context))
		{
			showNotify(context,message);
		}
	}


	//当手机网络连接状态变化时，会执行onNetworkChanged 
	@Override
	public void onNetworkChanged(NetworkInfo info) {
		for (int index = 0 ;index<CIMListenerManager.getCIMListeners().size();index++) {
			CIMListenerManager.getCIMListeners().get(index).onNetworkChanged(info);
		}
	}

	//当收到sendbody的响应时，会执行onReplyReceived 
	@Override
	public void onReplyReceived(ReplyBody body) {
		for (int index = 0 ;index<CIMListenerManager.getCIMListeners().size();index++) {
			CIMListenerManager.getCIMListeners().get(index).onReplyReceived(body);
		}
	}
	 
	
	private void  showNotify(Context context , Message msg)
	{
				
		this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		String title  = "系统消息";
        
		Notification notification = new Notification(R.drawable.icon_notify,title,msg.getTimestamp());
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		Intent notificationIntent = new Intent(context,SystemMessageActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context,1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		notification.setLatestEventInfo(context,title,  msg.getContent(),contentIntent);
		notificationManager.notify(R.drawable.icon_notify, notification);

	}

	@Override
	public void onCIMConnectionSucceed() {
		for (int index = 0 ;index<CIMListenerManager.getCIMListeners().size();index++) {
			CIMListenerManager.getCIMListeners().get(index).onCIMConnectionSucceed();
		}
	}


	@Override
	public void onConnectionStatus(boolean arg0) {
		for (int index = 0 ;index<CIMListenerManager.getCIMListeners().size();index++) {
			CIMListenerManager.getCIMListeners().get(index).onConnectionStatus(arg0);
		}
	}


	@Override
	public void onCIMConnectionClosed() {
		// TODO Auto-generated method stub
		for (int index = 0 ;index<CIMListenerManager.getCIMListeners().size();index++) {
			CIMListenerManager.getCIMListeners().get(index).onCIMConnectionSucceed();
		}
	}

 
 
}
