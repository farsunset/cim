 /**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.push;

import com.farsunset.cim.sdk.server.model.Message;


/**
 * 消息发送实接口
 * 
 */
public interface CIMMessagePusher {

 
 
    /**
     * 向用户发送消息
     * @param msg
     */
	public void push(Message msg);

 
}
