 
package com.farsunset.cim.client.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.farsunset.cim.nio.mutual.SentBody;


/**
 * 与服务端连接服务
 * @author 3979434
 *
 */
  public class CIMPushService extends Service {

   
	CIMConnectorManager manager;
	AlarmManager localAlarmManager;
    private IBinder binder=new CIMPushService.LocalBinder();
    PendingIntent localPendingIntent;
    @Override
    public void onCreate()
    {
    	manager = CIMConnectorManager.getManager(this.getApplicationContext());
    	localPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, KeepAliveReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);
    	localAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
    	localAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 300000L + System.currentTimeMillis(),300000L, localPendingIntent);
    }
 
 
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
    	
    	String action;
    	if(intent==null)
    	{
    		 intent = new  Intent(CIMPushManager.ACTION_CONNECTION);
    		 String host = CIMDataConfig.getString(this, CIMDataConfig.KEY_CIM_SERVIER_HOST);
    	     int port =CIMDataConfig.getInt(this, CIMDataConfig.KEY_CIM_SERVIER_PORT);
    	     intent.putExtra(CIMDataConfig.KEY_CIM_SERVIER_HOST, host);
    	     intent.putExtra(CIMDataConfig.KEY_CIM_SERVIER_PORT, port);
    	} 
    	
    	action = intent.getStringExtra(CIMPushManager.SERVICE_ACTION);
    	
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
    		localAlarmManager.cancel(localPendingIntent);
    		localPendingIntent.cancel();
    		manager.destroy();
    		this.stopSelf();
    		android.os.Process.killProcess(android.os.Process.myPid());
    	}
    	
    	if(CIMPushManager.ACTION_CONNECTION_STATUS.equals(action))
    	{
    	     manager.deliverIsConnected();
    	}
    	
    	if(CIMPushManager.ACTION_CONNECTION_KEEPALIVE.equals(action))
    	{
    		
    	    if(!manager.isConnected())
    	    {
    	    	Log.d(CIMPushService.class.getSimpleName(), "isConnected() = false ");
    	    	String host = intent.getStringExtra(CIMDataConfig.KEY_CIM_SERVIER_HOST);
    	    	int port = intent.getIntExtra(CIMDataConfig.KEY_CIM_SERVIER_PORT, 28888);
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
		return binder;
	}

    public class LocalBinder extends Binder{
    	
    	public CIMPushService getService()
    	{
            return CIMPushService.this;
        }
    }
}
