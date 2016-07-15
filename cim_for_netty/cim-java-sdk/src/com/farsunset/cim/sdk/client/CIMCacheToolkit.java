/**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.client;

import java.util.HashMap;

class CIMCacheToolkit  {

	private static HashMap<String,String> CIM_CONFIG_INFO = new HashMap<String,String>();
	
	public static final String KEY_MANUAL_STOP = "KEY_MANUAL_STOP";
	
	public static final String KEY_CIM_DESTROYED = "KEY_CIM_DESTROYED";
	
	public static final String KEY_CIM_SERVIER_HOST = "KEY_CIM_SERVIER_HOST";

	public static final String KEY_CIM_SERVIER_PORT = "KEY_CIM_SERVIER_PORT";
	
	public static final String KEY_CIM_CONNECTION_STATE = "KEY_CIM_CONNECTION_STATE";

	static  CIMCacheToolkit toolkit;
	public static CIMCacheToolkit getInstance(){
         if (toolkit==null){
			 toolkit = new CIMCacheToolkit();
		 }
		return toolkit;
	}
	
	 


	public  void remove(String key)
	{
		CIM_CONFIG_INFO.remove(key);
	}


	public  void putString(String key,String value)
	{
		CIM_CONFIG_INFO.put(key,value);

	}
	
	public  String getString(String key)
	{
		return CIM_CONFIG_INFO.get(key);
	}
	
	public  void putBoolean(String key,boolean value)
	{
		putString(key,Boolean.toString(value));
	}
	
	public  boolean getBoolean(String key)
	{
		String value = getString(key);
		return value == null?false:Boolean.parseBoolean(value);
	}
	
	
	public  void putInt(String key,int value)
	{
		putString(key, String.valueOf(value));
	}
	
	public  int getInt(String key)
	{
		String value = getString(key);
		return value == null?0:Integer.parseInt(value);
	}
	 
}
