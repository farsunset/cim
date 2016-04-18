/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.server.session;

import java.util.List;


/**
 *  客户端的 session管理接口
 *  可自行实现此接口管理session
 */
 
public interface  SessionManager  {

	
	/**
	 * 添加新的session
	 */
	public void add(String account,CIMSession session);
	
	/**
	 * 更新session
	 */
	public void update(CIMSession session);
	
	/**
	 * 
	 * @param account 客户端session的 key 一般可用 用户账号来对应session
	 * @return
	 */
	CIMSession get(String account);
	
	/**
	 * 获取所有session
	 * @return
	 */
	public List<CIMSession> queryAll();
	
    
    /**
	 * 删除session
	 * @param session
	 */
    public void  remove(String account);
    
    /**
	 * 设置session失效
	 * @param session
	 */
    public void  setState(String account,int state);
}