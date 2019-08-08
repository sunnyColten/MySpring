package org.spring.test.v2;

import org.junit.Assert;
import org.junit.Test;
import org.spring.beans.propertyeditors.CustomNumberEditor;

public class CustomNumberEditorTest {

	//测试自定义的Editor是否正确
	@Test
	public void testConvertString() {
		//测试传入的数据转成Integer类型  true表示允许为空数据
		CustomNumberEditor editor = new CustomNumberEditor(Integer.class,true);
		
		editor.setAsText("3");
		Object value = editor.getValue();
		Assert.assertTrue(value instanceof Integer);		
		Assert.assertEquals(3, ((Integer)editor.getValue()).intValue());
		
		//传入空值
		editor.setAsText("");
		Assert.assertTrue(editor.getValue() == null);
		
		try{
			//试图传入一个非法的值
			editor.setAsText("3.1");
			
		}catch(IllegalArgumentException e){
			return ;
		}
		Assert.fail();		
		
	}

}
