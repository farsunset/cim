package com.farsunset.ichat.example.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.telephony.TelephonyManager;


public class StringUtil {
	 
	 
	public static boolean isEmpty(Object obj)
	{
		 
		return null == obj || "".equals(obj.toString().trim());
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
	 
	public static String getIMEI(Context context)
	{
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		
	}
}
