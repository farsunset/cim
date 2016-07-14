/**
 * probject:cim-android-sdk
 * @version 2.1.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.android;


import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.exception.CIMSessionDisableException;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
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
        	  onInnerConnectionClosed();
          }
          
          /*
           * cim连接服务器失败事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_FAILED))
          {
        	  onInnerConnectionFailed((Exception) it.getSerializableExtra("exception"));
          }
          
          /*
           * cim连接服务器成功事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_SUCCESSED))
          {
        	  onInnerConnectionSuccessed();
          }
          
          /*
           * 收到推送消息事件
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_MESSAGE_RECEIVED))
          {
        	  onInnerMessageReceived((Message)it.getSerializableExtra("message"));
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
          if(it.getAction().equals(CIMConnectorManager.ACTION_SENT_SUCCESSED))
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
           * 重新连接，如果断开的话
           */
          if(it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_RECOVERY))
          {
        	  CIMPushManager.connect(context);
          }
	}

	private void startPushService()
	{
		Intent intent  = new Intent(context, CIMPushService.class);
		intent.setAction(CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE);
		context.startService(intent);
	}
	private  void onInnerConnectionClosed(){
		CIMCacheToolkit.getInstance(context).putBoolean(CIMCacheToolkit.KEY_CIM_CONNECTION_STATE, false);
		if(CIMConnectorManager.netWorkAvailable(context))
		{
			CIMPushManager.connect(context);
		}
		
		onConnectionClosed();
	}
	
	Handler connectionHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message message){
			CIMPushManager.connect(context);
		}
	};

   private   void onInnerConnectionFailed(Exception e){
		
		if(CIMConnectorManager.netWorkAvailable(context))
		{
			connectionHandler.sendEmptyMessageDelayed(0, CIMConstant.RECONN_INTERVAL_TIME);
		}
		
		onConnectionFailed(e);
	}


	
	 
	 
	
	private   void onInnerConnectionSuccessed(){
		CIMCacheToolkit.getInstance(context).putBoolean(CIMCacheToolkit.KEY_CIM_CONNECTION_STATE, true);
		
		boolean autoBind = CIMPushManager.autoBindAccount(context);
		onConnectionSuccessed(autoBind);
	}

	private void onUncaughtException(Throwable arg0) {}

	

	private  void onDevicesNetworkChanged(NetworkInfo info) {
		
		if(info !=null)
		{
			CIMPushManager.connect(context);
		} 
		
		onNetworkChanged(info);
	}

	private void onInnerMessageReceived(com.farsunset.cim.sdk.android.model.Message message)
	{
		if(CIMConstant.MessageType.TYPE_999.equals(message.getType()))
		{
			CIMCacheToolkit.getInstance(context).putBoolean(CIMCacheToolkit.KEY_MANUAL_STOP,true);
		}
		
		onMessageReceived(message);
	}
	
	private   void onSentFailed(Exception e, SentBody body){
		
		//与服务端端开链接，重新连接
		if(e instanceof CIMSessionDisableException)
		{
			CIMPushManager.connect(context);
		}else
		{
			//发送失败 重新发送
			CIMPushManager.sendRequest(context, body);
		}
		
	}

	private  void onSentSucceed(SentBody body){}
	@Override
	public abstract void onMessageReceived(Message message);
	@Override
	public abstract void onReplyReceived(ReplyBody body);
	public abstract void onNetworkChanged(NetworkInfo info);
	public abstract void onConnectionFailed(Exception e);

	 
}
