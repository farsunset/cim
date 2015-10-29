package com.farsunset.cim.util;

public interface Constants {

	 
	public class Common{  
		public final static String STATUS_1="1";
		public final static String STATUS_0="0";
		public final static String STATUS_2="2";
		public final static String STATUS_3="3";
		public final static String TYPR_1="1";
		public final static String TYPR_2="2";
		public final static String TYPR_9 = "9";
	   
	}
	public class User{
		public final static Integer SUCCESS_CODE=1;
		public final static Integer ERROR_CODE=0;
		public final static String User="User";

	}
	
	public class RequestParam
	{
		public final static String CURRENTPAGE="currentPage";
		public final static String TYPE="type";
		public static final String SIZE = "size";
		
	}
	
	public class Number{
		
		public final static Integer INT_0=0;
		public final static Integer INT_10=10;
	    public final static Integer INT_403=403;

	}
	
	public static interface MessageType{
		
		
		//用户之间的普通消息
		public static final String TYPE_0 = "0";
		
		public static final String TYPE_1 = "1";
		
		//系统向用户发送的普通消息
		public static final String TYPE_2 = "2";
		
		//群里用户发送的  消息
		public static final String TYPE_3 = "3";
		
		//系统定制消息---好友验证请求
		String TYPE_100 = "100";
		
		//系统定制消息---同意好友请求
		String TYPE_101 = "101";
		
		//系统定制消息---进群请求
		String TYPE_102 = "102";
		
		//系统定制消息---同意进群请求
		String TYPE_103 = "103";
		
	 
		//系统定制消息---好友下线消息
		String TYPE_900 = "900";
		
		//系统定制消息---好友上线消息
		String TYPE_901 = "901";
	}
	
}
