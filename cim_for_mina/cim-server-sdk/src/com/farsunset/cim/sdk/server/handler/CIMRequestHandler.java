/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */   
package com.farsunset.cim.sdk.server.handler;

/**
 *  请求处理接口,所有的请求实现必须实现此接口
 *  @author 3979434@qq.com
 */
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;
 
public    interface   CIMRequestHandler  {

    public abstract ReplyBody process(CIMSession session,SentBody message);
}