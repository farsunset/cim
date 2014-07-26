package com.farsunset.ichat.example.app;



import android.app.Activity;
import android.app.ProgressDialog;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.android.OnCIMMessageListener;
import com.farsunset.cim.nio.mutual.Message;
import com.farsunset.cim.nio.mutual.ReplyBody;

public  abstract  class CIMMonitorActivity extends Activity implements OnCIMMessageListener{
	
	
	private ProgressDialog progressDialog;  
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		CIMListenerManager.registerMessageListener(this,this);
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
	
	
	public void showProgressDialog(String title,String message)
	{
		if(progressDialog==null)
		{
			
			 progressDialog = ProgressDialog.show(this, title, message, true, true);
		}else if(progressDialog.isShowing())
		{
			progressDialog.setTitle(title);
			progressDialog.setMessage(message);
		}
	
		progressDialog.show();
		
	}
	
	public void hideProgressDialog()
	{
	
		if(progressDialog!=null&&progressDialog.isShowing())
		{
			progressDialog.dismiss();
		}
		
	}
	
	public void showToask(String hint){
		
		   Toast toast=Toast.makeText(this,hint,Toast.LENGTH_SHORT);
		   toast.show();
	}
 
	
	 

	@Override
	public void onConnectionSucceed() {}
	@Override
	public void onConnectionStatus(boolean  isConnected){}
	
	@Override
	public void onReplyReceived(ReplyBody reply) {}
	
	@Override
	public void onMessageReceived(Message arg0) {}
	 
	@Override
	public   void onNetworkChanged(NetworkInfo info){};
}
