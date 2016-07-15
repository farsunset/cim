/**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.client.model;

import java.io.Serializable;
/**
 * 消息对象
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	
	/**
	 * 消息类型，用户自定义消息类别
	 */
	private String mid;
	
	
	/**
	 * 消息类型，用户自定义消息类别
	 */
	private String type;
	 
	private String content;

	private String file;
	
	private String fileType;
	/**
	 * 消息发送者账号
	 */
	private String sender;
	/**
	 * 消息发送者接收者
	 */
	private String receiver;

	/**
	 * content 内容格式
	 */
	private String format;

	
	private long timestamp;
	
	
	public Message()
	{
		timestamp = System.currentTimeMillis();
	}
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

 
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	 

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String toString() {

		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<message>");
		buffer.append("<mid>").append(mid).append("</mid>");

		if (isNotEmpty(type)) {
			buffer.append("<type>").append(type).append("</type>");
		}
 
		if (isNotEmpty(file)) {
			buffer.append("<file>").append(file).append("</file>");
		}
 
		if (isNotEmpty(fileType)) {
			buffer.append("<fileType>").append(fileType).append("</fileType>");
		}
		
		if (isNotEmpty(content)) {
			buffer.append("<content><![CDATA[").append(content).append("]]></content>");
		}
 
		if (isNotEmpty(sender)) {
			buffer.append("<sender>").append(sender).append("</sender>");
		}

		if (isNotEmpty(receiver)) {
			buffer.append("<receiver>").append(receiver).append("</receiver>");
		}

		if (isNotEmpty(format)) {
			buffer.append("<format>").append(format).append("</format>");
		}

		if (timestamp > 0) {
			buffer.append("<timestamp>").append(timestamp).append("</timestamp>");
		}

		buffer.append("</message>");
		return buffer.toString();
	}

	public String toXmlString() {

		return toString();
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public boolean isNotEmpty(String txt) {
		return txt != null && txt.trim().length()>0;
	}

	
}
