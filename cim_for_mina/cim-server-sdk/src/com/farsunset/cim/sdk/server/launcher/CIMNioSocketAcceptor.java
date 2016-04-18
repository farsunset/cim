/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */  
package com.farsunset.cim.sdk.server.launcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.farsunset.cim.sdk.server.handler.ServerKeepAliveFactoryImpl;

public class CIMNioSocketAcceptor {
	IoAcceptor acceptor;
    IoHandler ioHandler;
    int port;
    
    private final int IDLE_TIME = 300;//秒
    private final int TIME_OUT = 10;//秒
    public void bind() throws IOException
    {
    	acceptor = new NioSocketAcceptor();  
        acceptor.getSessionConfig().setReadBufferSize(1024);  
        ((DefaultSocketSessionConfig)acceptor.getSessionConfig()).setTcpNoDelay(true);
        acceptor.getSessionConfig().setBothIdleTime(IDLE_TIME);
        acceptor.getFilterChain().addLast("executor",new ExecutorFilter());  
        acceptor.getFilterChain().addLast("logger",new LoggingFilter());  
        acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new com.farsunset.cim.sdk.server.filter.ServerMessageCodecFactory()));  
        KeepAliveMessageFactory heartBeatFactory = new ServerKeepAliveFactoryImpl();  
        KeepAliveFilter keepAliveFilter = new KeepAliveFilter(heartBeatFactory,IdleStatus.BOTH_IDLE,KeepAliveRequestTimeoutHandler.CLOSE,IDLE_TIME,TIME_OUT);  
        keepAliveFilter.setForwardEvent(true);  
        acceptor.getFilterChain().addLast("heartbeat",keepAliveFilter);  
        acceptor.setHandler(ioHandler);  
        
        acceptor.bind(new InetSocketAddress(port));
    }
    
    public void unbind()
    {
    	acceptor.unbind();
    }
	public void setAcceptor(IoAcceptor acceptor) {
		this.acceptor = acceptor;
	}
	public void setIoHandler(IoHandler ioHandler) {
		this.ioHandler = ioHandler;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public  Map<Long, IoSession> getManagedSessions()
	{
		return acceptor.getManagedSessions();
	}
}
