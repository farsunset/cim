/**
 * probject:cim-core
 * @version 1.5.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.server.session;

import java.util.Collection;


/**
 *  客户端的 session管理接口
 *  可自行实现此接口管理session
 */
 
public interface  SessionManager  {

	
	/**
	 * 添加新的session
	 */
	public void addSession(String account,CIMSession session);
	
	/**
	 * 添加新的session
	 */
	public void updateSession(CIMSession session);
	
	/**
	 * 
	 * @param account 客户端session的 key 一般可用 用户账号来对应session
	 * @return
	 */
	CIMSession getSession(String account);
	
	/**
	 * 获取所有session
	 * @return
	 */
	public Collection<CIMSession> getSessions();
	
    
    /**
	 * 删除session
	 * @param session
	 */
    public void  removeSession(String account);
    
    /**
	 * 删除session
	 * @param session
	 */
    public void  setInvalid(String account);
    
    /**
	 * session是否存在
	 * @param session
	 */
    public boolean containsCIMSession(String account);
    
    
}