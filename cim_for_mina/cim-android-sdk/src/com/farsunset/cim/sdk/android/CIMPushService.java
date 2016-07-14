 /**
 * probject:cim-android-sdk
 * @version 2.1.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.android;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.farsunset.cim.sdk.android.model.SentBody;


/**
 * 与服务端连接服务
 *
 */
  public class CIMPushService extends Service {
	final static String TAG = CIMPushService.class.getSimpleName();
	protected final  static int DEF_CIM_PORT = 28888;
	CIMConnectorManager manager;
	WakeLock wakeLock;
    @Override
    public void onCreate()
    {
    	manager = CIMConnectorManager.getManager(this.getApplicationContext());
    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CIMPushService.class.getName());
    }
 
 
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
    	
    	if(intent==null)
    	{
    		return START_STICKY;
    	} 
    	
    	String action = intent.getAction();
    	
    	if(CIMPushManager.ACTION_CREATE_CIM_CONNECTION.equals(action))
    	{
	    	String host = CIMCacheToolkit.getInstance(this).getString(CIMCacheToolkit.KEY_CIM_SERVIER_HOST);
    	    int port =CIMCacheToolkit.getInstance(this).getInt(CIMCacheToolkit.KEY_CIM_SERVIER_PORT);
	    	manager.connect(host,port);
    	}
    	
    	if(CIMPushManager.ACTION_SEND_REQUEST_BODY.equals(action))
    	{
    		manager.send((SentBody) intent.getSerializableExtra(CIMPushManager.KEY_SEND_BODY));
    	}
    	
    	if(CIMPushManager.ACTION_CLOSE_CIM_CONNECTION.equals(action))
    	{
    		manager.closeSession();
    	}
    	
    	if(CIMPushManager.ACTION_DESTORY.equals(action))
    	{
    		manager.destroy();
    		this.stopSelf();
    		android.os.Process.killProcess(android.os.Process.myPid());
    	}
     
    	if(CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action) )
    	{
    		if(!manager.isConnected()){
    			
    			boolean  isManualStop  = CIMCacheToolkit.getInstance(this).getBoolean(CIMCacheToolkit.KEY_MANUAL_STOP);
    	    	Log.i(TAG, "CIM.isConnected() == false, isManualStop == " + isManualStop);
    	    	CIMPushManager.connect(this);
				
    		}else
    		{
    			Log.i(TAG, "CIM.isConnected() == true");
    		}
    		
    	}
    	
    	try{
    		if(!wakeLock.isHeld())
        	{
        		this.wakeLock.acquire();
        	}	
    	}catch(Exception e){}
    	
    	
    	return  START_STICKY;
    }


    public void onDestroy()
    {
    	super.onDestroy();
    	if(wakeLock.isHeld())
        {
        	this.wakeLock.release();
        	wakeLock = null;
        }
    }
    
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
 
}
