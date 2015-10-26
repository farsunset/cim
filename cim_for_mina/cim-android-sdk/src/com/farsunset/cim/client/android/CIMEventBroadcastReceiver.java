/**
 * probject:cim-android-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.client.android;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.farsunset.cim.client.constant.CIMConstant;
import com.farsunset.cim.client.exception.CIMSessionDisableException;
import com.farsunset.cim.client.model.Message;
import com.farsunset.cim.client.model.ReplyBody;
import com.farsunset.cim.client.model.SentBody;
/**
 *  消息入口，所有消息都会经过这里
 */
public  abstract  class CIMEventBroadcastReceiver extends BroadcastReceiver implements CIMEventListener {
 

	public Context context;
	@Override
	public void onReceive(Context ctx, Intent it) {

		  context = ctx;
		  
		  /*
		   * 操作事件广播，用于提高service存活率
		   */
		  if(it.getAction().equals(Intent.ACTION_USER_PRESENT)
			 ||it.getAction().equals(Intent.ACTION_POWER_CONNECTED)
			 ||it.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)
			) 	  
          {
			  startPushService();
          }
		  

		  /*
           * 设备网络状态变化事件
           */
		  if(it.getAction().equals(CIMConnectorManager.ACTION_NETWORK_CHANGED))
          {
			  ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	          android.net.NetworkInfo info = connectivityManager.getActiveNetworkInfo();
	          onDevicesNetworkChanged(info);
          }
		  
		  /*
           * cim断开服务器事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_CLOSED))
          {
	        	if(CIMConnectorManager.netWorkAvailable(context))
	      		{
	      			CIMPushManager.init(context);
	      		}
	        	onCIMConnectionClosed();
          }
          
          /*
           * cim连接服务器失败事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_FAILED))
          {
        	  onConnectionFailed((Exception) it.getSerializableExtra("exception"));
          }
          
          /*
           * cim连接服务器成功事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_SUCCESS))
          {
        	  
        	  
        	  CIMPushManager.bindAccount(context);
        	  
        	  onCIMConnectionSucceed();
          }
          
          /*
           * 收到推送消息事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_MESSAGE_RECEIVED))
          {
        	  filterType999Message((Message)it.getSerializableExtra("message"));
          }
          
          
          /*
           * 获取收到replybody成功事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_REPLY_RECEIVED))
          {
        	  onReplyReceived((ReplyBody)it.getSerializableExtra("replyBody"));
          }
          
          
          /*
           * 获取sendbody发送失败事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_SENT_FAILED))
          {
        	  onSentFailed((Exception) it.getSerializableExtra("exception"),(SentBody)it.getSerializableExtra("sentBody"));
          }
          
          /*
           * 获取sendbody发送成功事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_SENT_SUCCESS))
          {
        	  onSentSucceed((SentBody)it.getSerializableExtra("sentBody"));
          }
          
          
          /*
           * 获取cim数据传输异常事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_UNCAUGHT_EXCEPTION))
          {
        	  onUncaughtException((Exception)it.getSerializableExtra("exception"));
          }
          
          /*
           * 获取cim连接状态事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_STATUS))
          {
        	  onConnectionStatus(it.getBooleanExtra(CIMPushManager.KEY_CIM_CONNECTION_STATUS, false));
          }
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

	
	private void startPushService()
	{
		Intent intent  = new Intent(context, CIMPushService.class);
		intent.putExtra(CIMPushManager.SERVICE_ACTION, CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE);
		context.startService(intent);
	}
	
	
	 

	private   void onConnectionFailed(Exception e){
		
		if(CIMConnectorManager.netWorkAvailable(context))
		{
			//间隔30秒后重连
			connectionHandler.sendMessageDelayed(connectionHandler.obtainMessage(), 30*1000);
		}
	}

	 
	Handler connectionHandler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message message){
			
			CIMPushManager.init(context);
		}
		
	};
	
	

	private void onUncaughtException(Throwable arg0) {}

	

	private  void onDevicesNetworkChanged(NetworkInfo info) {
		
		if(info !=null)
		{
			CIMPushManager.init(context);
		} 
		
		onNetworkChanged(info);
	}

	private void filterType999Message(com.farsunset.cim.client.model.Message message)
	{
		if(CIMConstant.MessageType.TYPE_999.equals(message.getType()))
		{
			CIMCacheTools.putBoolean(context,CIMCacheTools.KEY_MANUAL_STOP,true);
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
	public abstract void onMessageReceived(com.farsunset.cim.client.model.Message message);
	@Override
	public abstract void onReplyReceived(ReplyBody body);
	
	public abstract void onNetworkChanged(NetworkInfo info);
	
	public abstract void onCIMConnectionSucceed();
	 
	public abstract void onCIMConnectionClosed();
}
