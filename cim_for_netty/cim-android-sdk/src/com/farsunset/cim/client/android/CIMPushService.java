 
package com.farsunset.cim.client.android;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.farsunset.cim.client.model.SentBody;


/**
 * 与服务端连接服务
 * @author 3979434
 *
 */
  public class CIMPushService extends Service {

	protected final  static int DEF_CIM_PORT = 28888;
	CIMConnectorManager manager;
    @Override
    public void onCreate()
    {
    	manager = CIMConnectorManager.getManager(this.getApplicationContext());
    }
 
 
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
    	
    	String action;
    	if(intent==null)
    	{
    		 intent = new  Intent(CIMPushManager.ACTION_CONNECTION);
    		 String host = CIMCacheTools.getString(this, CIMCacheTools.KEY_CIM_SERVIER_HOST);
    	     int port =CIMCacheTools.getInt(this, CIMCacheTools.KEY_CIM_SERVIER_PORT);
    	     intent.putExtra(CIMCacheTools.KEY_CIM_SERVIER_HOST, host);
    	     intent.putExtra(CIMCacheTools.KEY_CIM_SERVIER_PORT, port);
    	} 
    	
    	action = intent.getStringExtra(CIMPushManager.SERVICE_ACTION);
    	
    	if(CIMPushManager.ACTION_CONNECTION.equals(action))
    	{
    		String host = intent.getStringExtra(CIMCacheTools.KEY_CIM_SERVIER_HOST);
	    	int port = intent.getIntExtra(CIMCacheTools.KEY_CIM_SERVIER_PORT, DEF_CIM_PORT);
	    	manager.connect(host,port);
    	}
    	
    	if(CIMPushManager.ACTION_SENDREQUEST.equals(action))
    	{
    		manager.send((SentBody) intent.getSerializableExtra(CIMPushManager.KEY_SEND_BODY));
    	}
    	
    	if(CIMPushManager.ACTION_DISCONNECTION.equals(action))
    	{
    		manager.closeSession();
    	}
    	
    	if(CIMPushManager.ACTION_DESTORY.equals(action))
    	{
    		manager.destroy();
    		this.stopSelf();
    		android.os.Process.killProcess(android.os.Process.myPid());
    	}
    	
    	if(CIMPushManager.ACTION_CONNECTION_STATUS.equals(action))
    	{
    	     manager.deliverIsConnected();
    	}
    	
    	if(CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action))
    	{
    		
    	    if(!manager.isConnected())
    	    {
    	    	Log.d(CIMPushService.class.getSimpleName(), "cimpush isConnected() = false ");
    	    	String host = intent.getStringExtra(CIMCacheTools.KEY_CIM_SERVIER_HOST);
    	    	int port = intent.getIntExtra(CIMCacheTools.KEY_CIM_SERVIER_PORT, DEF_CIM_PORT);
    	    	manager.connect(host,port);
    	    }else
    	    {
    	    	Log.d(CIMPushService.class.getSimpleName(), "isConnected() = true ");
    	    }
    	}
    	
    	
    	return Service.START_REDELIVER_INTENT;
    }


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
   
 
}
