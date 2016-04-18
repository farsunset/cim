/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */ 
package com.farsunset.cim.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
public class ContextHolder  implements ApplicationContextAware{

    private static  ApplicationContext context;
    
    public static  Object getBean(String name)
    {
    	return context.getBean(name);
    }
    
    public static  <T> T getBean(Class<T> c)
    {
    	
    	return context.getBean(c);
    }


	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}
}