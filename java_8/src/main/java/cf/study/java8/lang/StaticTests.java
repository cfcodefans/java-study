package cf.study.java8.lang;

import org.junit.Test;

class Super {
	public static String str = "Super";
	
	public static void foo() {
		System.out.println("Super.foo");
	}
}

class Sub extends Super {
	public static int num;
	
	public static void bar() {
		System.out.println("Sub.bar");
	}
}

class StringUtils extends org.apache.commons.lang3.StringUtils {
	
}

public class StaticTests {
	@Test
	public void testStaticMethod() {
		Sub.foo();
		System.out.println(StringUtils.repeat('a', 20));
	}
	
	@Test
	public void testStaticVar() {
		System.out.println(Sub.str);
	}
	
}
