 
package com.farsunset.cim.nio.handle;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.farsunset.cim.nio.constant.CIMConstant;
import com.farsunset.cim.nio.filter.ServerMessageDecoder;
import com.farsunset.cim.nio.filter.ServerMessageEncoder;
import com.farsunset.cim.nio.mutual.ReplyBody;
import com.farsunset.cim.nio.mutual.SentBody;
import com.farsunset.cim.nio.session.CIMSession;

/**
 *  
 * 客户端请求的入口，所有请求都首先经过它分发处理
 * @author farsunset (3979434@qq.com)
 */
public class MainIOHandler extends SimpleChannelUpstreamHandler {

	protected final Logger logger = Logger.getLogger(MainIOHandler.class);

	private HashMap<String, CIMRequestHandler> handlers = new HashMap<String, CIMRequestHandler>();


	private int port;
	
	public void init()
	{
		ServerBootstrap bootstrap = new ServerBootstrap(  
                new NioServerSocketChannelFactory(  
                        Executors.newCachedThreadPool(),  
                        Executors.newCachedThreadPool()));  
  
        bootstrap.setPipelineFactory(new ChannelPipelineFactory()  
        {  
  
            @Override  
            public ChannelPipeline getPipeline() throws Exception  
            {  
            	return Channels.pipeline(
                        new ServerMessageDecoder(),
                        new ServerMessageEncoder(),
                        MainIOHandler.this);
               
            }  
  
        });  
          
        bootstrap.bind(new InetSocketAddress(port));  
        logger.warn("netty 启动成功" + port);

	}
	 
	public void  channelConnected(ChannelHandlerContext ctx, ChannelStateEvent event) 
	{
		logger.warn("sessionCreated()... from "+ctx.getChannel().getRemoteAddress().toString());
	}

	 
	 
	 
	 public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)  
			throws Exception {
		logger.debug("message: " + e.getMessage().toString());

		CIMSession cimSession =new  CIMSession(ctx.getChannel());
		ReplyBody reply = new ReplyBody();
		SentBody body = (SentBody) e.getMessage();
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
    		logger.debug("-----------------------process done. reply: " + reply.toString());
        }
        
        //设置心跳时间 
        cimSession.setAttribute(CIMConstant.HEARTBEAT_KEY, System.currentTimeMillis());
	}

	 public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent event) {
			CIMSession cimSession =new  CIMSession(ctx.getChannel());
			try{
				logger.warn("sessionClosed()... from "+cimSession.getRemoteAddress());
				CIMRequestHandler handler = handlers.get("sessionClosedHander");
				if(handler!=null && cimSession.containsAttribute(CIMConstant.SESSION_KEY))
				{
					handler.process(cimSession, null);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	 
	 
	/**
	 *//*
	 public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent event) {
		CIMSession cimSession =new  CIMSession(ctx.getChannel());
		try{
			logger.warn("sessionClosed()... from "+cimSession.getRemoteAddress());
			CIMRequestHandler handler = handlers.get("sessionClosedHander");
			if(handler!=null && cimSession.containsAttribute(CIMConstant.SESSION_KEY))
			{
				handler.process(cimSession, null);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}*/

	public void setHandlers(HashMap<String, CIMRequestHandler> handlers) {
		this.handlers = handlers;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	

}