/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import com.farsunset.cim.sdk.client.constant.CIMConstant;
import com.farsunset.cim.sdk.client.exception.CIMSessionDisableException;
import com.farsunset.cim.sdk.client.exception.WriteToClosedSessionException;
import com.farsunset.cim.sdk.client.filter.ClientMessageDecoder;
import com.farsunset.cim.sdk.client.filter.ClientMessageEncoder;
import com.farsunset.cim.sdk.client.model.Intent;
import com.farsunset.cim.sdk.client.model.Message;
import com.farsunset.cim.sdk.client.model.ReplyBody;
import com.farsunset.cim.sdk.client.model.SentBody;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 */
@io.netty.channel.ChannelHandler.Sharable 
class CIMConnectorManager  extends SimpleChannelInboundHandler<Object> {

	private  Channel channel;;

	
	Bootstrap bootstrap;
	EventLoopGroup loopGroup ;
	static CIMConnectorManager manager;

	// 消息广播action
	public static final String ACTION_MESSAGE_RECEIVED = "com.farsunset.cim.MESSAGE_RECEIVED";
	
	// 发送sendbody失败广播
	public static final String ACTION_SENT_FAILED = "com.farsunset.cim.SENT_FAILED";
	
	// 发送sendbody成功广播
	public static final String ACTION_SENT_SUCCESSED = "com.farsunset.cim.SENT_SUCCESSED";
	// 链接意外关闭广播
	public static final String ACTION_CONNECTION_CLOSED = "com.farsunset.cim.CONNECTION_CLOSED";
	// 链接失败广播
	public static final String ACTION_CONNECTION_FAILED = "com.farsunset.cim.CONNECTION_FAILED";
	// 链接成功广播
	public static final String ACTION_CONNECTION_SUCCESSED = "com.farsunset.cim.CONNECTION_SUCCESSED";
	// 发送sendbody成功后获得replaybody回应广播
	public static final String ACTION_REPLY_RECEIVED = "com.farsunset.cim.REPLY_RECEIVED";
	// 网络变化广播
	public static final String ACTION_NETWORK_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";
	
	// 未知异常
	public static final String ACTION_UNCAUGHT_EXCEPTION = "com.farsunset.cim.UNCAUGHT_EXCEPTION";

	//重试连接
	public final static String ACTION_CONNECTION_RECOVERY = "com.farsunset.cim.CONNECTION_RECOVERY";

	private ExecutorService executor;
	public static final String HEARTBEAT_PINGED ="HEARTBEAT_PINGED";
	//连接空闲时间
	public static final int READ_IDLE_TIME = 180;//秒
	
	//心跳超时
	public static final int HEART_TIME_OUT = 30 * 1000;//秒
	
