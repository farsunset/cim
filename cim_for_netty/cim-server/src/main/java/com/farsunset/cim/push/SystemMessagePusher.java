 /**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.push;

import com.farsunset.cim.sdk.server.model.Message;
 
public class SystemMessagePusher  extends DefaultMessagePusher{

	
	@Override
	public void push(Message messsage){
		
		messsage.setSender("system");
		super.push(messsage);
		
	}
}
