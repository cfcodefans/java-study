package cf.study.java8.lang;

import org.junit.Test;

public class GenericTests {
	public static class Holder<T> {
		T t;
	}
	
	@Test
	public void testTypeParam() {
		Holder<Object> strHolder = new Holder<Object>();
		System.out.println(strHolder.getClass().getGenericSuperclass()); 
	}
}
