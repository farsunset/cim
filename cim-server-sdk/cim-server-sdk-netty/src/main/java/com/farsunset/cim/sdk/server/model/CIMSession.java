/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim.sdk.server.model;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.model.proto.SessionProto;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Objects;

/**
 * IoSession包装类,集群时 将此对象存入表中
 */
public class CIMSession implements Serializable {

	private transient static final long serialVersionUID = 1L;

	public transient static String HOST = "HOST";
	public transient static final int STATE_ENABLED = 0;
	public transient static final int STATE_DISABLED = 1;
	public transient static final int APNS_ON = 1;
	public transient static final int APNS_OFF = 0;

	public transient static String CHANNEL_IOS = "ios";
	public transient static String CHANNEL_ANDROID = "android";
	public transient static String CHANNEL_WINDOWS = "windows";

	private transient Channel session;

	/**
	 * 数据库主键ID
	 */
	private Long id;
	
	/**
	 * session绑定的用户账号
	 */
	private String account; 
	
	/**
	 * session在本台服务器上的ID
	 */
	private String nid; 
	
	/**
	 * 客户端ID (设备号码+应用包名),ios为deviceToken
	 */
	private String deviceId; 
	
	/**
	 * session绑定的服务器IP
	 */
	private String host; 
	
	/**
	 * 终端设备类型
	 */
	private String channel; 
	
	/**
	 * 终端设备型号
	 */
	private String deviceModel; 
	
	/**
	 * 终端应用版本
	 */
	private String clientVersion; 
	
	/**
	 * 终端系统版本
	 */
	private String systemVersion; 
	
	/**
	 * 登录时间
	 */
	private Long bindTime; 
	
	/**
	 * 经度
	 */
	private Double longitude; 
	
	/**
	 * 维度
	 */
	private Double latitude; 
	
	/**
	 * 位置
	 */
	private String location; 
	
	/**
	 * APNs推送状态
	 */
	private int apns; 
	
	/**
	 * 状态
	 */
	private int state; 


	public CIMSession(Channel session) {
		this.session = session;
		this.nid = session.id().asShortText();
	}


	public CIMSession() {

	}

	public void setSession(Channel session) {
		this.session = session;
	}

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;

		setAttribute(CIMConstant.KEY_ACCOUNT, account);
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getApns() {
		return apns;
	}

	public void setApns(int apns) {
		this.apns = apns;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public Channel getSession() {
		return session;
	}


	public void setAttribute(String key, Object value) {
		if (session != null) {
			session.attr(AttributeKey.valueOf(key)).set(value);
		}
	}

	public boolean containsAttribute(String key) {
		if (session != null) {
			return session.hasAttr(AttributeKey.valueOf(key));
		}
		return false;
	}

	public Object getAttribute(String key) {
		if (session != null) {
			return session.attr(AttributeKey.valueOf(key)).get();
		}
		return null;
	}

	public void removeAttribute(String key) {
		if (session != null) {
			session.attr(AttributeKey.valueOf(key)).set(null);
		}
	}

	public SocketAddress getRemoteAddress() {
		if (session != null) {
			return session.remoteAddress();
		}
		return null;
	}


	public void write(Transportable data) {

		if (session == null || !session.isActive()) {
			return;
		}

		session.writeAndFlush(data);
	}

	public boolean isConnected() {
		return (session != null && session.isActive()) || state == STATE_ENABLED;
	}

	public void closeNow() {
		if (session != null) {
			session.close();
		}
	}

	public void closeOnFlush() {
		if (session != null) {
			session.close();
		}
	}

	public boolean isIOSChannel() {
		return Objects.equals(channel, CHANNEL_IOS);
	}

	public boolean isAndroidChannel() {
		return Objects.equals(channel, CHANNEL_ANDROID);
	}

	public boolean isWindowsChannel() {
		return Objects.equals(channel, CHANNEL_WINDOWS);
	}

	public boolean isApnsEnable() {
		return Objects.equals(apns, APNS_ON);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CIMSession) {
			CIMSession target = (CIMSession) o;
			return Objects.equals(target.deviceId, deviceId) && Objects.equals(target.nid, nid)
					&& Objects.equals(target.host, host);
		}
		return false;
	}

	public byte[] getProtobufBody() {
		SessionProto.Model.Builder builder = SessionProto.Model.newBuilder();
		if (id != null) {
			builder.setId(id);
		}
		if (account != null) {
			builder.setAccount(account);
		}
		if (nid != null) {
			builder.setNid(nid);
		}
		if (deviceId != null) {
			builder.setDeviceId(deviceId);
		}
		if (host != null) {
			builder.setHost(host);
		}
		if (channel != null) {
			builder.setChannel(channel);
		}
		if (deviceModel != null) {
			builder.setDeviceModel(deviceModel);
		}
		if (clientVersion != null) {
			builder.setClientVersion(clientVersion);
		}
		if (systemVersion != null) {
			builder.setSystemVersion(systemVersion);
		}
		if (bindTime != null) {
			builder.setBindTime(bindTime);
		}
		if (longitude != null) {
			builder.setLongitude(longitude);
		}
		if (latitude != null) {
			builder.setLatitude(latitude);
		}
		if (location != null) {
			builder.setLocation(location);
		}
		builder.setState(state);
		builder.setApns(apns);
		return builder.build().toByteArray();
	}
	
	
	public static CIMSession decode(byte[] protobufBody) throws InvalidProtocolBufferException {
		if(protobufBody == null) {
			return null;
		}
		SessionProto.Model proto = SessionProto.Model.parseFrom(protobufBody);
		CIMSession session = new CIMSession();
		session.setId(proto.getId());
		session.setApns(proto.getApns());
		session.setBindTime(proto.getBindTime());
		session.setChannel(proto.getChannel());
		session.setClientVersion(proto.getClientVersion());
		session.setDeviceId(proto.getDeviceId());
		session.setDeviceModel(proto.getDeviceModel());
		session.setHost(proto.getHost());
		session.setLatitude(proto.getLatitude());
		session.setLongitude(proto.getLongitude());
		session.setLocation(proto.getLocation());
		session.setNid(proto.getNid());
		session.setSystemVersion(proto.getSystemVersion());
		session.setState(proto.getState());
		session.setAccount(proto.getAccount());
		return session;
	}
	

}
