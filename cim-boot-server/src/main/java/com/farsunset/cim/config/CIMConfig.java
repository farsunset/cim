package com.farsunset.cim.config;

import com.farsunset.cim.component.handler.annotation.CIMHandler;
import com.farsunset.cim.component.predicate.HandshakePredicate;
import com.farsunset.cim.config.properties.CIMProperties;
import com.farsunset.cim.sdk.server.group.SessionGroup;
import com.farsunset.cim.sdk.server.group.TagSessionGroup;
import com.farsunset.cim.sdk.server.handler.CIMNioSocketAcceptor;
import com.farsunset.cim.sdk.server.handler.CIMRequestHandler;
import com.farsunset.cim.sdk.server.model.SentBody;
import com.farsunset.cim.service.SessionService;
import io.netty.channel.Channel;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CIMConfig implements CIMRequestHandler, ApplicationListener<ApplicationStartedEvent> {

	@Resource
	private ApplicationContext applicationContext;

	@Resource
	private SessionService sessionService;

	private final HashMap<String,CIMRequestHandler> handlerMap = new HashMap<>();

	@Bean
	public SessionGroup sessionGroup() {
		return new SessionGroup();
	}

	@Bean
	public TagSessionGroup tagSessionGroup() {
		return new TagSessionGroup();
	}


	@Bean(destroyMethod = "destroy")
	public CIMNioSocketAcceptor getNioSocketAcceptor(CIMProperties properties, HandshakePredicate handshakePredicate) {

		return new CIMNioSocketAcceptor.Builder()
				.setAppPort(properties.getAppPort())
				.setWebsocketPort(properties.getWebsocketPort())
				.setWebsocketPath(properties.getWebsocketPath())
				.setHandshakePredicate(handshakePredicate)
				.setOuterRequestHandler(this)
				.build();

	}

	@Override
	public void process(Channel channel, SentBody body) {
		
        CIMRequestHandler handler = handlerMap.get(body.getKey());
		
		if(handler == null) {return ;}
		
		handler.process(channel, body);
		
	}
	/*
	 * springboot启动完成之后再启动cim服务的，避免服务正在重启时，客户端会立即开始连接导致意外异常发生.
	 */
	@Override
	public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {

		Map<String, CIMRequestHandler> beans =  applicationContext.getBeansOfType(CIMRequestHandler.class);

		for (Map.Entry<String, CIMRequestHandler> entry : beans.entrySet()) {

			CIMRequestHandler handler = entry.getValue();

			CIMHandler annotation = handler.getClass().getAnnotation(CIMHandler.class);

			if (annotation != null){
				handlerMap.put(annotation.key(),handler);
			}
		}


		applicationContext.getBean(CIMNioSocketAcceptor.class).bind();

		sessionService.deleteLocalhost();
	}
}