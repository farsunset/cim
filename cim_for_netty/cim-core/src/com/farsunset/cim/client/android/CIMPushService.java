 
package com.farsunset.cim.client.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.farsunset.cim.nio.mutual.SentBody;


/**
 * 与服务端连接服务
 * @author 3979434
 *
 */
  public class CIMPushService extends Service {

   
	CIMConnectorManager manager;
	
    private IBinder binder=new CIMPushService.LocalBinder();
    
    @Override
    public void onCreate()
    {
    	manager = CIMConnectorManager.getManager(this.getApplicationContext());
    }
 
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
    	
    	
    	if(intent==null)
    	{
    		return super.onStartCommand(intent, flags, startId);
    	}
    	
    	String action = intent.getStringExtra(CIMPushManager.SERVICE_ACTION);
    	
    	if(CIMPushManager.ACTION_CONNECTION.equals(action))
    	{
    		String host = intent.getStringExtra(CIMDataConfig.KEY_CIM_SERVIER_HOST);
	    	int port = intent.getIntExtra(CIMDataConfig.KEY_CIM_SERVIER_PORT, 28888);
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
    	}
    	
    	if(CIMPushManager.ACTION_CONNECTION_STATUS.equals(action))
    	{
    	     manager.deliverIsConnected();
    	}
    	
    	return Service.START_REDELIVER_INTENT;
    }
    
   
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

    public class LocalBinder extends Binder{
    	
    	public CIMPushService getService()
    	{
            return CIMPushService.this;
        }
    }
}
