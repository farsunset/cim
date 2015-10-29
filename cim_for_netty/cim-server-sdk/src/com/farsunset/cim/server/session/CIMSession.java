package com.farsunset.cim.server.session;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import com.farsunset.cim.server.constant.CIMConstant;

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
	private String nid;//session在本台服务器上的ID
	private String deviceId;//客户端设备ID
	private String host;//session绑定的服务器IP
	private String account;//session绑定的账号
	private String channel;//终端设备类型
	private String deviceModel;//终端设备型号
	private String appVersion;//客户端应用版本
	private Long bindTime;//登录时间
	
	private Long heartbeat;//心跳时间
	
	public CIMSession(Channel session) {
		this.session = session;
		this.nid = session.id().asLongText();
	}
 
	 
 

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		if(session!=null)
		{
			setTag(CIMConstant.SESSION_KEY, account);
		}
		this.account = account;
	}

	
	public void setTag(String key,Object value)
	{
		if(value!=null)
		session.attr(AttributeKey.valueOf(key)).set(value.toString());
	}
	
	 
	public String getTag(String key)
	{
		Object tag = session.attr(AttributeKey.valueOf(key)).get();
		return tag!=null?tag.toString():null;
	}


	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
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
		if(session!=null )
		{
			setTag(CIMConstant.HEARTBEAT_KEY, heartbeat.toString());
		}
	}

	public void setHost(String host) {
		this.host = host;
	}


 


	public String getAppVersion() {
		return appVersion;
	}




	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}




	public boolean hasTag(String key) {
		if(session!=null)
			return session.hasAttr(AttributeKey.valueOf(key));
		return false;
	}
	

	public void removeTag(String key) {
		session.attr(AttributeKey.valueOf(key)).remove();
	}

	public SocketAddress getRemoteAddress() {
		if(session!=null)
		return session.remoteAddress();
		return null;
	}

	public boolean write(Object msg) {
		if(session!=null && session.isActive())
		{
			
			return session.writeAndFlush(msg).awaitUninterruptibly(5000);
		}
		return false;
	}

	public boolean isConnected() {
		if(session!=null)
		return session.isActive();
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
			if (t.session.id().asLongText().equals(session.id().asLongText())&& t.host.equals(host)) {
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