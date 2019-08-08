package org.spring.beans.factory.support;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.spring.beans.BeanDefinition;
import org.spring.beans.ConstructorArgument;
import org.spring.beans.SimpleTypeConverter;
import org.spring.beans.factory.BeanCreationException;
import org.spring.beans.factory.config.ConfigurableBeanFactory;

public class ConstructorResolver {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private final ConfigurableBeanFactory beanFactory;

	public ConstructorResolver(ConfigurableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public Object autowireConstructor(final BeanDefinition bd) {
		
		//记录筛选到的构造器
		Constructor<?> constructorToUse = null;		
		
		//该数组用于存放被BeanDefinitionCalueResolver处理过value数据
		//也就是经过处理过的用来创建对象用的参数
		Object[] argsToUse = null;
	
		Class<?> beanClass = null;
		try {
			beanClass = this.beanFactory.getBeanClassLoader().loadClass(bd.getBeanClassName());
		} catch (ClassNotFoundException e) {
			throw new BeanCreationException( bd.getID(), "Instantiation of bean failed, can't resolve class", e);
		}	

		//将ValueHolder中的RuntimeBeanReference转换成对象 和 将TypedStringValue进行类型转换
		BeanDefinitionValueResolver valueResolver =
				new BeanDefinitionValueResolver(this.beanFactory);
		//对应类似整型这些需要做类型转换
		SimpleTypeConverter typeConverter = new SimpleTypeConverter();
		
		//得到该类的构造器
		Constructor<?>[] candidates = beanClass.getConstructors();	
		//xml配置中的参数信息
		ConstructorArgument cargs = bd.getConstructorArgument();
		
		for(int i=0; i<candidates.length;i++){
			//第i个构造器的参数列表
			Class<?> [] parameterTypes = candidates[i].getParameterTypes();
			//1.xml配置中的参数个数是否 和 该类的该构造器参数个数 相等
			if(parameterTypes.length != cargs.getArgumentCount()){
				continue;
			}
			
			argsToUse = new Object[parameterTypes.length];
			
			//2.类型是否匹配
			boolean result = this.valuesMatchTypes(parameterTypes, 
					cargs.getArgumentValues(), 
					argsToUse, 
					valueResolver, 
					typeConverter);
			
			//找到合适的构造函数
			if(result){
				constructorToUse = candidates[i];
				break;
			}
			
		}
		
		//找不到一个合适的构造函数
		if(constructorToUse == null){
			throw new BeanCreationException( bd.getID(), "can't find a apporiate constructor");
		}
		
		//找到了调用对应的构造器进行实例化
		try {
			return constructorToUse.newInstance(argsToUse);
		} catch (Exception e) {
			throw new BeanCreationException( bd.getID(), "can't find a create instance using "+constructorToUse);
		}		
		
	}
	
	/**
	 * 
	 * @param parameterTypes 该构造器的参数列表
	 * @param valueHolders	xml中参数对应的value列表
	 * @param argsToUse		用来存放经过处理过的用来创建对象用的参数
	 * @param valueResolver	对value列表处理
	 * @param typeConverter	类型转化器
	 * @return
	 */
	private boolean valuesMatchTypes(Class<?> [] parameterTypes,
			List<ConstructorArgument.ValueHolder> valueHolders,
			Object[] argsToUse,
			BeanDefinitionValueResolver valueResolver,
			SimpleTypeConverter typeConverter ){
		
		
		for(int i=0;i<parameterTypes.length;i++){
			ConstructorArgument.ValueHolder valueHolder 
				= valueHolders.get(i);
			//获取参数的值，可能是TypedStringValue, 也可能是RuntimeBeanReference
			Object originalValue = valueHolder.getValue();
			
			try{
				//1.获得真正的值
				Object resolvedValue = valueResolver.resolveValueIfNecessary( originalValue);
				//如果参数类型是 int, 但是值是字符串,例如"3",还需要转型
				//2.如果转型失败，则抛出异常。说明这个构造函数不可用
				Object convertedValue = typeConverter.convertIfNecessary(resolvedValue, parameterTypes[i]);
				//转型成功，记录下来
				argsToUse[i] = convertedValue;
			}catch(Exception e){
				logger.error(e);
				return false;
			}				
		}
		return true;
	}
	

}
