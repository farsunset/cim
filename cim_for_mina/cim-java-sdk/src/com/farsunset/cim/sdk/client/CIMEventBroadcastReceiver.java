/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.sdk.client;


import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.farsunset.cim.sdk.client.constant.CIMConstant;
import com.farsunset.cim.sdk.client.exception.SessionDisconnectedException;
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
          if(intent.getAction().equals(CIMConstant.IntentAction.ACTION_CONNECTION_CLOSED))
          {
        	  onInnerConnectionClosed();
          }
          
          /*
           * cim连接服务器失败事件
           */
          if(intent.getAction().equals(CIMConstant.IntentAction.ACTION_CONNECTION_FAILED))
          {
        	  long interval = intent.getLongExtra("interval", CIMConstant.RECONN_INTERVAL_TIME);
        	  onInnerConnectionFailed((Exception) intent.getExtra(Exception.class.getName()),interval);
          }
          
          /*
           * cim连接服务器成功事件
           */
          if(intent.getAction().equals(CIMConstant.IntentAction.ACTION_CONNECTION_SUCCESSED))
          {
        	  onInnerConnectionSuccessed();
          }
          
          /*
           * 收到推送消息事件
           */
          if(intent.getAction().equals(CIMConstant.IntentAction.ACTION_MESSAGE_RECEIVED))
          {
        	  onInnerMessageReceived((Message)intent.getExtra(Message.class.getName()));
          }
          
          
          /*
           * 获取收到replybody成功事件
           */
          if(intent.getAction().equals(CIMConstant.IntentAction.ACTION_REPLY_RECEIVED))
          {
        	  listener.onReplyReceived((ReplyBody)intent.getExtra(ReplyBody.class.getName()));
          }
          
          
          /*
           * 获取sendbody发送失败事件
           */
          if(intent.getAction().equals(CIMConstant.IntentAction.ACTION_SENT_FAILED))
          {
        	  onSentFailed((Exception) intent.getExtra(Exception.class.getName()),(SentBody)intent.getExtra(SentBody.class.getName()));
          }
          
          /*
           * 获取sendbody发送成功事件
           */
          if(intent.getAction().equals(CIMConstant.IntentAction.ACTION_SENT_SUCCESSED))
          {
        	  onSentSucceed((SentBody)intent.getExtra(SentBody.class.getName()));
          }
          
          
          /*
           * 获取cim数据传输异常事件
           */
          if(intent.getAction().equals(CIMConstant.IntentAction.ACTION_UNCAUGHT_EXCEPTION))
          {
        	  onUncaughtException((Exception)intent.getExtra(Exception.class.getName()));
          }
          
          
          /*
           * 重新连接，如果断开的话
           */
          if(intent.getAction().equals(CIMConstant.IntentAction.ACTION_CONNECTION_RECOVERY))
          {
        	  CIMPushManager.connect();
          }
	}

	 
	private  void onInnerConnectionClosed(){
		
		listener.onConnectionClosed();
		
		CIMCacheToolkit.getInstance().putBoolean(CIMCacheToolkit.KEY_CIM_CONNECTION_STATE, false);
		CIMPushManager.connect();
		
		
	}
	
	
   
	
   private   void onInnerConnectionFailed(Exception e,long interval){
		
	    connectionHandler.schedule(new ConnectionTask(),interval);
		
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
		
		e.printStackTrace();
		//与服务端端开链接，重新连接
		if(e instanceof SessionDisconnectedException)
		{
			CIMPushManager.connect();
		}else
		{
			//发送失败 重新发送
			//CIMPushManager.sendRequest( body);
		}
		
	}

	private  void onSentSucceed(SentBody body){}
	 
	
	 
	class ConnectionTask extends TimerTask{
		
		public void run(){
			CIMPushManager.connect();
		}
	}
	 
}
