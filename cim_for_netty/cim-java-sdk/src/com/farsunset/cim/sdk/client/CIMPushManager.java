/**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.sdk.client;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Properties;


import com.farsunset.cim.sdk.client.constant.CIMConstant;
import com.farsunset.cim.sdk.client.model.Intent;
import com.farsunset.cim.sdk.client.model.SentBody;

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
	
	public static  void connect(String ip,int port){
    	
		CIMCacheToolkit.getInstance().putBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED, false);
		CIMCacheToolkit.getInstance().putBoolean(CIMCacheToolkit.KEY_MANUAL_STOP, false);
		
		CIMCacheToolkit.getInstance().putString( CIMCacheToolkit.KEY_CIM_SERVIER_HOST, ip);
		CIMCacheToolkit.getInstance().putInt( CIMCacheToolkit.KEY_CIM_SERVIER_PORT, port);
		
		Intent serviceIntent  = new Intent();
		serviceIntent.putExtra(CIMCacheToolkit.KEY_CIM_SERVIER_HOST, ip);
		serviceIntent.putExtra(CIMCacheToolkit.KEY_CIM_SERVIER_PORT, port);
		serviceIntent.setAction(ACTION_CREATE_CIM_CONNECTION);
		startService(serviceIntent);
		
	}

	private static void startService(Intent intent) {
		CIMPushService.getInstance().onStartCommand(intent);
    }

	protected static  void connect(){
		
		boolean  isManualStop  = CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_MANUAL_STOP);
		boolean  isManualDestory  = CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
		
		if(isManualStop || isManualDestory)
		{
			return ;
		}
		
		String host = CIMCacheToolkit.getInstance().getString( CIMCacheToolkit.KEY_CIM_SERVIER_HOST);
    	int port =CIMCacheToolkit.getInstance().getInt( CIMCacheToolkit.KEY_CIM_SERVIER_PORT);
    	
    	connect(host,port);
		 
	}
	
    
    private static void sendBindRequest(String account){
    	
    	CIMCacheToolkit.getInstance().putBoolean(CIMCacheToolkit.KEY_MANUAL_STOP, false);
    	SentBody sent = new SentBody();
    	Properties sysPro=System.getProperties(); 
		sent.setKey(CIMConstant.RequestKey.CLIENT_BIND);
		sent.put("account", account);
		sent.put("deviceId", getLocalMac());
		sent.put("channel", sysPro.getProperty("os.name"));
		sent.put("device",getDeviceModel());
		sent.put("version",getClientVersion());
		sent.put("osVersion",sysPro.getProperty("os.version"));
		sendRequest(sent);
    }
    
    /**
	 * 设置一个账号登录到服务端
	 * @param account 用户唯一ID
	 */
    public static  void bindAccount(String account){
		
    	
    	boolean  isManualDestory  =  CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
    	if(isManualDestory || account==null || account.trim().length()==0)
    	{
    		return ;
    	}
    	sendBindRequest(account);
		
	}
    
    protected static boolean  autoBindDeviceId(){
    	
    	String account = getAccount();
    	
    	
    	
    	
    	 
    	boolean  isManualDestory  =  CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
    	boolean  isManualStoped  =  CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_MANUAL_STOP);
    	if( isManualStoped || account==null || account.trim().length()==0 ||  isManualDestory )
    	{
    		return false;
    	}
    	
    	sendBindRequest(account);
    	
    	return true;
	}


    
    /**
	 * 发送一个CIM请求
	 * @param context
	 * @body
	 */
    public static  void sendRequest(SentBody body){
		
    	boolean  isManualStop  = CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_MANUAL_STOP);
		boolean  isManualDestory  = CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
		
		if(isManualStop || isManualDestory)
		{
			return ;
		}
 
    	Intent serviceIntent  = new Intent();
		serviceIntent.putExtra(KEY_SEND_BODY, body);
		serviceIntent.setAction(ACTION_SEND_REQUEST_BODY);
		startService(serviceIntent);
		
	}
 
    /**
	 * 停止接受推送，将会退出当前账号登录，端口与服务端的连接
	 * @param context
	 */
    public static  void stop(){
    	
    	boolean  isManualDestory  = CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
    	if(isManualDestory){
    		return ;
    	}
    	
    	CIMCacheToolkit.getInstance().putBoolean(CIMCacheToolkit.KEY_MANUAL_STOP, true);

    	Intent serviceIntent  = new Intent();
		serviceIntent.setAction(ACTION_CLOSE_CIM_CONNECTION);
		startService(serviceIntent);
		
	}
    
    
    /**
	 * 完全销毁CIM，一般用于完全退出程序，调用resume将不能恢复
	 * @param context
	 */
    public static  void destroy(){
    	
    	
    	CIMCacheToolkit.getInstance().putBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED, true);
    	
    	Intent serviceIntent  = new Intent();
		serviceIntent.setAction(ACTION_DESTORY);
		startService(serviceIntent);
		
	}
    
    
    /**
     * 重新恢复接收推送，重新连接服务端，并登录当前账号如果aotuBind == true
     * @param context
     * @param aotuBind
     */
    public static  void resume(){
    	
    	boolean  isManualDestory  = CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
    	if(isManualDestory){
    		return ;
    	}
    	
    	autoBindDeviceId();
	}
    
    public static boolean isConnected(){
    	return CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_CIM_CONNECTION_STATE);
    }
 
    public static int getState(){
		boolean  isManualDestory  = CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_CIM_DESTROYED);
		if(isManualDestory){
			return STATE_DESTROYED;
		}
		
    	boolean  isManualStop  = CIMCacheToolkit.getInstance().getBoolean(CIMCacheToolkit.KEY_MANUAL_STOP);
    	if(isManualStop){
			return STATE_STOPED;
		}
    	
    	return STATE_NORMAL;
    }
    
    
    public static String getDeviceModel(){
    	return System.getProperties().getProperty(CIMConstant.ConfigKey.DEVICE_MODEL);
    }
    
    public static String getClientVersion(){
    	return System.getProperties().getProperty(CIMConstant.ConfigKey.CLIENT_VERSION);
    }
    public static String getAccount(){
    	return System.getProperties().getProperty(CIMConstant.ConfigKey.CLIENT_ACCOUNT);
    }
    public static void setAccount(String account){
    	 System.getProperties().put(CIMConstant.ConfigKey.CLIENT_ACCOUNT,account);
    }
    public static void setClientVersion(String version){
   	     System.getProperties().put(CIMConstant.ConfigKey.CLIENT_VERSION,version);
    }
    
    public static void setDeviceModel(String model){
      	 System.getProperties().put(CIMConstant.ConfigKey.DEVICE_MODEL,model);
    }
    
    private static String getLocalMac()  {
    	InetAddress ia;
		try {
			ia = InetAddress.getLocalHost();
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
			StringBuffer sb = new StringBuffer("");
			for(int i=0; i<mac.length; i++) {
				if(i!=0) {
					sb.append("-");
				}
				//字节转换为整数
				int temp = mac[i]&0xff;
				String str = Integer.toHexString(temp);
				if(str.length()==1) {
					sb.append("0"+str);
				}else {
					sb.append(str);
				}
			}
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}