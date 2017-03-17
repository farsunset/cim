/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim.sdk.android;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import com.farsunset.cim.sdk.android.constant.CIMConstant;
import com.farsunset.cim.sdk.android.exception.SessionDisconnectedException;
import com.farsunset.cim.sdk.android.filter.ClientMessageDecoder;
import com.farsunset.cim.sdk.android.filter.ClientMessageEncoder;
import com.farsunset.cim.sdk.android.exception.NetworkDisconnectedException;
import com.farsunset.cim.sdk.android.model.HeartbeatRequest;
import com.farsunset.cim.sdk.android.model.HeartbeatResponse;
import com.farsunset.cim.sdk.android.model.Message;
import com.farsunset.cim.sdk.android.model.ReplyBody;
import com.farsunset.cim.sdk.android.model.SentBody;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 * @author 3979434@qq.com
 */
@Sharable
class CIMConnectorManager extends SimpleChannelInboundHandler<Object> {

	private final String TAG = CIMConnectorManager.class.getSimpleName();
	private final int CONNECT_TIMEOUT = 10 * 1000;//秒
	private final int WRITE_TIMEOUT = 10 * 1000;//秒

	private final int READ_IDLE_TIME = 120;//秒
	private final int HEARBEAT_TIME_OUT = (READ_IDLE_TIME + 20) * 1000;// 收到服务端心跳请求超时时间 毫秒
	private final String KEY_LAST_HEART_TIME =  "KEY_LAST_HEART_TIME" ;
		
	private Bootstrap bootstrap;
	private EventLoopGroup loopGroup ;
	private Channel channel;;
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	private Context context;
	
	private static CIMConnectorManager manager;

	

