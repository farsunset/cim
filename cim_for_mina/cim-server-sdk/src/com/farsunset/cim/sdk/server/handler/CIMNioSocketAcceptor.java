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
package com.farsunset.cim.sdk.server.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.farsunset.cim.sdk.server.constant.CIMConstant;
import com.farsunset.cim.sdk.server.filter.ServerMessageCodecFactory;
import com.farsunset.cim.sdk.server.model.HeartbeatRequest;
import com.farsunset.cim.sdk.server.model.HeartbeatResponse;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;

public class CIMNioSocketAcceptor extends IoHandlerAdapter implements KeepAliveMessageFactory{
	
	public final static String WEBSOCKET_HANDLER_KEY = "client_websocket_handshake";
	public final static String CIMSESSION_CLOSED_HANDLER_KEY = "client_cimsession_closed";
	private Logger logger = Logger.getLogger(CIMNioSocketAcceptor.class);
	private HashMap<String, CIMRequestHandler> handlers = new HashMap<String, CIMRequestHandler>();
	private IoAcceptor acceptor;
	private int port;
    private final int IDLE_TIME = 120;//秒
    private final int TIME_OUT = 10;//秒
    private final int READ_BUFFER_SIZE = 1024;//byte

    public void bind() throws IOException
    {
    
    	/**
    	 * 预制websocket握手请求的处理
    	 */
    	handlers.put(WEBSOCKET_HANDLER_KEY, new WebsocketHandler());
    	
    	acceptor = new NioSocketAcceptor();  
        acceptor.getSessionConfig().setReadBufferSize(READ_BUFFER_SIZE);  
        ((DefaultSocketSessionConfig)acceptor.getSessionConfig()).setKeepAlive(true);
        ((DefaultSocketSessionConfig)acceptor.getSessionConfig()).setTcpNoDelay(true);
        
        
        KeepAliveFilter keepAliveFilter = new KeepAliveFilter(this,IdleStatus.WRITER_IDLE);
        keepAliveFilter.setRequestInterval(IDLE_TIME);
        keepAliveFilter.setRequestTimeout(TIME_OUT);
        keepAliveFilter.setForwardEvent(true);
        
        acceptor.getFilterChain().addLast("executor",new ExecutorFilter());  
        acceptor.getFilterChain().addLast("logger",new LoggingFilter());  
        acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new ServerMessageCodecFactory())); 
        acceptor.getFilterChain().addLast("heartbeat",keepAliveFilter);  

        acceptor.setHandler(this);  
        
        acceptor.bind(new InetSocketAddress(port));
    }
    
    public void unbind()
    {
    	acceptor.unbind();
    }
     
	 
	public void sessionCreated(IoSession session) {
		logger.warn("sessionCreated()... from "+session.getRemoteAddress()+" nid:" + session.getId());
	}

	public void messageReceived(IoSession ios, Object message){
		 
		SentBody body = (SentBody) message;
		
		
		CIMRequestHandler handler = handlers.get(body.getKey());
		if (handler == null) {
			
			ReplyBody reply = new ReplyBody();
			reply.setKey(body.getKey());
			reply.setCode(CIMConstant.ReturnCode.CODE_404);
			reply.setMessage("KEY:"+body.getKey()+"  not defined on server");
			ios.write(reply);
			
		} else {
			ReplyBody reply = handler.process(new CIMSession(ios), body);
			if(reply!=null)
	        {
				reply.setKey(body.getKey());
	        	ios.write(reply);
	        }
		}
		
        
	}

	/**
	 */
	public void sessionClosed(IoSession session) {
		
		CIMSession cimSession =new  CIMSession(session);
		
		logger.warn("sessionClosed()... from "+session.getRemoteAddress()+" nid:"+cimSession.getNid() +",isConnected:"+session.isConnected());
		CIMRequestHandler handler = handlers.get(CIMSESSION_CLOSED_HANDLER_KEY);
		if(handler!=null)
		{
			handler.process(cimSession, null);
		}
	}

	/**
	 */
	public void sessionIdle(IoSession session, IdleStatus status)  {
		logger.warn("sessionIdle()... from "+session.getRemoteAddress()+" nid:" + session.getId());
	}

	/**
	 */
	public void exceptionCaught(IoSession session, Throwable cause){
		
		logger.error("exceptionCaught()... from "+session.getRemoteAddress()+" isConnected:"+session.isConnected()+" nid:" + session.getId(),cause);
		session.closeNow();
	}

	/**
	 */
	public void messageSent(IoSession session, Object message) throws Exception {
	}


	
	@Override
	public Object getRequest(IoSession session) {
		return HeartbeatRequest.getInstance();
	}

	@Override
	public Object getResponse(IoSession arg0, Object arg1) {
		return null;
	}

	@Override
	public boolean isRequest(IoSession arg0, Object arg1) {
		return false;
	}

	@Override
	public boolean isResponse(IoSession arg0, Object arg1) {
		return arg1 instanceof HeartbeatResponse;
	}
	
	public Map<Long,IoSession> getManagedSessions()
	{
		return acceptor.getManagedSessions();
	}
	
	public IoSession getManagedSession(Long nid)
	{
		if(nid == null)
		{
			return null;
		}
		
		return getManagedSessions().get(nid);
	}
	
	public void setAcceptor(IoAcceptor acceptor) {
		this.acceptor = acceptor;
	}
	 
	public void setPort(int port) {
		this.port = port;
	}

	public void setHandlers(HashMap<String, CIMRequestHandler> handlers) {
		this.handlers = handlers;
	}
	
}
