package cf.study.java8.lang;

import java.util.Arrays;

import org.junit.Test;

public class SystemTests {

	@Test
	public void testArrayCopy() {
		int[] ia = { 0xff00ff };
		
		int[] _ia = { 0xff00ff };
		System.arraycopy(ia, 0, _ia, 0, 1);
		System.out.println(Arrays.toString(_ia));

		
		byte[] ba = new byte[4];
		
		System.arraycopy(ia, 0, ba, 0, 1);
		System.out.println(Arrays.toString(ba));
	}
	
	@Test
	public void testEnv() {
		System.getenv().forEach((k, v)->System.out.println(k + " = \t" + v));
	}
	
	@Test
	public void testProperties() {	
		System.getProperties().forEach((k, v)->System.out.println(k + " = \t" + v));
	}
	
}
