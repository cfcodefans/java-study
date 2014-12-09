package cf.study.java8.lang;

import org.junit.Assert;
import org.junit.Test;

public class OperTests {
	@Test
	public void unary() {
		int i = 5;
		i = +5;
		Assert.assertEquals(i, 5);
		
		i = +i;
		Assert.assertEquals(i, 5);
		
		i = -i;
		Assert.assertNotEquals(i, 5);
	}
	
	@Test
	public void arithmetic() {
//		int i = 1;
//		System.out.println(i + i);
//		System.out.println(i * i);
//		System.out.println(i - i);
//		System.out.println(i / i);
//		
//		System.out.println(i * (i + i));
//		System.out.println(i / (i + i));
//		System.out.println(i + (i + i));
//		System.out.println(i - (i + i));
//		
//		System.out.println(i - +i);
//		System.out.println(i + -i);
//		System.out.println(i + ++ i);
//		System.out.println(i + +(+ i));
		
		int a = 0;
		System.out.println(+a);
		System.out.println(++a);
		System.out.println(+ ++a);
	}
}
