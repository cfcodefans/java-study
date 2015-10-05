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
	
	public static void foo() {
		bar();
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
	
	
	@Test
	public void testStaticMethodInheritance() {
		Sub s = new Sub();
		s.foo();
		Super _s = (Super)s;
		_s.foo();
	}
}
