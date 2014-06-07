 
package com.farsunset.cim.nio.session;

import java.util.Collection;

import org.apache.mina.core.session.IoSession;

/**
 *  客户端的 session管理接口
 *  可自行实现此接口管理session
 *  @author 3979434@qq.com
 */
 
public interface  SessionManager  {

	
	/**
	 * 添加新的session
	 */
	public void addSession(String account,IoSession session);
	
	/**
	 * 
	 * @param account 客户端session的 key 一般可用 用户账号来对应session
	 * @return
	 */
	IoSession getSession(String account);
	
	/**
	 * 获取所有session
	 * @return
	 */
	public Collection<IoSession> getSessions();
	
	/**
	 * 删除session
	 * @param session
	 */
    public void  removeSession(IoSession session) ;
    
    
    /**
	 * 删除session
	 * @param session
	 */
    public void  removeSession(String account);
    
    /**
	 * session是否存在
	 * @param session
	 */
    public boolean containsIoSession(IoSession ios);
    
    /**
	 * session获取对应的 用户 key  
	 * @param session
	 */
    public String getAccount(IoSession ios);
}