package com.farsunset.cim.config;

import com.farsunset.cim.handler.BindHandler;
import com.farsunset.cim.handler.SessionClosedHandler;
import com.farsunset.cim.sdk.server.handler.CIMNioSocketAcceptor;
import com.farsunset.cim.sdk.server.handler.CIMRequestHandler;
import com.farsunset.cim.sdk.server.model.CIMSession;
import com.farsunset.cim.sdk.server.model.SentBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;

@Configuration
public class CIMConfig implements CIMRequestHandler, ApplicationListener<ApplicationStartedEvent> {

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

	@Bean(destroyMethod = "destroy")
	public CIMNioSocketAcceptor getNioSocketAcceptor(@Value("${cim.server.port}") int port) {
		CIMNioSocketAcceptor nioSocketAcceptor = new CIMNioSocketAcceptor();
		nioSocketAcceptor.setPort(port);
		nioSocketAcceptor.setAppSentBodyHandler(this);
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


	/**
	 * springboot启动完成之后再启动cim服务的，避免服务正在重启时，客户端会立即开始连接导致意外异常发生.
	 */
	@Override
	public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
		applicationContext.getBean(CIMNioSocketAcceptor.class).bind();
	}
}