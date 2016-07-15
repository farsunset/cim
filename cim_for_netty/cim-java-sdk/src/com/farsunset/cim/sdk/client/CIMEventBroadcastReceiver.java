/**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.client;


import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.farsunset.cim.sdk.client.constant.CIMConstant;
import com.farsunset.cim.sdk.client.exception.CIMSessionDisableException;
import com.farsunset.cim.sdk.client.model.Intent;
import com.farsunset.cim.sdk.client.model.Message;
import com.farsunset.cim.sdk.client.model.ReplyBody;
import com.farsunset.cim.sdk.client.model.SentBody;
 
/**
 *  消息入口，所有消息都会经过这里
 */
public  class CIMEventBroadcastReceiver {
    Random random = new Random();
	private static CIMEventBroadcastReceiver recerver;
	private CIMEventListener listener;
	private Timer connectionHandler = new Timer();;
	public static CIMEventBroadcastReceiver getInstance(){
         if (recerver==null){
        	 recerver = new CIMEventBroadcastReceiver();
		 }
		return recerver;
	}
	
	public void setGlobalCIMEventListener(CIMEventListener ls){
		listener = ls;
	}
	
	
	public void onReceive(Intent intent) {

		  
		  /*
           * cim断开服务器事件
           */
          if(intent.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_CLOSED))
          {
        	  onInnerConnectionClosed();
          }
          
          /*
           * cim连接服务器失败事件
           */
          if(intent.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_FAILED))
          {
        	  onInnerConnectionFailed((Exception) intent.getExtra("exception"));
          }
          
          /*
           * cim连接服务器成功事件
           */
          if(intent.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_SUCCESSED))
          {
        	  onInnerConnectionSuccessed();
          }
          
          /*
           * 收到推送消息事件
           */
          if(intent.getAction().equals(CIMConnectorManager.ACTION_MESSAGE_RECEIVED))
          {
        	  onInnerMessageReceived((Message)intent.getExtra("message"));
          }
          
          
          /*
           * 获取收到replybody成功事件
           */
          if(intent.getAction().equals(CIMConnectorManager.ACTION_REPLY_RECEIVED))
          {
        	  listener.onReplyReceived((ReplyBody)intent.getExtra("replyBody"));
          }
          
          
          /*
           * 获取sendbody发送失败事件
           */
          if(intent.getAction().equals(CIMConnectorManager.ACTION_SENT_FAILED))
          {
        	  onSentFailed((Exception) intent.getExtra("exception"),(SentBody)intent.getExtra("sentBody"));
          }
          
          /*
           * 获取sendbody发送成功事件
           */
          if(intent.getAction().equals(CIMConnectorManager.ACTION_SENT_SUCCESSED))
          {
        	  onSentSucceed((SentBody)intent.getExtra("sentBody"));
          }
          
          
          /*
           * 获取cim数据传输异常事件
           */
          if(intent.getAction().equals(CIMConnectorManager.ACTION_UNCAUGHT_EXCEPTION))
          {
        	  onUncaughtException((Exception)intent.getExtra("exception"));
          }
          
          
          /*
           * 重新连接，如果断开的话
           */
          if(intent.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_RECOVERY))
          {
        	  CIMPushManager.connect();
          }
	}

	 
	private  void onInnerConnectionClosed(){
		
		listener.onConnectionClosed();
		
		CIMCacheToolkit.getInstance().putBoolean(CIMCacheToolkit.KEY_CIM_CONNECTION_STATE, false);
		CIMPushManager.connect();
		
		
	}
	
	
   
	
   private   void onInnerConnectionFailed(Exception e){
		
	    connectionHandler.schedule(new ConnectionTask(),random.nextInt(CIMConstant.RECONN_INTERVAL_TIME) + 20 );
		
	    listener.onConnectionFailed(e);
	}
	
	private   void onInnerConnectionSuccessed(){
		CIMCacheToolkit.getInstance().putBoolean(CIMCacheToolkit.KEY_CIM_CONNECTION_STATE, true);
		
		boolean  autoBind = CIMPushManager.autoBindDeviceId();
		
		listener.onConnectionSuccessed(autoBind);
	}

	private void onUncaughtException(Throwable arg0) {}

	
 

	private void onInnerMessageReceived(com.farsunset.cim.sdk.client.model.Message message)
	{
		listener.onMessageReceived(message);
	}
	
	private   void onSentFailed(Exception e, SentBody body){
		
		//与服务端端开链接，重新连接
		if(e instanceof CIMSessionDisableException)
		{
			CIMPushManager.connect();
		}else
		{
			//发送失败 重新发送
			CIMPushManager.sendRequest( body);
		}
		
	}

	private  void onSentSucceed(SentBody body){}
	 
	
	 
	class ConnectionTask extends TimerTask{
		
		public void run(){
			CIMPushManager.connect();
		}
	}
	 
}
