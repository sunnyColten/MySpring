package org.spring.test.v2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.spring.beans.BeanDefinition;
import org.spring.beans.PropertyValue;
import org.spring.beans.factory.config.RuntimeBeanReference;
import org.spring.beans.factory.support.DefaultBeanFactory;
import org.spring.beans.factory.xml.XmlBeanDefinitionReader;
import org.spring.core.io.ClassPathResource;

public class BeanDefinitionTestV2 {

	//测试对BeanDefinition、PropertyValue、RuntimeBeanReference、RuntimeBeanReference的关系设计是否正确
	@Test
	public void testGetBeanDefinition() {
		
		DefaultBeanFactory factory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		reader.loadBeanDefinitions(new ClassPathResource("petstore-v2.xml"));
		BeanDefinition bd = factory.getBeanDefinition("petStore");
		//测试1:测试
		List<PropertyValue> pvs = bd.getPropertyValues();
		Assert.assertTrue(pvs.size() == 4);
		
		//测试2
		{
			PropertyValue pv = this.getPropertyValue("accountDao", pvs);
			
			Assert.assertNotNull(pv);
			
			Assert.assertTrue(pv.getValue() instanceof RuntimeBeanReference);
		}
		
		{
			PropertyValue pv = this.getPropertyValue("itemDao", pvs);
			
			Assert.assertNotNull(pv);
			
			Assert.assertTrue(pv.getValue() instanceof RuntimeBeanReference);
		}
		
	}
	
	private PropertyValue getPropertyValue(String name,List<PropertyValue> pvs){
		for(PropertyValue pv : pvs){
			if(pv.getName().equals(name)){
				return pv;
			}
		}
		return null;
	}

}

