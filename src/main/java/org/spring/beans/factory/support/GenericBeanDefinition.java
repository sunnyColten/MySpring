package org.spring.beans.factory.support;

import java.util.ArrayList;
import java.util.List;

import org.spring.beans.BeanDefinition;
import org.spring.beans.ConstructorArgument;
import org.spring.beans.PropertyValue;

/**
 * 用于存放如何创建Bean的相关信息，BeanFactory根据该信息创建Bean实例
 * @author zzf
 */

public class GenericBeanDefinition implements BeanDefinition {
	//存放<bean>标签的id
	private String id;
	//存放<bean>标签的class
	private String beanClassName;
	
	private boolean singleton = true;
	private boolean prototype = false;
	//默认为单例
	private String scope = SCOPE_DEFAULT;
	
	//存放<property>标签信息
	List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
	//存放<constructor-arg>标签信息
	private ConstructorArgument constructorArgument = new ConstructorArgument();
	
	public GenericBeanDefinition(String id, String beanClassName) {
		
		this.id = id;
		this.beanClassName = beanClassName;
	}
	public String getBeanClassName() {
		
		return this.beanClassName;
	}
	
	public boolean isSingleton() {
		return this.singleton;
	}
	public boolean isPrototype() {
		return this.prototype;
	}
	public String getScope() {
		return this.scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
		this.singleton = SCOPE_SINGLETON.equals(scope) || SCOPE_DEFAULT.equals(scope);
		this.prototype = SCOPE_PROTOTYPE.equals(scope);
		
	}
	public List<PropertyValue> getPropertyValues(){
		return this.propertyValues;
	}
	public ConstructorArgument getConstructorArgument() {
		return this.constructorArgument;
	}
	public String getID() {
		return this.id;
	}
	public boolean hasConstructorArgumentValues() {
		return !this.constructorArgument.isEmpty();
	}
}
