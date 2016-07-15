 /**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.client;
 

import com.farsunset.cim.sdk.client.model.Intent;
import com.farsunset.cim.sdk.client.model.SentBody;


/**
 * 与服务端连接服务
 *
 */
  public class CIMPushService  {

	protected final  static int DEF_CIM_PORT = 28888;
	private CIMConnectorManager manager;
	
	private static CIMPushService service;
	public static CIMPushService getInstance(){
         if (service==null){
        	 service = new CIMPushService();
		 }
		return service;
	}
	
	
    public   CIMPushService()
    {
    	manager = CIMConnectorManager.getManager();
    }
 
 
    public void onStartCommand(Intent intent) {
    	
    	if(intent==null)
    	{
    		return ;
    	} 
    	
    	String action = intent.getAction();
    	
    	if(CIMPushManager.ACTION_CREATE_CIM_CONNECTION.equals(action))
    	{
	    	String host = CIMCacheToolkit.getInstance().getString(CIMCacheToolkit.KEY_CIM_SERVIER_HOST);
    	    int port =CIMCacheToolkit.getInstance().getInt(CIMCacheToolkit.KEY_CIM_SERVIER_PORT);
	    	manager.connect(host,port);
    	}
    	
    	if(CIMPushManager.ACTION_SEND_REQUEST_BODY.equals(action))
    	{
    		manager.send((SentBody) intent.getExtra(CIMPushManager.KEY_SEND_BODY));
    	}
    	
    	if(CIMPushManager.ACTION_CLOSE_CIM_CONNECTION.equals(action))
    	{
    		manager.closeSession();
    	}
    	
    	if(CIMPushManager.ACTION_DESTORY.equals(action))
    	{
    		manager.destroy();
    	}
     
    	if(CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action) && !manager.isConnected())
    	{
    		
    		String host = CIMCacheToolkit.getInstance().getString(CIMCacheToolkit.KEY_CIM_SERVIER_HOST);
	    	int port =CIMCacheToolkit.getInstance().getInt( CIMCacheToolkit.KEY_CIM_SERVIER_PORT);
	    	manager.connect(host,port);
    	}
    }
    
 
}
