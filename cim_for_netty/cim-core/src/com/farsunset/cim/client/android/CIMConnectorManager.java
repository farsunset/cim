package com.farsunset.cim.client.android;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.farsunset.cim.nio.filter.ClientMessageDecoder;
import com.farsunset.cim.nio.filter.ClientMessageEncoder;
import com.farsunset.cim.nio.mutual.Message;
import com.farsunset.cim.nio.mutual.ReplyBody;
import com.farsunset.cim.nio.mutual.SentBody;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 * @author 3979434@qq.com
 */
class CIMConnectorManager  {

	private  Channel channel;;

	Context context;
	
	ClientBootstrap bootstrap;

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

		
		  bootstrap = new ClientBootstrap(
	                new NioClientSocketChannelFactory(
	                        Executors.newCachedThreadPool(),
	                        Executors.newCachedThreadPool()));

	       bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
	            public ChannelPipeline getPipeline() throws Exception {
	                return Channels.pipeline(
	                        new ClientMessageDecoder(),
	                        new ClientMessageEncoder(),
	                        channelUpstreamHandler);
	            }
	       });

		 
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
			
			channel  = bootstrap.connect(new InetSocketAddress(cimServerHost, cimServerPort)).getChannel(); //这里的IP和端口，根据自己情况修改
			
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
				
				if(channel!=null && channel.isConnected())
				{
					boolean  isDone = channel.write(body).awaitUninterruptibly(5000);
					if (!isDone) {

						Intent intent = new Intent();
						intent.setAction(ACTION_SENT_FAILED);
						intent.putExtra("exception", new WriteToClosedSessionException());
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
		bootstrap.shutdown();
		bootstrap.releaseExternalResources();
		manager = null;
	}

	public boolean isConnected() {
		if (channel == null) {
			return false;
		}
		return channel.isConnected() ;
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


	SimpleChannelUpstreamHandler channelUpstreamHandler = new SimpleChannelUpstreamHandler() {

		@Override
		public void channelConnected( ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {

			System.out.println("******************CIM连接服务器成功:"+ctx.getChannel().getLocalAddress());
			
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_SUCCESS);
			context.sendBroadcast(intent);

		}

		 
		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {

			System.out.println("******************CIM与服务器断开连接:"+ctx.getChannel().getLocalAddress());
			if(channel.getId()==ctx.getChannel().getId())
			{
			   Intent intent = new Intent();
			   intent.setAction(ACTION_CONNECTION_CLOSED);
			   context.sendBroadcast(intent);

			}
		}

		 

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) 
				throws Exception {

			Intent intent = new Intent();
			intent.setAction(ACTION_UNCAUGHT_EXCEPTION);
			intent.putExtra("exception", e.getCause());
			context.sendBroadcast(intent);
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) 
				throws Exception {

			if (event.getMessage() instanceof Message) {

				Intent intent = new Intent();
				intent.setAction(ACTION_MESSAGE_RECEIVED);
				intent.putExtra("message", (Message) event.getMessage());
				context.sendBroadcast(intent);
			 
			}
			if (event.getMessage() instanceof ReplyBody) {

				
				Intent intent = new Intent();
				intent.setAction(ACTION_REPLY_RECEIVED);
				intent.putExtra("replyBody", (ReplyBody) event.getMessage());
				context.sendBroadcast(intent);
			}
		}

		@Override
		public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent event) 
				throws Exception {

			Intent intent = new Intent();
			intent.setAction(ACTION_SENT_SUCCESS);
			//intent.putExtra("sentBody", null);
			context.sendBroadcast(intent);
			
 
		}
	};
	
	public static boolean netWorkAvailable(Context context) {
		try {
			ConnectivityManager nw = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = nw.getActiveNetworkInfo();
			return networkInfo != null;
		} catch (Exception e) {}

		return false;
	}

	
}