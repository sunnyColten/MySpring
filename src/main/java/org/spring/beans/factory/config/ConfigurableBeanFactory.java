package org.spring.beans.factory.config;

import org.spring.beans.factory.BeanFactory;

public interface ConfigurableBeanFactory extends BeanFactory {	
	void setBeanClassLoader(ClassLoader beanClassLoader);
	ClassLoader getBeanClassLoader();	
}
