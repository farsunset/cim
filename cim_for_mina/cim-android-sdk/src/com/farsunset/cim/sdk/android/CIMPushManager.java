/**
 * probject:cim-android-sdk
 * @version 2.1.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.android;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.model.SentBody;

/**
 * CIM 功能接口
 */
public class CIMPushManager  {

	static String  ACTION_ACTIVATE_PUSH_SERVICE ="ACTION_ACTIVATE_PUSH_SERVICE";
	
	static String  ACTION_CREATE_CIM_CONNECTION ="ACTION_CREATE_CIM_CONNECTION";
	
	static String  ACTION_SEND_REQUEST_BODY ="ACTION_SEND_REQUEST_BODY";
	
	static String  ACTION_CLOSE_CIM_CONNECTION ="ACTION_CLOSE_CIM_CONNECTION";
	
	static String  ACTION_DESTORY ="ACTION_DESTORY";
	
	static String  KEY_SEND_BODY ="KEY_SEND_BODY";
	
	static String  KEY_CIM_CONNECTION_STATUS ="KEY_CIM_CONNECTION_STATUS";
	
	//被销毁的destroy()
	public static final int STATE_DESTROYED = 0x0000DE;
	//被销停止的 stop()
	public static final int STATE_STOPED = 0x0000EE;
	
	public static final int STATE_NORMAL = 0x000000;
	/**
	 * 初始化,连接服务端，在程序启动页或者 在Application里调用
	 * @param context
	 * @param ip
	 * @param port
	 */
	public static  void connect(Context context,String host,int port){
		
		connect(context,host,port,false);
		
	}
	
private static  void connect(Context context,String ip,int port,boolean autoBind){
		
		CIMCacheToolkit.getInstance(context).putBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED, false);
		CIMCacheToolkit.getInstance(context).putBoolean(CIMCacheToolkit.KEY_MANUAL_STOP, false);
		
		CIMCacheToolkit.getInstance(context).putString( CIMCacheToolkit.KEY_CIM_SERVIER_HOST, ip);
		CIMCacheToolkit.getInstance(context).putInt( CIMCacheToolkit.KEY_CIM_SERVIER_PORT, port);
		
		if(!autoBind)
		{
			CIMCacheToolkit.getInstance(context).remove(CIMCacheToolkit.KEY_ACCOUNT);
		}
		
