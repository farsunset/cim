package com.farsunset.cim.sdk.server.launcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
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

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.filter.ServerMessageDecoder;
import com.farsunset.cim.sdk.server.filter.ServerMessageEncoder;
import com.farsunset.cim.sdk.server.handler.CIMRequestHandler;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;
 
@io.netty.channel.ChannelHandler.Sharable 
public class CIMNioSocketAcceptor extends SimpleChannelInboundHandler<SentBody>{

	protected final Logger logger = Logger.getLogger(CIMNioSocketAcceptor.class.getSimpleName());
	private HashMap<String, CIMRequestHandler> handlers = new HashMap<String, CIMRequestHandler>();

	private int port;
	//连接空闲时间
	public static final int READ_IDLE_TIME = 180;//秒
	
	//连接空闲时间
	public static final int WRITE_IDLE_TIME = 150;//秒
	
	public static final int PING_TIME_OUT = 30;//心跳响应 超时为30秒
	
	public static final String HEARTBEAT_PINGED ="HEARTBEAT_PINGED";
	 
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
					bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
					bootstrap.channel(NioServerSocketChannel.class);
			        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			                 @Override
			                 public void initChannel(SocketChannel ch) throws Exception {
			                	 ChannelPipeline pipeline = ch.pipeline();

			                	 pipeline.addLast(new ServerMessageDecoder(ClassResolvers.cacheDisabled(null)));
			                	 pipeline.addLast(new ServerMessageEncoder());
			                	 pipeline.addLast(new IdleStateHandler(READ_IDLE_TIME,WRITE_IDLE_TIME,0));
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
	 */
	public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.error("exceptionCaught()... from "+ctx.channel().remoteAddress());
		ctx.channel().close();
  	    CIMRequestHandler handler = handlers.get(CIMConstant.RequestKey.KEY_CLIENT_CIMSESSION_CLOSED);
  	    handler.process(new CIMSession(ctx.channel()), null);
	}

	
	 /**
	  * 检测到连接空闲事件，发送心跳请求命令
	  */
	 @Override
	 public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		 
	        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state().equals(IdleState.WRITER_IDLE)) {
	        	onWriterIdeled(ctx.channel());
	        }
	        
	        //如果心跳请求发出30秒内没收到响应，则关闭连接
	        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state().equals(IdleState.READER_IDLE))
	        {
	        	onReaderIdeled(ctx.channel());
	        }
	        super.userEventTriggered(ctx, evt); 
	 }
	 
	 
	 private void onWriterIdeled(Channel channel){
		 channel.attr(AttributeKey.valueOf(HEARTBEAT_PINGED)).set(System.currentTimeMillis());
		 channel.writeAndFlush(CIMConstant.CMD_HEARTBEAT_REQUEST);
	 }
	
	 private void onReaderIdeled(Channel channel){
		Long lastTime = (Long) channel.attr(AttributeKey.valueOf(HEARTBEAT_PINGED)).get();
     	if(lastTime == null || System.currentTimeMillis() - lastTime >= PING_TIME_OUT)
     	{
     	   channel.close();
     	   CIMRequestHandler handler = handlers.get(CIMConstant.RequestKey.KEY_CLIENT_CIMSESSION_CLOSED);
     	   handler.process(new CIMSession(channel), null);
     	}
     	
     	channel.attr(AttributeKey.valueOf(HEARTBEAT_PINGED)).set(null);
	 }
	 
	
	 protected void channelRead0(ChannelHandlerContext ctx, SentBody body) throws Exception {
		
	
	 
		CIMSession cimSession =new  CIMSession(ctx.channel());
		ReplyBody reply = new ReplyBody();
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
		logger.debug("channelInactive-----------"+ctx.channel().remoteAddress());
	}

	 
	public void setHandlers(HashMap<String, CIMRequestHandler> handlers) {
		this.handlers = handlers;
	}

	public void setPort(int port) {
		this.port = port;
	}

}