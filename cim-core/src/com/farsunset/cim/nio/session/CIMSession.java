package com.farsunset.cim.nio.session;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

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

	private IoSession session;
	
	private String id;//session全局ID
	private String deviceId;//客户端设备ID
	private String host;//session绑定的服务器IP
	private String account;//session绑定的账号
	private String channel;//终端设备类型
	private String deviceModel;//终端设备型号
	
	private Long bindTime;//登录时间
	
	private Long heartbeat;//心跳时间
	
	public CIMSession(IoSession session) {
		this.session = session;
	}
 
	public CIMSession()
	{
		
	}
	
	
 

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		if(session!=null)
		{
			session.setAttribute(CIMConstant.SESSION_KEY, account);
		}
		this.account = account;
	}

	 
	public String getId() {
		return id;
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



	public void setId(String id) {
		this.id = id;
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
		if(session!=null)
		{
			session.setAttribute(CIMConstant.HEARTBEAT_KEY, heartbeat);
		}
	}

	public void setHost(String host) {
		this.host = host;
	}



	public void setAttribute(String key, Object value) {
		if(session!=null)
		session.setAttribute(key, value);
	}


	public boolean containsAttribute(String key) {
		if(session!=null)
		return session.containsAttribute(key);
		return false;
	}
	
	public Object getAttribute(String key) {
		if(session!=null)
		return session.getAttribute(key);
		return null;
	}

	public void removeAttribute(String key) {
		if(session!=null)
		session.removeAttribute(key);
	}

	public SocketAddress getRemoteAddress() {
		if(session!=null)
		return session.getRemoteAddress();
		return null;
	}

	public boolean write(Object msg) {
		if(session!=null)
		{
			WriteFuture wf = session.write(msg);
			wf.awaitUninterruptibly(5, TimeUnit.SECONDS);
			return wf.isWritten();
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
		session.close(immediately);
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

	public void setIoSession(IoSession session) {
		this.session = session;
	}

	public IoSession getIoSession() {
		return session;
	}
	
	
  

}