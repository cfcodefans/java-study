package cf.study.java8.lang.reflect;

import java.util.stream.Stream;

import org.junit.Test;

public class ReflectTests {

	public static class Sample<T> {
		public T genericField;
		public String stringField;
	}

	@Test
	public void testFields() {
		Stream.of(Sample.class.getDeclaredFields()).forEach((fd) -> {
//			System.out.println(ToStringBuilder.reflectionToString(fd, ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(fd.getDeclaringClass());
			System.out.println(fd.getGenericType().getClass());
		});
		
		Stream.of(Sample.class.getDeclaredMethods()).forEach((md) -> {
//			System.out.println(ToStringBuilder.reflectionToString(fd, ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(md.getDeclaringClass());
			System.out.println(md.getName());
		});
	}
}
