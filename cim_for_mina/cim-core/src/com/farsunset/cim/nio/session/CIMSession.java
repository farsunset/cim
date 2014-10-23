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
	private transient static final long serialVersionUID = 1L;
	public  transient  static String ID = "ID";
	public  transient static String HOST = "HOST";
    
	private transient IoSession session;
	
	private String gid;//session全局ID
	private Long nid;//session在本台服务器上的ID
	private String deviceId;//客户端ID  (设备号码+应用包名)
	private String host;//session绑定的服务器IP
	private String account;//session绑定的账号
	private String channel;//终端设备类型
	private String deviceModel;//终端设备型号
	private Long bindTime;//登录时间
	private Long heartbeat;//心跳时间
	
	public CIMSession(IoSession session) {
		this.session = session;
		this.nid = session.getId();
	}
 
	public CIMSession()
	{
		
	}
	
	
 

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
		
		setAttribute(CIMConstant.SESSION_KEY, account);
	}

	 
	
	 


	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		
		this.gid = gid;
		
		setAttribute("gid", gid);
	}

	public Long getNid() {
		return nid;
	}

	public void setNid(Long nid) {
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
		
		setAttribute("channel", channel);
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
		
		setAttribute("deviceModel", deviceModel);
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		
		setAttribute("deviceId", deviceId);
	}


   

	public String getHost() {
		return host;
	}



	public Long getBindTime() {
		return bindTime;
	}

	public void setBindTime(Long bindTime) {
		this.bindTime = bindTime;
	    setAttribute("bindTime", bindTime);
	}

	public Long getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(Long heartbeat) {
		this.heartbeat = heartbeat;
		setAttribute(CIMConstant.HEARTBEAT_KEY, heartbeat);
	}

	public void setHost(String host) {
		this.host = host;
		 
		setAttribute("host", host);
	}


	public void setIoSession(IoSession session) {
		this.session = session;
	}

	public IoSession getIoSession() {
		return session;
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
			return ip.equals(host);
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
			if(t.deviceId!=null && deviceId!=null &&  t.nid!=null && nid!=null)
			{
				return t.deviceId.equals(deviceId) && t.nid.longValue()==nid.longValue() && t.host.equals(host);
			} 
		}  
		return false;
	}

 

}