	private CIMConnectorManager(Context ctx) {
		context = ctx;

		bootstrap = new Bootstrap();
		loopGroup = new NioEventLoopGroup();
		bootstrap.group(loopGroup);
		bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ch.pipeline().addLast(new ClientMessageDecoder());
                 ch.pipeline().addLast(new ClientMessageEncoder());
                 ch.pipeline().addLast(new IdleStateHandler(READ_IDLE_TIME,0,0));
                 ch.pipeline().addLast(CIMConnectorManager.this);
             }
          });

	}

	public synchronized static CIMConnectorManager getManager(Context context) {
		if (manager == null) {
			manager = new CIMConnectorManager(context);
		}
		return manager;

	}

	private  synchronized void syncConnection( String host, int port) {
		
		if(isConnected()){
			return ;
		}
		
		try {

			Log.i(TAG, "****************CIM正在连接服务器  "+host+":"+port+"......");
			ChannelFuture channelFuture = bootstrap.connect(host, port).syncUninterruptibly();  
			channel  = channelFuture.channel();
		} catch (Exception e) {
			
			long interval = CIMConstant.RECONN_INTERVAL_TIME - (5*1000 - new Random().nextInt(15*1000));

			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_FAILED);
			intent.putExtra(Exception.class.getName(), e);
			intent.putExtra("interval", interval);
			context.sendBroadcast(intent);
			
			Log.e(TAG, "****************CIM连接服务器失败  "+host+":"+port+"......将在"+interval/1000+"秒后重新尝试连接");
		 
		}  

	}

	public void connect(final String host, final int port) {

		if (!isNetworkConnected(context)) {
			
			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_FAILED);
			intent.putExtra(Exception.class.getName(), new NetworkDisconnectedException());
			context.sendBroadcast(intent);
			
			return;
		}
	
		executor.execute(new Runnable() {
			public void run() 
			{
				syncConnection(host, port);
		    }
		});
		 
	}

	public synchronized void send(SentBody body) {

		boolean isSuccessed = false;
		
		Throwable exception = new SessionDisconnectedException();
		
		if(channel!=null && channel.isActive())
		{
			isSuccessed = channel.writeAndFlush(body).awaitUninterruptibly(WRITE_TIMEOUT);
		} 
		
		if(!isSuccessed){
			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_SENT_FAILED);
			intent.putExtra(Exception.class.getName(),exception);
			intent.putExtra(SentBody.class.getName(), body);
			context.sendBroadcast(intent);
		}else
		{
			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_SENT_SUCCESSED);
			intent.putExtra(SentBody.class.getName(), (SentBody) body);
			context.sendBroadcast(intent);
		}
		
	}

	public   void destroy() {
		if (channel != null) {
			channel.close();
		}

		if (loopGroup != null) {
			loopGroup.shutdownGracefully();
		}
		
		
		CIMCacheToolkit.destroy();
		
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


	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		
		Log.i(TAG, "****************CIM连接服务器成功:"+ctx.channel().localAddress()+" NID:"+ctx.channel().id().asShortText());
		
		setLastHeartbeatTime(ctx.channel());
		
		Intent intent = new Intent();
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_SUCCESSED);
		context.sendBroadcast(intent);

	}
	 
	@Override
	public void channelInactive(ChannelHandlerContext ctx)  {

		Log.e(TAG, "****************CIM与服务器断开连接:"+ctx.channel().localAddress()+" NID:"+ctx.channel().id().asShortText());
		 
		Intent intent = new Intent();
		intent.setAction(CIMConstant.IntentAction.ACTION_CONNECTION_CLOSED);
		context.sendBroadcast(intent);

	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)  {
		
		/**
		 * 用于解决，wifi情况下。偶而路由器与服务器断开连接时，客户端并没及时收到关闭事件
		 * 导致这样的情况下当前连接无效也不会重连的问题
		 * 
		 */
		if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state().equals(IdleState.READER_IDLE))
        {
			Log.d(TAG, "****************CIM "+IdleState.READER_IDLE+":"+ctx.channel().localAddress()+" NID:"+ctx.channel().id().asShortText());

			Long lastTime = getLastHeartbeatTime(ctx.channel());
	    	if(lastTime != null && System.currentTimeMillis() - lastTime > HEARBEAT_TIME_OUT)
	    	{
	    		channel.close();
				Log.e(TAG, "****************CIM心跳超时 ,即将重新连接......"+" NID:"+ctx.channel().id().asShortText());
	    	}
        }
		 
	}

	@Override
	public  void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		
		Log.e(TAG, "****************CIM连接出现未知异常:"+ctx.channel().localAddress()+" NID:"+ctx.channel().id().asShortText());
		
		if(cause!=null && cause.getMessage()!=null){
			Log.e(TAG, cause.getMessage());
		}
		
		Intent intent = new Intent();
		intent.setAction(CIMConstant.IntentAction.ACTION_UNCAUGHT_EXCEPTION);
		intent.putExtra(Exception.class.getName(), cause);
		context.sendBroadcast(intent);
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception{
		if (msg instanceof Message) {

			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_MESSAGE_RECEIVED);
			intent.putExtra(Message.class.getName(), (Message) msg);
			context.sendBroadcast(intent);
		 
		}
		if (msg instanceof ReplyBody) {
			
			Intent intent = new Intent();
			intent.setAction(CIMConstant.IntentAction.ACTION_REPLY_RECEIVED);
			intent.putExtra(ReplyBody.class.getName(), (ReplyBody) msg);
			context.sendBroadcast(intent);
		}
		
		//收到服务端发来的心跳请求命令，则马上回应服务器
		if (msg instanceof HeartbeatRequest) {
			ctx.writeAndFlush(HeartbeatResponse.getInstance());
			setLastHeartbeatTime(ctx.channel());
		}
				
	}

	private void setLastHeartbeatTime(Channel channel)
	{
		channel.attr(AttributeKey.valueOf(KEY_LAST_HEART_TIME)).set(System.currentTimeMillis());
	}
	
	private Long  getLastHeartbeatTime(Channel channel)
	{
		return (Long) channel.attr(AttributeKey.valueOf(KEY_LAST_HEART_TIME)).get();
	}
	
	public static boolean isNetworkConnected(Context context) {
		try {
			ConnectivityManager nw = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = nw.getActiveNetworkInfo();
			return networkInfo != null;
		} catch (Exception e) {}

		return false;
	}
 
}