		Intent serviceIntent  = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(CIMCacheToolkit.KEY_CIM_SERVIER_HOST, ip);
		serviceIntent.putExtra(CIMCacheToolkit.KEY_CIM_SERVIER_PORT, port);
		serviceIntent.setAction(ACTION_CREATE_CIM_CONNECTION);
		context.startService(serviceIntent);
		
	}

	protected static  void connect(Context context){
		
		boolean  isManualStop  = CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_MANUAL_STOP);
		boolean  isManualDestory  = CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
		
		if(isManualStop || isManualDestory)
		{
			return ;
		}
		
		String host = CIMCacheToolkit.getInstance(context).getString( CIMCacheToolkit.KEY_CIM_SERVIER_HOST);
    	int port =CIMCacheToolkit.getInstance(context).getInt( CIMCacheToolkit.KEY_CIM_SERVIER_PORT);
    	
    	connect(context,host,port,true);
		 
	}
	
	
	/**
	 * 设置一个账号登录到服务端
	 * @param account 用户唯一ID
	 */
    public static  void bindAccount(Context context,String account){
		
    	
    	boolean  isManualDestory  = CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
    	if(isManualDestory || account==null || account.trim().length()==0)
    	{
    		return ;
    	}
    	sendBindRequest(context,account);
		
	}
    
    
    private static void sendBindRequest(Context context, String account){
    	
    	CIMCacheToolkit.getInstance(context).putBoolean(CIMCacheToolkit.KEY_MANUAL_STOP, false);
    	CIMCacheToolkit.getInstance(context).putString(CIMCacheToolkit.KEY_ACCOUNT, account);
    	
    	String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    	imei += context.getPackageName();
    	SentBody sent = new SentBody();
		sent.setKey(CIMConstant.RequestKey.CLIENT_BIND);
		sent.put("account", account);
		sent.put("deviceId",UUID.nameUUIDFromBytes(imei.getBytes()).toString().replaceAll("-", ""));
		sent.put("channel", "android");
		sent.put("device",android.os.Build.MODEL);
		sent.put("version",getVersionName(context));
		sent.put("osVersion",android.os.Build.VERSION.RELEASE);
		sent.put("packageName",context.getPackageName());
		sendRequest(context,sent);
    }
    
    protected static  boolean  autoBindAccount(Context context){
    	
    	String account = CIMCacheToolkit.getInstance(context).getString(CIMCacheToolkit.KEY_ACCOUNT);
    	boolean  isManualDestory  =  CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
    	boolean  isManualStoped  =  CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_MANUAL_STOP);
    	if( isManualStoped || account==null || account.trim().length()==0 ||  isManualDestory )
    	{
    		return false;
    	}
    	
    	sendBindRequest(context,account);
    	
    	return true;
	}


    
    /**
	 * 发送一个CIM请求
	 * @param context
	 * @body
	 */
    public static  void sendRequest(Context context, SentBody body){
		
    	boolean  isManualStop  = CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_MANUAL_STOP);
		boolean  isManualDestory  = CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
		
		if(isManualStop || isManualDestory)
		{
			return ;
		}
 
    	Intent serviceIntent  = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(KEY_SEND_BODY, body);
		serviceIntent.setAction(ACTION_SEND_REQUEST_BODY);
		context.startService(serviceIntent);
		
	}
 
    /**
	 * 停止接受推送，将会退出当前账号登录，端口与服务端的连接
	 * @param context
	 */
    public static  void stop(Context context){
    	
    	boolean  isManualDestory  = CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
    	if(isManualDestory){
    		return ;
    	}
    	
    	CIMCacheToolkit.getInstance(context).putBoolean(CIMCacheToolkit.KEY_MANUAL_STOP, true);

    	Intent serviceIntent  = new Intent(context, CIMPushService.class);
		serviceIntent.setAction(ACTION_CLOSE_CIM_CONNECTION);
		context.startService(serviceIntent);
		
	}
    
    
    /**
	 * 完全销毁CIM，一般用于完全退出程序，调用resume将不能恢复
	 * @param context
	 */
    public static  void destroy(Context context){
    	
    	
    	CIMCacheToolkit.getInstance(context).putBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED, true);
    	CIMCacheToolkit.getInstance(context).remove(CIMCacheToolkit.KEY_ACCOUNT);
    	
    	Intent serviceIntent  = new Intent(context, CIMPushService.class);
		serviceIntent.setAction(ACTION_DESTORY);
		context.startService(serviceIntent);
		
	}
    
    
    /**
     * 重新恢复接收推送，重新连接服务端，并登录当前账号如果aotuBind == true
     * @param context
     * @param aotuBind
     */
    public static  void resume(Context context){
    	
    	boolean  isManualDestory  = CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
    	if(isManualDestory){
    		return ;
    	}
    	
    	autoBindAccount(context);
	}
    
    public static boolean isConnected(Context context){
    	return CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_CIM_CONNECTION_STATE);
    }
 
    public static int getState(Context context){
		boolean  isManualDestory  = CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
		if(isManualDestory){
			return STATE_DESTROYED;
		}
		
    	boolean  isManualStop  = CIMCacheToolkit.getInstance(context).getBoolean(CIMCacheToolkit.KEY_MANUAL_STOP);
    	if(isManualStop){
			return STATE_STOPED;
		}
    	
    	return STATE_NORMAL;
    }
    
    private  static String getVersionName(Context context) {
    	String  versionName = null;
		try {
			PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionName = mPackageInfo.versionName;
		} catch (NameNotFoundException e) {
		}
		return versionName;
	}
	
}