package org.spring.beans.factory.support;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spring.beans.BeanDefinition;
import org.spring.beans.PropertyValue;
import org.spring.beans.SimpleTypeConverter;
import org.spring.beans.factory.BeanCreationException;
import org.spring.beans.factory.config.ConfigurableBeanFactory;
import org.spring.util.ClassUtils;

public class DefaultBeanFactory extends DefaultSingletonBeanRegistry 
	implements ConfigurableBeanFactory,BeanDefinitionRegistry{

	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);
	private ClassLoader beanClassLoader;
	
	public DefaultBeanFactory() {
		
	}

	public void registerBeanDefinition(String beanID,BeanDefinition bd){
		this.beanDefinitionMap.put(beanID, bd);
	}
	public BeanDefinition getBeanDefinition(String beanID) {
			
		return this.beanDefinitionMap.get(beanID);
	}

	public Object getBean(String beanID) {
		BeanDefinition bd = this.getBeanDefinition(beanID);
		if(bd == null){
			return null;
		}
		
		//是否为单例
		if(bd.isSingleton()){
			Object bean = this.getSingleton(beanID);
			if(bean == null){
				bean = createBean(bd);
				this.registerSingleton(beanID, bean);
			}
			return bean;
		} 
		//不是单例
		return createBean(bd);
	}
	private Object createBean(BeanDefinition bd) {
		//创建实例
		Object bean = instantiateBean(bd);
		//设置属性
		populateBean(bd, bean);
		
		return bean;		
		
	}
	private Object instantiateBean(BeanDefinition bd) {
		//构造器注入 只有在创建bean的时候会用到 所以放这里
		if(bd.hasConstructorArgumentValues()){
			ConstructorResolver resolver = new ConstructorResolver(this);
			return resolver.autowireConstructor(bd);
		}else{
			//使用默认构造器
			ClassLoader cl = this.getBeanClassLoader();
			String beanClassName = bd.getBeanClassName();
			try {
				Class<?> clz = cl.loadClass(beanClassName);
				return clz.newInstance();
			} catch (Exception e) {			
				throw new BeanCreationException("create bean for "+ beanClassName +" failed",e);
			}	
		}
	}
	
	protected void populateBean(BeanDefinition bd, Object bean){
		List<PropertyValue> pvs = bd.getPropertyValues();
		
		if (pvs == null || pvs.isEmpty()) {
			return;
		}
		BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this);
		try{
			for (PropertyValue pv : pvs){
				String propertyName = pv.getName();
				//originalValue可能是RuntimeBeanReference或TypedStringValue
				Object originalValue = pv.getValue();
				//将RuntimeBeanReference解析成bean  将TypedStringValue解析成值返回
				Object resolvedValue = valueResolver.resolveValueIfNecessary(originalValue);
				//使用BeanUtils工具类注入属性值
				BeanUtils.setProperty(bean, propertyName, resolvedValue);
			}
		}catch(Exception ex){
			throw new BeanCreationException("Failed to obtain BeanInfo for class [" + bd.getBeanClassName() + "]", ex);
		}	
	}

	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}

    public ClassLoader getBeanClassLoader() {
		return (this.beanClassLoader != null ? this.beanClassLoader : ClassUtils.getDefaultClassLoader());
	}
}
