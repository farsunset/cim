 
package com.farsunset.cim.nio.session;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

/** 
 * 自带默认 session管理实现， 各位可以自行实现 AbstractSessionManager接口来实现自己的 session管理
 *
 * @author farsunset (3979434@qq.com)
 */
public class DefaultSessionManager implements SessionManager{

	protected final Logger logger = Logger.getLogger(DefaultSessionManager.class); 

    static final String SESSION_KEY ="account";
    private static HashMap<String,IoSession> sessions =new  HashMap<String,IoSession>();
    
    
    private static final AtomicInteger connectionsCounter = new AtomicInteger(0);
 
   

    /**
     *  
     */
    public void addSession(String account,IoSession session) {
        if(session!=null)
        {
        	session.setAttribute(SESSION_KEY, account);
        	sessions.put(account, session);
        	connectionsCounter.incrementAndGet();
        }
        
    }

     
    public IoSession getSession(String account) {
    	
    	 
       return sessions.get(account);
    }

    

     
    public Collection<IoSession> getSessions() {
        return sessions.values();
    }
 
    public void  removeSession(IoSession session) {
        
    	 
    	sessions.remove(session.getAttribute(SESSION_KEY));
    }

     
    public void  removeSession(String account) {
        
    	sessions.remove(account);
    	
    }
    
    
    public boolean containsIoSession(IoSession ios)
    {
    	return sessions.containsKey(ios.getAttribute(SESSION_KEY)) || sessions.containsValue(ios);
    }

    
    public String getAccount(IoSession ios)
    {
    	 if(ios.getAttribute(SESSION_KEY)==null)
    	 {
    		for(String key:sessions.keySet())
    		{
    			if(sessions.get(key).equals(ios) || sessions.get(key).getId()==ios.getId())
    			{
    				return key;
    			}
    		}
    	 }else
    	 {
    	    return ios.getAttribute(SESSION_KEY).toString();
    	 }
    	 
    	 return null;
    }
 
}
