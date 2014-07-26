package com.farsunset.cim.client.android;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.farsunset.cim.nio.constant.CIMConstant;
import com.farsunset.cim.nio.mutual.SentBody;

/**
 * CIM 功能接口
 * 
 * @author 3979434@qq.com
 */
public class CIMPushManager  {

	
	static String  ACTION_CONNECTION ="ACTION_CONNECTION";
	
	static String  ACTION_CONNECTION_STATUS ="ACTION_CONNECTION_STATUS";
	
	static String  ACTION_SENDREQUEST ="ACTION_SENDREQUEST";
	
	static String  ACTION_DISCONNECTION ="ACTION_DISSENDREQUEST";
	
	static String  ACTION_DESTORY ="ACTION_DESTORY";
	
	static String  SERVICE_ACTION ="SERVICE_ACTION";
	
	static String  KEY_SEND_BODY ="KEY_SEND_BODY";
	
	static String  KEY_CIM_CONNECTION_STATUS ="KEY_CIM_CONNECTION_STATUS";
	
	/**
	 * 初始化,连接服务端，在程序启动页或者 在Application里调用
	 * @param context
	 * @param ip
	 * @param port
	 */
	public static  void init(Context context,String ip,int port){
		
		CIMDataConfig.putBoolean(context,CIMDataConfig.KEY_CIM_DESTORYED, false);
		
		Intent serviceIntent  = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(CIMDataConfig.KEY_CIM_SERVIER_HOST, ip);
		serviceIntent.putExtra(CIMDataConfig.KEY_CIM_SERVIER_PORT, port);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_CONNECTION);
		context.startService(serviceIntent);
		CIMDataConfig.putString(context, CIMDataConfig.KEY_CIM_SERVIER_HOST, ip);
		CIMDataConfig.putInt(context, CIMDataConfig.KEY_CIM_SERVIER_PORT, port);
	}
	
	protected static  void init(Context context){
		
		String host = CIMDataConfig.getString(context, CIMDataConfig.KEY_CIM_SERVIER_HOST);
    	int port =CIMDataConfig.getInt(context, CIMDataConfig.KEY_CIM_SERVIER_PORT);
    	
    	init(context,host,port);
		 
	}
	
	
	/**
	 * 设置一个账号登录到服务端
	 * @param account 用户唯一ID
	 */
    public static  void setAccount(Context context,String account){
		
    	if(account==null || account.trim().length()==0)
        {
    		return ;
    	}
    	CIMDataConfig.putString(context,CIMDataConfig.KEY_ACCOUNT, account);
    	
    	
    	boolean  isManualDestory  = CIMDataConfig.getBoolean(context,CIMDataConfig.KEY_CIM_DESTORYED);
    	if(isManualDestory || account==null){
    		return ;
    	}
    	
    	
    	String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    	SentBody sent = new SentBody();
		sent.setKey(CIMConstant.RequestKey.CLIENT_BIND);
		sent.put("account", account);
		sent.put("deviceId",imei);
		sent.put("channel", "android");
		sent.put("device",android.os.Build.MODEL);
		
		sendRequest(context,sent);
		
		
	}
    
    protected static  void setAccount(Context context){
    	
    	String account = CIMDataConfig.getString(context,CIMDataConfig.KEY_ACCOUNT);
    	setAccount(context,account);
	}

    protected static  void clearAccount(Context context){
    	
    	CIMDataConfig.putString(context,CIMDataConfig.KEY_ACCOUNT, null);
	}
    
    /**
	 * 发送一个CIM请求
	 * @param context
	 * @body
	 */
    public static  void sendRequest(Context context,SentBody body){
		
    	boolean  isManualDestory  = CIMDataConfig.getBoolean(context,CIMDataConfig.KEY_CIM_DESTORYED);
    	if(isManualDestory){
    		return ;
    	}
 
    	Intent serviceIntent  = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(KEY_SEND_BODY, body);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_SENDREQUEST);
		context.startService(serviceIntent);
		
	}
 
    /**
	 * 停止接受推送，将会退出当前账号登录，端口与服务端的连接
	 * @param context
	 */
    public static  void stop(Context context){
    	
    	boolean  isManualDestory  = CIMDataConfig.getBoolean(context,CIMDataConfig.KEY_CIM_DESTORYED);
    	if(isManualDestory){
    		return ;
    	}

    	CIMDataConfig.putBoolean(context,CIMDataConfig.KEY_MANUAL_STOP, true);
    	
    	Intent serviceIntent  = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_DISCONNECTION);
		context.startService(serviceIntent);
		
	}
    
    
    /**
	 * 完全销毁CIM，一般用于完全退出程序，调用resume将不能恢复
	 * @param context
	 */
    public static  void destory(Context context){
    	
    	
    	CIMDataConfig.putBoolean(context,CIMDataConfig.KEY_CIM_DESTORYED, true);
    	CIMDataConfig.putString(context,CIMDataConfig.KEY_ACCOUNT, null);
    	
    	Intent serviceIntent  = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_DESTORY);
		context.startService(serviceIntent);
		
	}
    
    
    /**
     * 重新恢复接收推送，重新连接服务端，并登录当前账号
     * @param context
     */
    public static  void resume(Context context){
    	
    	boolean  isManualDestory  = CIMDataConfig.getBoolean(context,CIMDataConfig.KEY_CIM_DESTORYED);
    	if(isManualDestory){
    		return ;
    	}
    	
    	setAccount(context);
	}
    
    
    /**
     * 异步获取与服务端连接状态,将会在广播中收到onConnectionStatus（boolean f）
     * @param context
     */
    public static void detectIsConnected(Context context){
    	Intent serviceIntent  = new Intent(context, CIMPushService.class);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_CONNECTION_STATUS);
		context.startService(serviceIntent);
    }
 
}