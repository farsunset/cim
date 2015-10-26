package com.farsunset.ichat.example.app;



import android.app.Activity;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.farsunset.cim.client.android.CIMEventListener;
import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.model.Message;
import com.farsunset.cim.client.model.ReplyBody;

public  abstract  class CIMMonitorActivity extends Activity implements CIMEventListener{
	
	
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
	
	
	 
	/**
     * 与服务端断开连接时回调,不要在里面做连接服务端的操作
     */
	
	@Override
	public void onCIMConnectionSucceed() {}
	
	 /**
     * 连接服务端成功时回调
     */
	
	@Override
	public void onCIMConnectionClosed() {}
	@Override
	public void onConnectionStatus(boolean  isConnected){}
	
	@Override
	public void onReplyReceived(ReplyBody reply) {}
	
	@Override
	public void onMessageReceived(Message arg0) {}
	 
	@Override
	public   void onNetworkChanged(NetworkInfo info){};
}
