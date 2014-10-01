package cf.study.java8.lang.number;

import org.junit.Test;

public class NumberTest {

	@Test
	public void testMod() {
		System.out.println(-3 % 2); //-1
		System.out.println(3 % -2); //1
	}
	
	@Test
	public void testMax() {
		System.out.println(Integer.MAX_VALUE);
		System.out.println(-Integer.MAX_VALUE);
		System.out.println(Integer.MAX_VALUE + (-Integer.MAX_VALUE));
		
		System.out.println(Long.MAX_VALUE);
		System.out.println(-Long.MAX_VALUE);
		System.out.println(Long.MAX_VALUE + (-Long.MAX_VALUE));
	}
	
	
	@Test
	public void testMaxAndMin() {
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.MIN_VALUE);
		System.out.println(Integer.MAX_VALUE + Integer.MIN_VALUE);
		
		System.out.println(Long.MAX_VALUE);
		System.out.println(Long.MIN_VALUE);
		System.out.println(Long.MAX_VALUE + Long.MIN_VALUE);
	}
}
