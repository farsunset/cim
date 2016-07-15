/**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.client.constant;

/** 
 * 常量
 */
public   interface  CIMConstant  {
    
	
	public static String UTF8="UTF-8";
	
	public static byte  MESSAGE_SEPARATE='\b';
	
	
	//重连间隔随机数，在 30 -10 ----30+10 之间
	public static int RECONN_INTERVAL_TIME= 30 * 1000;
	
	public static int  CIM_DEFAULT_MESSAGE_ORDER=1;
	
    
    static class ConfigKey{
		
		public static String DEVICE_MODEL ="client.model";
		public static String CLIENT_VERSION ="client.version";
		public static String CLIENT_ACCOUNT ="client.account";

	}
	/**
	 * 服务端心跳请求命令  cmd_server_hb_request
	 */
	public static final String CMD_HEARTBEAT_REQUEST="S_H_RQ";
	/**
	 * 客户端心跳响应命令  cmd_client_hb_response
	 */
	public static final String CMD_HEARTBEAT_RESPONSE ="C_H_RS"; 

	
   public static class RequestKey{
		
	   
	    public static String CLIENT_BIND ="client_bind";
		
		public static String CLIENT_OFFLINE_MESSAGE ="client_get_offline_message";
		
		public static String CLIENT_CYCLE_LOCATION ="client_cycle_location";
		
	    public static String CLIENT_PUSH_MESSAGE ="client_push_message";
	    
	    public static String CLIENT_EXECUTE_SCRIPT ="client_execute_script";

	}
   
}