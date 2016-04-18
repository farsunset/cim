/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.server.session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.farsunset.cim.sdk.server.constant.CIMConstant;

/** 
 * 自带默认 session管理实现， 各位可以自行实现 AbstractSessionManager接口来实现自己的 session管理
 *服务器集群时 须要将CIMSession 信息存入数据库或者nosql 等 第三方存储空间中，便于所有服务器都可以访问
 */
public class DefaultSessionManager implements SessionManager{


    private static HashMap<String,CIMSession> sessions =new  HashMap<String,CIMSession>();
    
    
    private static final AtomicInteger connectionsCounter = new AtomicInteger(0);
 
   

    /**
     *  
     */
    public void add(String account,CIMSession session) {
        if(session!=null)
        {
        	session.setAttribute(CIMConstant.SESSION_KEY, account);
        	sessions.put(account, session);
        	connectionsCounter.incrementAndGet();
        }
        
    }

     
    public CIMSession get(String account) {
    	
    	 
       return sessions.get(account);
    }

    

     
    public List<CIMSession> queryAll() {
    	 List<CIMSession> list = new ArrayList<CIMSession>();
         list.addAll(sessions.values());
         return list;
    }
 
    public void  remove(CIMSession session) {
        
    	 
    	sessions.remove(session.getAttribute(CIMConstant.SESSION_KEY));
    }

     
    public void  remove(String account) {
        
    	sessions.remove(account);
    	
    }
    
    
    public boolean containsCIMSession(String account)
    {
    	return sessions.containsKey(account);
    }

    
    public String getAccount(CIMSession ios)
    {
    	 if(ios.getAttribute(CIMConstant.SESSION_KEY)==null)
    	 {
    		for(String key:sessions.keySet())
    		{
    			if(sessions.get(key).equals(ios) || sessions.get(key).getGid()==ios.getGid())
    			{
    				return key;
    			}
    		}
    	 }else
    	 {
    	    return ios.getAttribute(CIMConstant.SESSION_KEY).toString();
    	 }
    	 
    	 return null;
    }


	@Override
	public void update(CIMSession session) {
		sessions.put(session.getAccount(), session);
	}


	@Override
	public void setState(String account,int state) {
		sessions.get(account).setStatus(state);
	}

 
}
