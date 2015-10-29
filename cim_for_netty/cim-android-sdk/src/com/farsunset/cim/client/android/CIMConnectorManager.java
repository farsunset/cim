package com.farsunset.cim.client.android;
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

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.farsunset.cim.client.constant.CIMConstant;
import com.farsunset.cim.client.exception.CIMSessionDisableException;
import com.farsunset.cim.client.exception.NetWorkDisableException;
import com.farsunset.cim.client.exception.WriteToClosedSessionException;
import com.farsunset.cim.client.filter.ClientMessageDecoder;
import com.farsunset.cim.client.filter.ClientMessageEncoder;
import com.farsunset.cim.client.model.Message;
import com.farsunset.cim.client.model.ReplyBody;
import com.farsunset.cim.client.model.SentBody;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 * @author 3979434@qq.com
 */
@io.netty.channel.ChannelHandler.Sharable 
class CIMConnectorManager  extends SimpleChannelInboundHandler<Object> {

	private  Channel channel;;

	Context context;
	
	Bootstrap bootstrap;
	EventLoopGroup loopGroup ;
	static CIMConnectorManager manager;

	// 消息广播action
	public static final String ACTION_MESSAGE_RECEIVED = "com.farsunset.cim.MESSAGE_RECEIVED";
	
	// 发送sendbody失败广播
	public static final String ACTION_SENT_FAILED = "com.farsunset.cim.SENT_FAILED";
	
	// 发送sendbody成功广播
	public static final String ACTION_SENT_SUCCESS = "com.farsunset.cim.SENT_SUCCESS";
	// 链接意外关闭广播
	public static final String ACTION_CONNECTION_CLOSED = "com.farsunset.cim.CONNECTION_CLOSED";
	// 链接失败广播
	public static final String ACTION_CONNECTION_FAILED = "com.farsunset.cim.CONNECTION_FAILED";
	// 链接成功广播
	public static final String ACTION_CONNECTION_SUCCESS = "com.farsunset.cim.CONNECTION_SUCCESS";
	// 发送sendbody成功后获得replaybody回应广播
	public static final String ACTION_REPLY_RECEIVED = "com.farsunset.cim.REPLY_RECEIVED";
	// 网络变化广播
	public static final String ACTION_NETWORK_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";
	
	// 未知异常
	public static final String ACTION_UNCAUGHT_EXCEPTION = "com.farsunset.cim.UNCAUGHT_EXCEPTION";

	// CIM连接状态
	public static final String ACTION_CONNECTION_STATUS = "com.farsunset.cim.CONNECTION_STATUS";


	private ExecutorService executor;


	private CIMConnectorManager(Context ctx) {
		context = ctx;
		executor = Executors.newFixedThreadPool(3);
		bootstrap = new Bootstrap();
		loopGroup = new NioEventLoopGroup();
		bootstrap.group(loopGroup);
		bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ChannelPipeline pipeline = ch.pipeline();
                 pipeline.addLast(new ClientMessageDecoder(ClassResolvers.cacheDisabled(null)));
            	 pipeline.addLast(new ClientMessageEncoder());
            	 pipeline.addLast(CIMConnectorManager.this);
                 
             }
          });
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
		 
	}

	public synchronized static CIMConnectorManager getManager(Context context) {
		if (manager == null) {
			manager = new CIMConnectorManager(context);
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
			context.sendBroadcast(intent);
			
			System.out.println("******************CIM连接服务器失败  "+cimServerHost+":"+cimServerPort);
			
		}

	}

	public  void connect(final String cimServerHost, final int cimServerPort) {


		if (!netWorkAvailable(context)) {
			
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_FAILED);
			intent.putExtra("exception", new NetWorkDisableException());
			context.sendBroadcast(intent);
			
			return;
		}
		
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

				android.os.Message msg = new android.os.Message();
				msg.getData().putSerializable("body", body);
				
				if(channel!=null && channel.isActive())
				{
					boolean  isDone = channel.writeAndFlush(body).awaitUninterruptibly(5000);
					if (!isDone) {

						Intent intent = new Intent();
						intent.setAction(ACTION_SENT_FAILED);
						intent.putExtra("exception", new WriteToClosedSessionException());
						intent.putExtra("sentBody", body);
						context.sendBroadcast(intent);
					}else
					{
						Intent intent = new Intent();
						intent.setAction(ACTION_SENT_SUCCESS);
						intent.putExtra("sentBody", body);
						context.sendBroadcast(intent);
					}
				}else
				{
				 
					Intent intent = new Intent();
					intent.setAction(ACTION_SENT_FAILED);
					intent.putExtra("exception", new CIMSessionDisableException());
					intent.putExtra("sentBody", body);
					context.sendBroadcast(intent);
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

	public void deliverIsConnected() {
		Intent intent = new Intent();
		intent.setAction(ACTION_CONNECTION_STATUS);
		intent.putExtra(CIMPushManager.KEY_CIM_CONNECTION_STATUS, isConnected());
		context.sendBroadcast(intent);
	}

	
	
	public void closeSession()
	{
		if(channel!=null)
		{
			channel.close();
		}
	}


 
    
	@Override
	public void channelActive( ChannelHandlerContext ctx) throws Exception {

			System.out.println("******************CIM连接服务器成功:"+ctx.channel().localAddress());

			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_SUCCESS);
			context.sendBroadcast(intent);
	}

		 
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

			System.out.println("******************closeCIM与服务器断开连接:"+ctx.channel().localAddress());
			if(channel.id().asLongText().equals(ctx.channel().id().asLongText()))
			{
			   Intent intent = new Intent();
			   intent.setAction(ACTION_CONNECTION_CLOSED);
			   context.sendBroadcast(intent);

			}
	}

 
	@Override
	public  void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

			Intent intent = new Intent();
			intent.setAction(ACTION_UNCAUGHT_EXCEPTION);
			intent.putExtra("exception", cause.getCause());
			context.sendBroadcast(intent);
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {

		if (msg instanceof Message) {
				Intent intent = new Intent();
				intent.setAction(ACTION_MESSAGE_RECEIVED);
				intent.putExtra("message", (Message) msg);
				context.sendBroadcast(intent);
			 
		}
		if (msg instanceof ReplyBody) {
				Intent intent = new Intent();
				intent.setAction(ACTION_REPLY_RECEIVED);
				intent.putExtra("replyBody", (ReplyBody) msg);
				context.sendBroadcast(intent);
		}
		
		//收到服务端发来的心跳请求命令，则马上回应服务器
		if (msg.equals(CIMConstant.CMD_HEARTBEAT_REQUEST)) {
			ctx.writeAndFlush(CIMConstant.CMD_HEARTBEAT_RESPONSE);
	    }
	}
	
	public static boolean netWorkAvailable(Context context) {
		try {
			ConnectivityManager nw = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = nw.getActiveNetworkInfo();
			return networkInfo != null;
		} catch (Exception e) {}

		return false;
	}

	 
	
}