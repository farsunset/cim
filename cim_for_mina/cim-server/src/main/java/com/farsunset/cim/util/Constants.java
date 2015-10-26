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
		
		//系统向用户发送的普通消息
		public static final String TYPE_2 = "2";
		 
	}
	
}
