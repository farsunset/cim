/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.ichat.example.receiver;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;

import com.farsunset.cim.sdk.android.CIMEventBroadcastReceiver;
import com.farsunset.cim.sdk.android.CIMListenerManager;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
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
	public void onMessageReceived(Message message) {
		
        //调用分发消息监听
		CIMListenerManager.notifyOnMessageReceived(message);
		
		//以开头的为动作消息，无须显示,如被强行下线消息Constant.TYPE_999
		if(message.getType().startsWith("9"))
		{
			return ;
		}
		
		showNotify(context,message);
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
	public void onNetworkChanged(NetworkInfo info) {
		CIMListenerManager.notifyOnNetworkChanged(info);
	}


	@Override
	public void onConnectionSuccessed(boolean hasAutoBind) {
		CIMListenerManager.notifyOnConnectionSuccessed(hasAutoBind);
	}

	@Override
	public void onConnectionClosed() {
		CIMListenerManager.notifyOnConnectionClosed();
	}


	@Override
	public void onReplyReceived(ReplyBody body) {
		CIMListenerManager.notifyOnReplyReceived(body);
	}



	@Override
	public void onConnectionFailed(Exception arg0) {
		// TODO Auto-generated method stub
		CIMListenerManager.notifyOnConnectionFailed(arg0);
	}
 
}
