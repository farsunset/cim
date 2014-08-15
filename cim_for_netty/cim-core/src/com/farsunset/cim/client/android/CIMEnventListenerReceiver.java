package com.farsunset.cim.client.android;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.farsunset.cim.nio.constant.CIMConstant;
import com.farsunset.cim.nio.mutual.Message;
import com.farsunset.cim.nio.mutual.ReplyBody;
import com.farsunset.cim.nio.mutual.SentBody;
/**
 *  消息入口，所有消息都会经过这里
 * @author 3979434
 *
 */
public  abstract  class CIMEnventListenerReceiver extends BroadcastReceiver implements OnCIMMessageListener {
 

	public Context context;
	@Override
	public void onReceive(Context ctx, Intent it) {

		  context = ctx;
		  
		  if(it.getAction().equals(CIMConnectorManager.ACTION_NETWORK_CHANGED))
          {
			  ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
	          android.net.NetworkInfo info = connectivityManager.getActiveNetworkInfo();
	        
	          onDevicesNetworkChanged(info);
          }
		
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_CLOSED))
          {
        	  dispatchConnectionClosed();
          }
          
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_FAILED))
          {
        	  onConnectionFailed((Exception) it.getSerializableExtra("exception"));
          }
          
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_SUCCESS))
          {
        	  dispatchConnectionSucceed();
          }
          
          if(it.getAction().equals(CIMConnectorManager.ACTION_MESSAGE_RECEIVED))
          {
        	  filterType999Message((Message)it.getSerializableExtra("message"));
          }
          
          if(it.getAction().equals(CIMConnectorManager.ACTION_REPLY_RECEIVED))
          {
        	  onReplyReceived((ReplyBody)it.getSerializableExtra("replyBody"));
          }
          
          if(it.getAction().equals(CIMConnectorManager.ACTION_SENT_FAILED))
          {
        	  onSentFailed((Exception) it.getSerializableExtra("exception"),(SentBody)it.getSerializableExtra("sentBody"));
          }
          
          if(it.getAction().equals(CIMConnectorManager.ACTION_SENT_SUCCESS))
          {
        	  onSentSucceed((SentBody)it.getSerializableExtra("sentBody"));
          }

          if(it.getAction().equals(CIMConnectorManager.ACTION_UNCAUGHT_EXCEPTION))
          {
        	  onUncaughtException((Exception)it.getSerializableExtra("exception"));
          }
          
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_STATUS))
          {
        	  onConnectionStatus(it.getBooleanExtra(CIMPushManager.KEY_CIM_CONNECTION_STATUS, false));
          }
          
          
	}

	
	private void dispatchConnectionClosed() {
		
		if(CIMConnectorManager.netWorkAvailable(context))
		{
			CIMPushManager.init(context);
		}
		
		onConnectionClosed();
	}


	protected boolean isInBackground(Context context) {
		List<RunningTaskInfo> tasksInfo = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
		if (tasksInfo.size() > 0) {

			if (context.getPackageName().equals(
					tasksInfo.get(0).topActivity.getPackageName())) {

				return false;
			}
		}
		return true;
	}

	private   void onConnectionFailed(Exception e){
		
		if(CIMConnectorManager.netWorkAvailable(context))
		{
			CIMPushManager.init(context);
		}
	}
	
	private void dispatchConnectionSucceed() {
		
		CIMPushManager.setAccount(context);
		onConnectionSucceed();
		
	}

 

	private void onUncaughtException(Throwable arg0) {}

	

	private  void onDevicesNetworkChanged(NetworkInfo info) {
		
		if(info !=null)
		{
			CIMPushManager.init(context);
		} 
		
		onNetworkChanged(info);
	}

	private void filterType999Message(com.farsunset.cim.nio.mutual.Message message)
	{
		if(CIMConstant.MessageType.TYPE_999.equals(message.getType()))
		{
			CIMDataConfig.putBoolean(context,CIMDataConfig.KEY_MANUAL_STOP,true);
		}
		
		onMessageReceived(message);
	}
	
	private   void onSentFailed(Exception e, SentBody body){
		
		//与服务端端开链接，重新连接
		if(e instanceof CIMSessionDisableException)
		{
			CIMPushManager.init(context);
		}else
		{
			//发送失败 重新发送
			CIMPushManager.sendRequest(context, body);
		}
		
	}

	private  void onSentSucceed(SentBody body){}
	
	@Override
	public abstract void onMessageReceived(com.farsunset.cim.nio.mutual.Message message);
	@Override
	public abstract void onReplyReceived(ReplyBody body);
	
	public abstract void onNetworkChanged(NetworkInfo info);
	 
}
