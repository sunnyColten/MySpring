package org.spring.beans.factory.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spring.beans.factory.config.SingletonBeanRegistry;
import org.spring.util.Assert;


public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
	
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(64);

	//往容器中注册单例
	public void registerSingleton(String beanName, Object singletonObject) {
		
		Assert.notNull(beanName, "'beanName' must not be null");
		
		Object oldObject = this.singletonObjects.get(beanName);
		if (oldObject != null) {
			throw new IllegalStateException("Could not register object [" + singletonObject +
					"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
		}
		this.singletonObjects.put(beanName, singletonObject);
		
	}

	//从容器中获取单例
	public Object getSingleton(String beanName) {
		
		return this.singletonObjects.get(beanName);
	}

}
