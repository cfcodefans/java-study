package cf.study.jdk7.lang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

public class ReflectionTest {
	
	public Long foo() throws RuntimeException {
		long ctm = System.currentTimeMillis();
		if (ctm % 2 == 0) 
			throw new RuntimeException("time is even");
		
		return ctm;
	}
	
	@Test
	public void testMethodInvocation() throws Exception {
		ReflectionTest rt = new ReflectionTest();
		Method md = rt.getClass().getMethod("foo");
		try {
			System.out.println(md.invoke(rt));
		} catch(InvocationTargetException e) {
			System.out.println(e.getCause().getMessage());
		}
	}
	
	@Test
	public void testClassName() {
		System.out.println(String.class.getCanonicalName());
	}
	
	public static void main(String[] args) {
		
	}
}