	private CIMConnectorManager() {
		executor = Executors.newCachedThreadPool();
		bootstrap = new Bootstrap();
		loopGroup = new NioEventLoopGroup();
		bootstrap.group(loopGroup);
		bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ChannelPipeline pipeline = ch.pipeline();
                 pipeline.addLast(new ClientMessageDecoder(ClassResolvers.cacheDisabled(CIMConnectorManager.class.getClassLoader())));
            	 pipeline.addLast(new ClientMessageEncoder());
            	 pipeline.addLast(new IdleStateHandler(READ_IDLE_TIME,0,0));
            	 pipeline.addLast(CIMConnectorManager.this);
                 
             }
          });
		 
	}

	public synchronized static CIMConnectorManager getManager() {
		if (manager == null) {
			manager = new CIMConnectorManager();
		}
		return manager;

	}

	private synchronized void  syncConnection(final String cimServerHost,final int cimServerPort) {
		try {

			if(isConnected()){
				return ;
			}
			ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(cimServerHost, cimServerPort)).sync(); //这里的IP和端口，根据自己情况修改
			channel  = channelFuture.channel();
		} catch (Exception e) {
			
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_FAILED);
			intent.putExtra("exception", e);
			sendBroadcast(intent);
			
			
		}

	}

	public  void connect(final String cimServerHost, final int cimServerPort) {
		
		
    	Future<?> future = executor.submit(new Runnable() {
			@Override
			public void run() {
				syncConnection(cimServerHost, cimServerPort);
			}
		});
		try {
			if(future.get()!=null)
			{
				connect(cimServerHost,cimServerPort);
			}
		} catch (Exception e) {
			
			connect(cimServerHost,cimServerPort);
			e.printStackTrace();
		}  
	}

	public void send(final SentBody body) {

		 
		executor.execute(new Runnable() {
			@Override
			public void run() {

				
				if(channel!=null && channel.isActive())
				{
					boolean  isDone = channel.writeAndFlush(body).awaitUninterruptibly(5000);
					if (!isDone) {

						Intent intent = new Intent();
						intent.setAction(ACTION_SENT_FAILED);
						intent.putExtra("exception", new WriteToClosedSessionException());
						intent.putExtra("sentBody", body);
					    sendBroadcast(intent);
					}else
					{
						Intent intent = new Intent();
						intent.setAction(ACTION_SENT_SUCCESSED);
						intent.putExtra("sentBody", body);
						sendBroadcast(intent);
					}
				}else
				{
				 
					Intent intent = new Intent();
					intent.setAction(ACTION_SENT_FAILED);
					intent.putExtra("exception", new CIMSessionDisableException());
					intent.putExtra("sentBody", body);
					sendBroadcast(intent);
				}
			}
		});
	}

	public   void destroy() {
		if (manager.channel != null) {
			manager.channel.close();
		}
		loopGroup.shutdownGracefully();
		manager = null;
	}

	public boolean isConnected() {
		if (channel == null) {
			return false;
		}
		return channel.isActive() ;
	}

	
	public void closeSession()
	{
		if(channel!=null)
		{
			channel.close();
		}
	}


	/**
	  * 检测到连接空闲事件，发送心跳请求命令
	  */
	 @Override
	 public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state().equals(IdleState.READER_IDLE))
	        {
	        	onReaderIdeled(ctx.channel());
	        }
	        super.userEventTriggered(ctx, evt); 
	 }
	 
 
	
	 private void onReaderIdeled(Channel channel){
		 
		//如果心跳请求发出30秒内没收到响应，则关闭连接
		Long lastTime = (Long) channel.attr(AttributeKey.valueOf(HEARTBEAT_PINGED)).get();
    	if(lastTime != null && System.currentTimeMillis() - lastTime > HEART_TIME_OUT)
    	{
    		channel.close();
    	}
	 }
    
	@Override
	public void channelActive( ChannelHandlerContext ctx) throws Exception {

		System.out.println("******************CIM连接服务器成功:"+ctx.channel().localAddress());
		
		ctx.channel().attr(AttributeKey.valueOf(HEARTBEAT_PINGED)).set(System.currentTimeMillis());
		
		Intent intent = new Intent();
		intent.setAction(ACTION_CONNECTION_SUCCESSED);
		sendBroadcast(intent);
	}

		 
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		System.out.println("******************closeCIM与服务器断开连接:"+ctx.channel().localAddress());
		if(channel.id().asLongText().equals(ctx.channel().id().asLongText()))
		{
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_CLOSED);
			sendBroadcast(intent);

		}
	}

 
	@Override
	public  void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

			Intent intent = new Intent();
			intent.setAction(ACTION_UNCAUGHT_EXCEPTION);
			intent.putExtra("exception", cause.getCause());
			sendBroadcast(intent);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof Message) {
				Intent intent = new Intent();
				intent.setAction(ACTION_MESSAGE_RECEIVED);
				intent.putExtra("message", (Message) msg);
				sendBroadcast(intent);
			 
		}
		if (msg instanceof ReplyBody) {
				Intent intent = new Intent();
				intent.setAction(ACTION_REPLY_RECEIVED);
				intent.putExtra("replyBody", (ReplyBody) msg);
				sendBroadcast(intent);
		}
		
		//收到服务端发来的心跳请求命令，则马上回应服务器
		if (msg.equals(CIMConstant.CMD_HEARTBEAT_REQUEST)) {
			ctx.writeAndFlush(CIMConstant.CMD_HEARTBEAT_RESPONSE);
			ctx.channel().attr(AttributeKey.valueOf(HEARTBEAT_PINGED)).set(System.currentTimeMillis());
	    }
	}
 
	
	private void sendBroadcast(final Intent intent) {
		 executor.execute(new Runnable(){
			@Override
			public void run() {
				 CIMEventBroadcastReceiver.getInstance().onReceive(intent);
			}
		 });
	}

	
}