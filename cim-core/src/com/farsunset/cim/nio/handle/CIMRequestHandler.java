 
package com.farsunset.cim.nio.handle;

/**
 *  请求处理接口,所有的请求实现必须实现此接口
 *  @author 3979434@qq.com
 */
import org.apache.mina.core.session.IoSession;

import com.farsunset.cim.nio.mutual.ReplyBody;
import com.farsunset.cim.nio.mutual.SentBody;
 
public    interface   CIMRequestHandler  {

    public abstract ReplyBody process(IoSession ios,SentBody message);
}