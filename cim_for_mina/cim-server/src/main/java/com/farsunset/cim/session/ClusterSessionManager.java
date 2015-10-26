 
package com.farsunset.cim.session;

import java.util.Collection;

import com.farsunset.cim.server.session.CIMSession;
import com.farsunset.cim.server.session.SessionManager;

/** 
 * 集群 session管理实现示例， 各位可以自行实现 AbstractSessionManager接口来实现自己的 session管理
 *服务器集群时 须要将CIMSession 信息存入数据库或者nosql 等 第三方存储空间中，便于所有服务器都可以访问
 * @author farsunset (3979434@qq.com)
 */
public class ClusterSessionManager implements SessionManager{


  //  private static HashMap<String,IoSession> sessions =new  HashMap<String,IoSession>();
    
    
 
   

    /**
     *  
     */
    public void addSession(String account,CIMSession session) {
         
        
        /**
         * 下面 将session 存入数据库
         */
        
    }

     
    public CIMSession getSession(String account) {
    	
    	//这里查询数据库 
    	 /*CIMSession session = database.getSession(account);
    	 session.setIoSession(((NioSocketAcceptor) ContextHolder.getBean("serverAcceptor")).getManagedSessions().get(session.getNid()));
         return session;*/
    	return null;
    }
    

     
    public Collection<CIMSession> getSessions() {
    	/*//这里查询数据库 
   	 return database.getSessions();*/
   	return null;
    }
 
    public void  removeSession(CIMSession session) {
        
    	 
    	//database.removeSession(session.getAttribute(CIMConstant.SESSION_KEY));*/
    }

     
    public void  removeSession(String account) {
        
    	//database.removeSession(account);*/
    	
    }
    
    
    public boolean containsCIMSession(CIMSession ios)
    {
    	//return database.containsCIMSession(session.getAccount());
    	
    	return false;
    }

    
    public String getAccount(CIMSession ios)
    {
       return 	ios.getAccount();
    }


	@Override
	public boolean containsCIMSession(String account) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setInvalid(String account) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateSession(CIMSession session) {
		
	}

 
 
}
