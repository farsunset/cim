/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.ichat.example.ui;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.farsunset.cim.sdk.android.CIMPushManager;
import com.farsunset.ichat.example.R;
import com.farsunset.ichat.example.app.CIMMonitorActivity;
import com.farsunset.ichat.example.app.Constant;

public class SplanshActivity extends CIMMonitorActivity{
	
	boolean initComplete = false;
	public void onCreate(Bundle savedInstanceState)
	{
		
		
		super.onCreate(savedInstanceState);
		
		//连接服务端
		
		CIMPushManager.connect(SplanshActivity.this,Constant.CIM_SERVER_HOST, Constant.CIM_SERVER_PORT);
		
		
		final View view = View.inflate(this, R.layout.activity_splansh, null);
		setContentView(view);
		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		
		
	}
	@Override
	public void onConnectionSuccessed(boolean autoBind) {
		
		Intent intent = new Intent(SplanshActivity.this,LoginActivity.class);  
		startActivity(intent);
		finish();
	}
	 
	
	 @Override
	public void onBackPressed() {
		  finish();
		  CIMPushManager.destroy(this);
	}
	 public  void onConnectionFailed(Exception e){
		 
		 showToask("连接服务器失败，请检查当前设备是否能连接上服务器IP和端口");
	 }
}
