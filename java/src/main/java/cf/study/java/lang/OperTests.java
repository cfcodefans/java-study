package cf.study.java.lang;

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
		Assert.assertNotSame(i, 5);
	}
	
	@Test
	public void testComparisons() {
//		Assert.assertTrue(true > false);
	}
	
	@Test
	public void arithmetic() {
		int i = 1;
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
		System.out.println(i - +i);
		System.out.println(i + -i);
		System.out.println(i + ++ i);
		System.out.println(i + +(+ i));
		
		int a = 0;
		System.out.println(+a);
		System.out.println(++a);
		System.out.println(+ ++a);
	}
	
	@Test
	public void mod() {
		System.out.println(6 % 4);//2
		System.out.println(-6 % 4);//-2
		System.out.println(6 % -4);//2
		System.out.println(-6 % -4);//-2
		System.out.println(0.6 % 0.4);//0.19999999999999996
		System.out.println(6 % 0.4);//0.3999999999999997
		System.out.println(0.4 % 0.6);//0.4
	}
	
}
