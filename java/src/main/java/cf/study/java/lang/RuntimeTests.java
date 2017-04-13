package cf.study.java.lang;

import org.junit.Test;

public class RuntimeTests {

	@Test
	public void testEnv() {
		Runtime rt = Runtime.getRuntime();
		System.out.println(rt.availableProcessors());
		System.out.println(rt.freeMemory());
		System.out.println(rt.totalMemory());
		System.out.println(rt.maxMemory());
	}
}
