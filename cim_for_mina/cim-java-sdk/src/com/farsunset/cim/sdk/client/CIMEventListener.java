/**
 * probject:cim-java-sdk
 * @version 2.0.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.client;


import com.farsunset.cim.sdk.client.model.Message;
import com.farsunset.cim.sdk.client.model.ReplyBody;

/**
 *CIM 主要事件接口
 */
public interface CIMEventListener
{


    /**
     * 当收到服务端推送过来的消息时调用
     * @param message
     */
    public abstract void onMessageReceived(Message message);

    /**
     * 当调用CIMPushManager.sendRequest()向服务端发送请求，获得相应时调用
     * @param replybody
     */
    public abstract void onReplyReceived(ReplyBody replybody);

    
    
    /**
     * 当连接服务器成功时回调
     * @param hasAutoBind  : true 已经自动绑定账号到服务器了，不需要再手动调用bindAccount
     */
    public abstract void onConnectionSuccessed(boolean hasAutoBind);
    
    /**
     * 当断开服务器连接的时候回调
     */
    public abstract void onConnectionClosed();
    
    /**
     * 当服务器连接失败的时候回调
     * 
     */
    public abstract void onConnectionFailed(Exception e);
}

