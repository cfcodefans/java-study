package cf.study.java8.lang;

import org.junit.Test;

public class ArrayTests {

	@Test
	public void testArrayType() {
		int[] intArray = new int[0];
		
//		Assert.assertTrue((intArray instanceof Array));
		System.out.println(intArray.getClass());
		System.out.println(intArray.getClass().isArray());
//		Assert.assertTrue(Array.class.isInstance(intArray));
		
		System.out.println(intArray instanceof int[]);
		
		System.out.println(new String[0] instanceof Object[]);
		
		System.out.println(new Long[0] instanceof Number[]);
	}
}
