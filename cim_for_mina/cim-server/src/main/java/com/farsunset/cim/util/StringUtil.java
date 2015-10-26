package com.farsunset.cim.util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class StringUtil {
	 
	 
	public static boolean isEmpty(Object obj)
	{
		if(null == obj)
			return true;
		if("".equals(obj.toString().trim()))
		{
			return true;
		}
		return false;
	}
 
	public static boolean isNotEmpty(Object obj)
	{
		 
		return !isEmpty(obj);
	}
	public static String getSequenceId()
	{
		String mark = String.valueOf(System.currentTimeMillis());
		return mark;
	}
	 
	public static String getCurrentlyDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyyMMddHHmmss");
		return dateFormat.format(new Date());
	}
	
	public static String transformDateTime(long t) {
		Date date = new Date(t);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
	
	public static String getCurrentlyDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(new Date());
	}
	 
		
}
