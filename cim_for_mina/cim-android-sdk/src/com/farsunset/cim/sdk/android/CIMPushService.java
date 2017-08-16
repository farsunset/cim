/**
 * Copyright 2013-2033 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.sdk.android;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.farsunset.cim.sdk.android.model.SentBody;


/**
 * 与服务端连接服务
 * @author 3979434
 *
 */
  public class CIMPushService extends Service {
	private final  String TAG = CIMPushService.class.getSimpleName();
	public final static String KEY_DELAYED_TIME ="KEY_DELAYED_TIME";
	private CIMConnectorManager manager;
    @Override
    public void onCreate()
    {
    	manager = CIMConnectorManager.getManager(this.getApplicationContext());
    }
 
    
    Handler connectionHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message message){
			
			connectionHandler.removeMessages(0);
			
			String host = message.getData().getString(CIMCacheManager.KEY_CIM_SERVIER_HOST);
    	    int port = message.getData().getInt(CIMCacheManager.KEY_CIM_SERVIER_PORT,0);
			manager.connect(host, port);
		}
	};
	
 
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
    	
    	
    	intent = (intent == null ? new Intent(CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE) : intent);
    	
    	String action = intent.getAction();
    	
    	if(CIMPushManager.ACTION_CREATE_CIM_CONNECTION.equals(action))
    	{
	    	
    	    long delayMillis = intent.getLongExtra(KEY_DELAYED_TIME,0);
    	    if( delayMillis > 0){
    	    	
    	    	Message msg = connectionHandler.obtainMessage();
    	    	msg.what = 0;
    	    	msg.setData(intent.getExtras());
    	    	connectionHandler.sendMessageDelayed(msg, delayMillis);
    	    	
    	    }else
    	    {
    	    	String host = intent.getStringExtra(CIMCacheManager.KEY_CIM_SERVIER_HOST);
        	    int port = intent.getIntExtra(CIMCacheManager.KEY_CIM_SERVIER_PORT,0);
    	    	manager.connect(host,port);
    	    }
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
    	}
     
    	if(CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action) )
    	{
    		if(!manager.isConnected()){
    			
    			boolean  isManualStop  = CIMCacheManager.getBoolean(getApplicationContext(),CIMCacheManager.KEY_MANUAL_STOP);
    	    	Log.w(TAG, "manager.isConnected() == false, isManualStop == " + isManualStop);
    	    	CIMPushManager.connect(this,0);
    	    	
    		}else
    		{
    			Log.i(TAG, "manager.isConnected() == true");
    		}
    		
    	}
    	
      	
    	return  START_STICKY;
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
