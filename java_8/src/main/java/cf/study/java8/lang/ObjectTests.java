package cf.study.java8.lang;

import org.junit.Test;

public class ObjectTests {
	@Test 
	public void testObject() {
		Object obj = new Object();
		
		System.out.println(obj);
		System.out.println(obj.hashCode());
		System.out.println(obj.toString());
		System.out.println(obj.getClass());
		System.out.println(obj.hashCode());
	}
}
