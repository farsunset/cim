package com.farsunset.cim.launcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.farsunset.cim.server.constant.CIMConstant;
import com.farsunset.cim.server.filter.ServerMessageDecoder;
import com.farsunset.cim.server.filter.ServerMessageEncoder;
import com.farsunset.cim.server.handler.CIMRequestHandler;
import com.farsunset.cim.server.mutual.ReplyBody;
import com.farsunset.cim.server.mutual.SentBody;
import com.farsunset.cim.server.session.CIMSession;
 
@io.netty.channel.ChannelHandler.Sharable 
public class CIMNioSocketAcceptor extends SimpleChannelInboundHandler<Object>{

	protected final Logger logger = Logger.getLogger(CIMNioSocketAcceptor.class.getSimpleName());
    public final static String KEY_CLIENT_CIMSESSION_CLOSED = "client_cimsession_closed";
	private HashMap<String, CIMRequestHandler> handlers = new HashMap<String, CIMRequestHandler>();

	private int port;
	

	 
	public void bind()  
	{
		new Thread(new Runnable(){
			@Override
			public void run() {
				EventLoopGroup bossGroup = new NioEventLoopGroup();
		        EventLoopGroup workerGroup = new NioEventLoopGroup();
				try{
					ServerBootstrap bootstrap = new ServerBootstrap();
					bootstrap.group(bossGroup, workerGroup);
					bootstrap.channel(NioServerSocketChannel.class);
			        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			                 @Override
			                 public void initChannel(SocketChannel ch) throws Exception {
			                	 ChannelPipeline pipeline = ch.pipeline();

			                	 pipeline.addLast(new ServerMessageDecoder(ClassResolvers.cacheDisabled(null)));
			                	 pipeline.addLast(new ServerMessageEncoder());
			                	 pipeline.addLast(new IdleStateHandler(CIMConstant.IDLE_TIME/2,0,CIMConstant.IDLE_TIME));
			                	 pipeline.addLast(CIMNioSocketAcceptor.this);
			                 }
			             });
			        
			       bootstrap.bind(port).sync().channel().closeFuture().sync();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				finally {
		            workerGroup.shutdownGracefully();
		            bossGroup.shutdownGracefully();
		        }
			}
		}).start();
	}

	
	@Override
	public void channelRegistered( ChannelHandlerContext ctx) throws Exception {

		logger.debug("channelRegistered()... from "+ctx.channel().remoteAddress());
	}

	
	
	 /**
	  * 检测到连接空闲事件，发送心跳请求命令
	  */
	 @Override
	 public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state().equals(IdleState.ALL_IDLE)) {
	        	ctx.channel().attr(AttributeKey.valueOf(CIMConstant.HEARTBEAT_PINGED)).set(System.currentTimeMillis());
        		ctx.channel().writeAndFlush(CIMConstant.CMD_HEARTBEAT_REQUEST);
	        }
	        
	        //如果心跳请求发出30秒内没收到响应，则关闭连接
	        if (evt instanceof IdleStateEvent 
	        		&& ((IdleStateEvent) evt).state().equals(IdleState.READER_IDLE)
	        		&& ctx.channel().attr(AttributeKey.valueOf(CIMConstant.HEARTBEAT_PINGED)).get()!=null)
	        {
	        	long t = (Long) ctx.channel().attr(AttributeKey.valueOf(CIMConstant.HEARTBEAT_PINGED)).get();
	        	if(System.currentTimeMillis() - t >= CIMConstant.PING_TIME_OUT)
	        	{
	        	   ctx.channel().close();
	        	}
	        }
	        super.userEventTriggered(ctx, evt); 
	 }
	
	 protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		/**
		 * flex 客户端安全策略请求，需要返回特定报文
		 */
		if(CIMConstant.FLEX_POLICY_REQUEST.equals(msg))
		{
			ctx.writeAndFlush(CIMConstant.FLEX_POLICY_RESPONSE);
			return ;
		}
		CIMSession cimSession =new  CIMSession(ctx.channel());
		ReplyBody reply = new ReplyBody();
		SentBody body = (SentBody)msg;
		String key = body.getKey();

		CIMRequestHandler handler = handlers.get(key);
		if (handler == null) {
			reply.setCode(CIMConstant.ReturnCode.CODE_405);
			reply.setCode("KEY ["+key+"] 服务端未定义");
		} else {
			reply = handler.process(cimSession, body);
		}
		
        if(reply!=null)
        {
        	reply.setKey(key);
        	cimSession.write(reply);
        }
        
	}
	 
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		CIMSession cimSession =new  CIMSession(ctx.channel());
		try{
			logger.debug("channelInactive()... from "+cimSession.getRemoteAddress());
			CIMRequestHandler handler = handlers.get(KEY_CLIENT_CIMSESSION_CLOSED);
			if(handler!=null && cimSession.hasTag(CIMConstant.SESSION_KEY))
			{
				handler.process(cimSession, null);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	 
	public void setHandlers(HashMap<String, CIMRequestHandler> handlers) {
		this.handlers = handlers;
	}

	public void setPort(int port) {
		this.port = port;
	}

}