/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.sdk.client;
 

import com.farsunset.cim.sdk.client.model.Intent;
import com.farsunset.cim.sdk.client.model.SentBody;


/**
 * 与服务端连接服务
 *
 */
  public class CIMPushService  {

	protected final  static int DEF_CIM_PORT = 23456;
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
    	
    	intent = (intent == null ? new Intent(CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE) : intent);

    	
    	String action = intent.getAction();
    	
    	if(CIMPushManager.ACTION_CREATE_CIM_CONNECTION.equals(action))
    	{
	    	String host = CIMCacheToolkit.getInstance().getString(CIMCacheToolkit.KEY_CIM_SERVIER_HOST);
    	    int port =CIMCacheToolkit.getInstance().getInt(CIMCacheToolkit.KEY_CIM_SERVIER_PORT);
	    	manager.connect(host,port);
    	}
    	
    	if(CIMPushManager.ACTION_SEND_REQUEST_BODY.equals(action))
    	{
    		manager.send((SentBody) intent.getExtra(SentBody.class.getName()));
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
