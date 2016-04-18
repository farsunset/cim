/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.ichat.example.app;


import com.farsunset.cim.sdk.android.CIMEventListener;
import com.farsunset.cim.sdk.android.CIMListenerManager;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;

import android.app.Activity;
import android.net.NetworkInfo;
import android.os.Bundle;

public   abstract  class CIMMonitorActivity extends Activity implements CIMEventListener{
	
	
	CommonBaseControl commonBaseControl;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		CIMListenerManager.registerMessageListener(this,this);
		
		commonBaseControl = new CommonBaseControl(this);
		
		
	}

	@Override
	public void finish() {
		super.finish();
		CIMListenerManager.removeMessageListener(this);
		
	}
 
	@Override
	public void onRestart() {
		super.onRestart();
		CIMListenerManager.registerMessageListener(this,this);
	}
	
	
	public void showProgressDialog(String title,String content)
	{
		commonBaseControl.showProgressDialog(title, content);
	}
	
	public void hideProgressDialog()
	{
		commonBaseControl.hideProgressDialog();
	}
	
	public void showToask(String hint){
		
		commonBaseControl.showToask(hint);
	}
	
	
	 

	@Override
	public  void onMessageReceived(Message arg0){};
	 
	@Override
	public   void onNetworkChanged(NetworkInfo info){}

	/**
     * 与服务端断开连接时回调,不要在里面做连接服务端的操作
     */
	@Override
	public void onConnectionClosed() {}

	
	 /**
    * 连接服务端成功时回调
    */
	
	@Override
	public void onConnectionSuccessed(boolean arg0) {}

	
	@Override
	public void onReplyReceived(ReplyBody arg0) {}
	@Override
	public  void onConnectionFailed(Exception e){};
}
