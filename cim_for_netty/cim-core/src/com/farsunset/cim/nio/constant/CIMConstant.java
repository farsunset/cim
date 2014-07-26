 
package com.farsunset.cim.nio.constant;

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
	
	
	public static final Charset ENCODE_UTF8 = Charset.forName("UTF-8");
	
	public static byte  MESSAGE_SEPARATE='\b';
	
	
	public static int  CIM_DEFAULT_MESSAGE_ORDER=1;
	
	
    public static final String SESSION_KEY ="account";
	
	public static final String HEARTBEAT_KEY ="heartbeat";
	
	
	/**
	 * 对应ichat 中 spring-cim.xml  > bean:mainIoHandler >handlers
	 * 为 服务端处理对应的handlers，应该继承与com.farsunset.cim.nio.handle.AbstractHandler
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