package org.spring.beans.factory.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.spring.beans.BeanDefinition;
import org.spring.beans.ConstructorArgument;
import org.spring.beans.PropertyValue;
import org.spring.beans.factory.BeanDefinitionStoreException;
import org.spring.beans.factory.config.RuntimeBeanReference;
import org.spring.beans.factory.config.TypedStringValue;
import org.spring.beans.factory.support.BeanDefinitionRegistry;
import org.spring.beans.factory.support.GenericBeanDefinition;
import org.spring.core.io.Resource;
import org.spring.util.StringUtils;

//解析xml文件 把它变成数据结构
public class XmlBeanDefinitionReader {
	
	public static final String ID_ATTRIBUTE = "id";	

	public static final String CLASS_ATTRIBUTE = "class";
	
	public static final String SCOPE_ATTRIBUTE = "scope";

	public static final String PROPERTY_ELEMENT = "property";
	
	public static final String REF_ATTRIBUTE = "ref";
	
	public static final String VALUE_ATTRIBUTE = "value";
	
	public static final String NAME_ATTRIBUTE = "name";
	
	public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";
	
	public static final String TYPE_ATTRIBUTE = "type";
	
	BeanDefinitionRegistry registry;
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	public XmlBeanDefinitionReader(BeanDefinitionRegistry registry){
		this.registry = registry;
	}
	public void loadBeanDefinitions(Resource resource){
		InputStream is = null;
		try{			
			is = resource.getInputStream();
			SAXReader reader = new SAXReader();
			Document doc = reader.read(is);
			
			Element root = doc.getRootElement(); //<beans>
			Iterator<Element> iter = root.elementIterator();
			while(iter.hasNext()){
				Element ele = (Element)iter.next();
				String id = ele.attributeValue(ID_ATTRIBUTE);
				String beanClassName = ele.attributeValue(CLASS_ATTRIBUTE);
				BeanDefinition bd = new GenericBeanDefinition(id,beanClassName);
				if (ele.attribute(SCOPE_ATTRIBUTE)!=null) {					
					bd.setScope(ele.attributeValue(SCOPE_ATTRIBUTE));					
				}
				//解析<constructor-arg /> 标签
				parseConstructorArgElements(ele,bd);
				//解析<property name="xx"  value="x"/>标签
				parsePropertyElement(ele,bd); 
				this.registry.registerBeanDefinition(id, bd);
			}
		} catch (Exception e) {		
			throw new BeanDefinitionStoreException("IOException parsing XML document from " + resource.getDescription(),e);
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void parseConstructorArgElements(Element beanEle, BeanDefinition bd) {
		Iterator iter = beanEle.elementIterator(CONSTRUCTOR_ARG_ELEMENT);
		while(iter.hasNext()){
			Element ele = (Element)iter.next();
			parseConstructorArgElement(ele, bd);			
		}
		
	}
	public void parseConstructorArgElement(Element ele, BeanDefinition bd) {
		
		//type和name这两个属性我们这里用不到 要实现该功能的同学自行完成
		String typeAttr = ele.attributeValue(TYPE_ATTRIBUTE);
		String nameAttr = ele.attributeValue(NAME_ATTRIBUTE);
		
		//这里复用解析Property标签的方法 得到RuntimeBeanReference或者TypedStringValue
		Object value = parsePropertyValue(ele, bd, null);
		ConstructorArgument.ValueHolder valueHolder = new ConstructorArgument.ValueHolder(value);
		if (StringUtils.hasLength(typeAttr)) {
			valueHolder.setType(typeAttr);
		}	
		if (StringUtils.hasLength(nameAttr)) {
			valueHolder.setName(nameAttr);
		}
		
		bd.getConstructorArgument().addArgumentValue(valueHolder);		
	}

	public void parsePropertyElement(Element beanElem, BeanDefinition bd) {
		Iterator iter= beanElem.elementIterator(PROPERTY_ELEMENT);
		while(iter.hasNext()){
			Element propElem = (Element)iter.next();
			String propertyName = propElem.attributeValue(NAME_ATTRIBUTE);
			if (!StringUtils.hasLength(propertyName)) {
				logger.fatal("Tag 'property' must have a 'name' attribute");
				return;
			}
			
			Object val = parsePropertyValue(propElem, bd, propertyName);
			PropertyValue pv = new PropertyValue(propertyName, val);
			
			bd.getPropertyValues().add(pv);
		}
		
	}
	
	public Object parsePropertyValue(Element ele, BeanDefinition bd, String propertyName) {
		String elementName = (propertyName != null) ?
						"<property> element for property '" + propertyName + "'" :
						"<constructor-arg> element";

		
		boolean hasRefAttribute = (ele.attribute(REF_ATTRIBUTE)!=null);
		boolean hasValueAttribute = (ele.attribute(VALUE_ATTRIBUTE) !=null);
		
		if (hasRefAttribute) {
			String refName = ele.attributeValue(REF_ATTRIBUTE);
			if (!StringUtils.hasText(refName)) {
				logger.error(elementName + " contains empty 'ref' attribute");
			}
			RuntimeBeanReference ref = new RuntimeBeanReference(refName);			
			return ref;
		}else if (hasValueAttribute) {
			TypedStringValue valueHolder = new TypedStringValue(ele.attributeValue(VALUE_ATTRIBUTE));
			
			return valueHolder;
		}		
		else {
			
			throw new RuntimeException(elementName + " must specify a ref or value");
		}
	}
}

