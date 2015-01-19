package cf.study.java8.lang;

import java.lang.reflect.Array;

import junit.framework.Assert;

import org.junit.Test;

public class ArrayTests {

	@Test
	public void testArrayType() {
		int[] intArray = new int[0];
		
//		Assert.assertTrue((intArray instanceof Array));
		System.out.println(intArray.getClass());
		System.out.println(intArray.getClass().isArray());
//		Assert.assertTrue(Array.class.isInstance(intArray));
		
	}
}
