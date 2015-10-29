/**
 * probject:cim-android-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.client.android;

import android.net.NetworkInfo;

import com.farsunset.cim.client.model.Message;
import com.farsunset.cim.client.model.ReplyBody;

/**
 *CIM 主要事件接口
 */
public interface CIMEventListener
{


    /**
     * 当收到服务端推送过来的消息时调用
     * @param message
     */
    public  void onMessageReceived(Message message);

    /**
     * 当调用CIMPushManager.sendRequest()向服务端发送请求，获得相应时调用
     * @param replybody
     */
    public  void onReplyReceived(ReplyBody replybody);

    /**
     * 当手机网络发生变化时调用
     * @param networkinfo
     */
    public  void onNetworkChanged(NetworkInfo networkinfo);
    
    /**
     * 获取到是否连接到服务端
     * 通过调用CIMPushManager.detectIsConnected()来异步获取
     * 
     */
    public  void onConnectionStatus(boolean  isConnected);
    
    /**
     * 连接服务端成功
     */
    public void onCIMConnectionSucceed();
	
	
	/**
     * 连接断开
     */
    public void onCIMConnectionClosed();
}

