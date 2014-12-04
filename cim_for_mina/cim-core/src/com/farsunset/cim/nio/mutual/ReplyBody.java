package com.farsunset.cim.nio.mutual;

import java.io.Serializable;
import java.util.HashMap;
/**
 * 请求应答对象
 * @author 3979434@qq.com
 *
 */
public class ReplyBody implements Serializable {

	/**
	 * @author  3979434@qq.com 
	 * 服务端返回消息对象
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 请求key
	 */
	private String key;
	
	
	/**
	 * 返回码
	 */
	private String code;
	
	/**
	 * 返回说明
	 */
	private String message;

	/**
	 * 返回数据集合
	 */
	private HashMap<String, String> data;

	
	private long timestamp;
	
	public ReplyBody()
	{
		data = new HashMap<String, String>();
		timestamp = System.currentTimeMillis();
	}
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	 

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void put(String k, String v) {
		data.put(k, v);
	}

	public String get(String k) {
		return data.get(k);
	}

	public void remove(String k) {
		data.remove(k);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HashMap<String, String> getData() {
		return data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	
	public String toString()
	{
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<reply>");
		buffer.append("<key>").append(this.getKey()).append("</key>");
		buffer.append("<timestamp>").append(timestamp).append("</timestamp>");
		buffer.append("<code>").append(code).append("</code>");
		buffer.append("<data>");
		for(String key:this.getData().keySet())
		{
			buffer.append("<"+key+">").append(this.get(key)).append("</"+key+">");
		}
		buffer.append("</data>");
		buffer.append("</reply>");
		return buffer.toString();
	}

	
	public String toXmlString()
	{
		
		return toString();
	}
}
