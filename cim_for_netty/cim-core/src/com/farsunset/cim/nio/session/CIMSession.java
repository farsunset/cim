package com.farsunset.cim.nio.session;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.Channel;

import com.farsunset.cim.nio.constant.CIMConstant;

/**
 * IoSession包装类,集群时 将此对象存入表中
 * 
 * @author 3979434@qq.com
 */

public class CIMSession  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String ID = "ID";
	public static String HOST = "HOST";

	private Channel session;
	
	private String gid;//session全局ID
	private int nid;//session在本台服务器上的ID
	private String deviceId;//客户端设备ID
	private String host;//session绑定的服务器IP
	private String account;//session绑定的账号
	private String channel;//终端设备类型
	private String deviceModel;//终端设备型号
	
	private Long bindTime;//登录时间
	
	private Long heartbeat;//心跳时间
	
	public CIMSession(Channel session) {
		this.session = session;
		this.nid = session.getId();
		if(session.getAttachment()==null)
		{
			HashMap<String,Object> params= new HashMap<String,Object>();
			session.setAttachment(params);
		}
		
	}
 
	 
 

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		if(session!=null)
		{
			((Map<String,Object>)session.getAttachment()).put(CIMConstant.SESSION_KEY, account);
		}
		this.account = account;
	}

	 
	
	 


	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public int getNid() {
		return nid;
	}

	public void setNid(int nid) {
		this.nid = nid;
	}

	public String getDeviceId() {
		return deviceId;
	}


	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


   

	public String getHost() {
		return host;
	}



	public Long getBindTime() {
		return bindTime;
	}

	public void setBindTime(Long bindTime) {
		this.bindTime = bindTime;
	}

	public Long getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(Long heartbeat) {
		this.heartbeat = heartbeat;
		if(session!=null && session.getAttachment()!=null)
		{
			((Map<String,Object>)session.getAttachment()).put(CIMConstant.HEARTBEAT_KEY, heartbeat);
		}
	}

	public void setHost(String host) {
		this.host = host;
	}



	public void setAttribute(String key, Object value) {
		if(session!=null)
			((Map<String,Object>)session.getAttachment()).put(key, value);
	}


	public boolean containsAttribute(String key) {
		if(session!=null)
		return ((Map<String,Object>)session.getAttachment()).containsKey(key);
		return false;
	}
	
	public Object getAttribute(String key) {
		return ((Map<String,Object>)session.getAttachment()).get(key);
	}

	public void removeAttribute(String key) {
		((Map<String,Object>)session.getAttachment()).remove(key);
	}

	public SocketAddress getRemoteAddress() {
		if(session!=null)
		return session.getRemoteAddress();
		return null;
	}

	public boolean write(Object msg) {
		if(session!=null && session.isConnected())
		{
			
			return session.write(msg).awaitUninterruptibly(5000);
		}
		return false;
	}

	public boolean isConnected() {
		if(session!=null)
		return session.isConnected();
		return false;
	}

	public boolean  isLocalhost()
	{
		
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			return ip.equals(host) && session!=null;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return false;
		 
	}
	
 
	public void close(boolean immediately) {
		if(session!=null)
		{
		  session.disconnect();
		  session.close();
		}
	}

	 
	public boolean equals(Object o) {
        
		if (o instanceof CIMSession) {
			
			CIMSession t = (CIMSession) o;
			if(!t.isLocalhost())
			{
				return false;
			}
			if (t.session.getId() == session.getId()&& t.host.equals(host)) {
				return true;
			}
			return false;
		} else {
			return false;
		}

	}

	public Channel getSession() {
		return session;
	}

	public void setSession(Channel session) {
		this.session = session;
	}

	
	
	
	
  

}