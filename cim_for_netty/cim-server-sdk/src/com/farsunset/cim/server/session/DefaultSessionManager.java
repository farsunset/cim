 
package com.farsunset.cim.server.session;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.farsunset.cim.server.constant.CIMConstant;

/** 
 * 自带默认 session管理实现， 各位可以自行实现 AbstractSessionManager接口来实现自己的 session管理
 *服务器集群时 须要将CIMSession 信息存入数据库或者nosql 等 第三方存储空间中，便于所有服务器都可以访问
 * @author farsunset (3979434@qq.com)
 */
public class DefaultSessionManager implements SessionManager{


    private static HashMap<String,CIMSession> sessions =new  HashMap<String,CIMSession>();
    
    
    private static final AtomicInteger connectionsCounter = new AtomicInteger(0);
 
   

    /**
     *  
     */
    public void addSession(String account,CIMSession session) {
        if(session!=null)
        {
        	session.setTag(CIMConstant.SESSION_KEY, account);
        	sessions.put(account, session);
        	connectionsCounter.incrementAndGet();
        }
        
    }

     
    public CIMSession getSession(String account) {
    	
    	 
       return sessions.get(account);
    }

    

     
    public Collection<CIMSession> getSessions() {
        return sessions.values();
    }
 
    public void  removeSession(CIMSession session) {
        
    	 
    	sessions.remove(session.getTag(CIMConstant.SESSION_KEY));
    }

     
    public void  removeSession(String account) {
        
    	sessions.remove(account);
    	
    }
    
    
    public boolean containsCIMSession(CIMSession ios)
    {
    	return sessions.containsKey(ios.getTag(CIMConstant.SESSION_KEY)) || sessions.containsValue(ios);
    }

    
    public String getAccount(CIMSession ios)
    {
    	 if(ios.getTag(CIMConstant.SESSION_KEY)==null)
    	 {
    		for(String key:sessions.keySet())
    		{
    			if(sessions.get(key).equals(ios) || sessions.get(key).getNid()==ios.getNid())
    			{
    				return key;
    			}
    		}
    	 }else
    	 {
    	    return ios.getTag(CIMConstant.SESSION_KEY).toString();
    	 }
    	 
    	 return null;
    }
 
}
