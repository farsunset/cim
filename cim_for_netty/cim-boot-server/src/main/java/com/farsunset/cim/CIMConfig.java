package com.farsunset.cim;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.farsunset.cim.handler.BindHandler;
import com.farsunset.cim.handler.SessionClosedHandler;
import com.farsunset.cim.sdk.server.handler.CIMNioSocketAcceptor;
import com.farsunset.cim.sdk.server.handler.CIMRequestHandler;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.sdk.server.session.CIMSession;

@Configuration
public class CIMConfig implements CIMRequestHandler {
	@Value("${cim.server.port}")
	private int port;

	@Resource
	private BindHandler bindHandler;
	
	@Resource
	private SessionClosedHandler closedHandler;

	@Resource
	private ApplicationContext applicationContext;
	
	private HashMap<String,Class<? extends CIMRequestHandler>> appHandlerMap = new HashMap<>();

	@PostConstruct
	private void initHandler() {
		/*
		 * 账号绑定handler
		 */
		appHandlerMap.put("client_bind", BindHandler.class);
		/*
		 * 连接关闭handler
		 */
		appHandlerMap.put("client_closed", SessionClosedHandler.class);
	}

	@Bean
	public CIMNioSocketAcceptor getNioSocketAcceptor() throws IOException {
		CIMNioSocketAcceptor nioSocketAcceptor = new CIMNioSocketAcceptor();
		nioSocketAcceptor.setPort(port);
		nioSocketAcceptor.setAppSentBodyHandler(this);
		nioSocketAcceptor.bind();
		return nioSocketAcceptor;
	}

	
	@Override
	public void process(CIMSession session, SentBody body) {
		
        CIMRequestHandler handler = findHandlerByKey(body.getKey());
		
		if(handler == null) {return ;}
		
		handler.process(session, body);
		
	}

	private CIMRequestHandler findHandlerByKey(String key){
		Class<? extends CIMRequestHandler> handlerClass = appHandlerMap.get(key);
		if (handlerClass==null){
			return null;
		}
		return applicationContext.getBean(handlerClass);
	}
	
	

}