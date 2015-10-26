package com.farsunset.ichat.example.app;



import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;
 

public   class CommonBaseControl   {
	
	private ProgressDialog progressDialog;  
	
	Context mMontent;
	
	
	public   CommonBaseControl(Context content)
	{
		 this.mMontent = content;
	}
	
	
	
	public void showProgressDialog(String title,String message)
	{
		if(progressDialog==null)
		{
			
			 progressDialog = ProgressDialog.show(mMontent, title, message, true, true);
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
		
		   Toast toast=Toast.makeText(mMontent,hint,Toast.LENGTH_SHORT);
		   toast.show();
	}
 
  
}
