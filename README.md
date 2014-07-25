#说明：此开源版本为基础功能版本，只有消息推送的基础功能！

#基于netty版本的下载地址：http://pan.baidu.com/s/1sjyhpAd


#前言：

   现在市面上有很多 xmpp协议的即时通讯方案，openfire androidpn，等等。它们都是使用了apache mina开发，但是这些东西基本都需要二次改造开发。而且改动还很大，我也看过这些东西的源码，发现代码结构不太理想，耦合的情况太多，实在不好扩展。所谓XMPP 协议。只不过是别人使用mina 自定义了一个消息编码解码协议。通俗的讲就是，xml形式消息的编码与解码，我们完全没有必要在国外这套不成熟的openfire 与xmpp 上耗费过多的精力去研究，我们完全可以通过apache mina 自定义自己的通讯协议，并可以为它使用自己的名字。我们不要盲目崇拜国外的有些东西，自己掌握原理，才是最重要的，各位切记~

   这套IM系统为我个人自主开发 使用了 apache mina ，主要功能为 服务端和客户端，客户端 到客户端的即时通信，可以支持包括文字 图片，语音等任何消息形式 服务端使用的 struts2+spring3和 apache mina android端 也使用的apache mina。这套IM系统结构还是非常清晰合理的，非常容易扩展和改造，下面是android版本 的 demo的目的是只是一个演示 ，可以参照它的代码，使用这套系统开发自己的东西，核心价值是一套高灵活性，相对标准化的即时通讯解决方案，即时聊天只是它的一种运用途径！


#服务端集群配置方案
###服务端修改
1.多台服务器集群配置，首先需要重写SessionManager接口(参考com.farsunset.ichat.cim.session.ClusterSessionManager.java)，用户登录时，将账号和服务器IP 存入数据库中，这样就可以统计各台服务器接受的连接数量。
2.客户端连接服务器时，服务端为客户端动态分配 服务器IP，每次分配 较为空闲的服务器IP
3.服务端接受消息后 通过接收者账号查询出对应的Iosession，和 登录的 服务器IP，然后将消息信息传往目标服务器处理发送


###客户端端修改
1.客户端登录时将不在是固定的服务器IP 而是先通过http接口获取到当前空闲的服务器IP，然后登录
     
#成功案例
#http://www.eoeandroid.com/thread-300586-1-1.html


 
 

##客户端接收消息
![image](http://staticres.oss-cn-hangzhou.aliyuncs.com/cim-android_client.png)

##服务端消息 web入口
#http://192.168.1.11:8080/ichat-server

![image](http://staticres.oss-cn-hangzhou.aliyuncs.com/cim-server.png)



##常用功能接口
所有开放外部接口都集中在
com.farsunset.cim.client.android.CIMPushManager
```java

1.1连接服务器
   /**
     * 初始化,连接服务端，在程序启动页或者 在Application里调用
	 * @param context
	 * @param ip
	 * @param port
	 */
	public static  void init(Context context,String ip,int port)

示例
CIMPushManager.init(context,"125.12.35.231",28888);



1.2绑定账号到服务端
    /**
	 * 设置一个账号登录到服务端
	 * @param account 用户唯一ID
	 */
public static  void setAccount(Context context,String account)

示例
CIMPushManager.setAccount(context,"xiyang");




1.3发送一个CIM请求
 酌情使用此功能，可用http接口替代

   /**
	 * 发送一个CIM请求
	 * @param context
	 * @param ip
	 * @param port
	 */
public static  void sendRequest(Context context,SentBody body)

示例：获取离线消息
    SentBody sent = new SentBody();
	sent.setKey(CIMConstant.RequestKey.CLIENT_OFFLINE_MESSAGE);
	sent.put("account", "xiyang");
	CIMPushManager.sendRequest(context, sent);

该功能需要服务端实现，详情参考CIM服务端及辅助文档.doc > 1.2.3



1.4停止接收消息

    /**
	 * 停止接受推送，将会退出当前账号登录，端口与服务端的连接
	 * @param context
	 */
    public static  void stop(Context context)
     示例：
     CIMPushManager.stop(context);    
     
     
     
1.5恢复接收消息

   /**
     * 重新恢复接收推送，重新连接服务端，并登录当前账号
     * @param context
     */
    public static  void resume(Context context)
     示例：
     CIMPushManager.resume(context);    



1.6完全销毁连接

   /**
	 * 完全销毁CIM，一般用于完全退出程序，调用resume将不能恢复
	 * @param context
	 */
    public static  void destory(Context context)
     示例：
     CIMPushManager.destory(context);    



1.7获取是否与服务端连接正常

    /**
     * 异步获取与服务端连接状态,将会在广播中收到onConnectionStatus（boolean f）
     * @param context
     */
    public void detectIsConnected(Context context)   

   示例：
   CIMPushManager.detectIsConnected(context);   



1.8推送消息以及相关事件的接收

首先注册一个广播，并监听以下action 参照 后面androidManifest.xml配置

参考CustomCIMMessageReceiver的实现
    /**
	 * 当收到消息时调用此方法
	 */
	public void onMessageReceived(message){}
	
    /**
     * 当手机网络变化时调用此方法
     * @param info
     */
	public void onNetworkChanged(NetworkInfo info)


    /**
     * 当调用CIMPushManager.sendRequest()获得相应时 调用此方法
     * ReplyBody.key 将是对应的 SentBody.key 
     * @param info
     */
	public void onReplyReceived(ReplyBody body)

	
	/**
     * 获取到是否连接到服务端
     * 通过调用CIMPushManager.detectIsConnected()来异步获取
     * 
     */
    public abstract void onConnectionStatus(boolean  isConnected)
```