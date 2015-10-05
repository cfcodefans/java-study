package cf.study.jdk7.lang;

import java.lang.reflect.Method;



import org.junit.Assert;
import org.junit.Test;

public class GenericTests {
	public static class Holder<T> {
		T t;
		public <E> E to(Class<E> e) {
			return (e.isInstance(t)) ? e.cast(t) : null;
		}
	}
	
	@Test
	public void testTypeParam() throws Exception {
		Holder<Object> strHolder = new Holder<Object>();
		final Class<? extends Holder> holderClass = strHolder.getClass();
		System.out.println(holderClass.getGenericSuperclass()); 
		
		final Method method = holderClass.getMethod("to", Class.class);
		Assert.assertNotNull(method);
		
		System.out.println(method.getGenericReturnType());
	}
}
