 
package com.farsunset.cim.server.constant;

import java.nio.charset.Charset;

/** 
 * 常量
 *
 * @author 3979434@qq.com
 */
public   interface  CIMConstant  {
    
	public static class ReturnCode{
		
		public static String CODE_404 ="404";
		
		public static String CODE_403 ="403";
		
		public static String CODE_405 ="405";
		
		public static String CODE_200 ="200";
		
		public static String CODE_206 ="206";
		
		public static String CODE_500 ="500";
		
		
	}
	
	//连接空闲时间
	public static final int IDLE_TIME = 60;//秒
	
	public static final int PING_TIME_OUT = 30;//心跳响应 超时为30秒
	
	
	public static final Charset ENCODE_UTF8 = Charset.forName("UTF-8");
	
	public static byte  MESSAGE_SEPARATE='\b';
	
	public static byte  FLEX_DATA_SEPARATE='\0';
	
	public static int  CIM_DEFAULT_MESSAGE_ORDER=1;
	
	
    public static final String SESSION_KEY ="account";
	
	public static final String HEARTBEAT_KEY ="heartbeat";
	
	/**
	 * 服务端心跳请求命令  
	 */
	public static final String CMD_HEARTBEAT_REQUEST="cmd_server_hb_request";
	/**
	 * 客户端心跳响应命令  
	 */
	public static final String CMD_HEARTBEAT_RESPONSE ="cmd_client_hb_response"; 
	
	public static final String HEARTBEAT_PINGED ="HEARTBEAT_PINGED";

	/**
	 * FLEX 客户端socket请求发的安全策略请求，需要特殊处理，返回安全验证报文
	 */
	public static final String FLEX_POLICY_REQUEST ="<policy-file-request/>";
	
	public static final String FLEX_POLICY_RESPONSE ="<?xml version=\"1.0\"?><cross-domain-policy><site-control permitted-cross-domain-policies=\"all\"/><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>\0"; 

	
	
	
	/**
	 * 对应cim-server 中 spring-cim.xml  > bean:mainIoHandler >handlers
	 * 为 服务端处理对应的handlers，应该继承与com.farsunset.cim.nio.handle.CIMRequestHandler
	 * @author xiajun
	 *
	 */
   public static class RequestKey{
		
	   
	     
		public static String CLIENT_BIND ="client_bind";
		
		public static String CLIENT_HEARTBEAT="client_heartbeat";
		
		public static String CLIENT_LOGOUT ="client_logout";
		
		
		public static String CLIENT_OFFLINE_MESSAGE ="client_get_offline_message";
		
		
	}
   
   public static class SessionStatus{
		
		public static int STATUS_OK =0;
		
		public static int STATUS_CLOSED =1;
		
	}
   
   
   public static class MessageType{
		
	    //账号在其他设备绑定时，会收到该类型消息
		public static String TYPE_999 ="999";
		
	}
}