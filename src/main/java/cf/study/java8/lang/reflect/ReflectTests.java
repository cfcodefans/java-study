package cf.study.java8.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.util.Assert;

import com.google.gson.internal.Streams;

import java.util.stream.*;

public class ReflectTests {

	public static class Sample<T> {
		public T genericField;
		public String stringField;
		
		class InstanceClz {}
		static class StaticClz{}
		
		public void method() {
			class MethodClz {};
		}
	}
	
	public static class StringSample extends Sample<String> {
		
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
	
	@Test
	public void testClzMeta() {
		Class<Sample[]> arrayClz = Sample[].class;
		System.out.println(arrayClz.getDeclaringClass());
		System.out.println(Sample.class.getDeclaringClass());
		System.out.println(Arrays.toString(Sample.class.getDeclaredClasses()));
		System.out.println(Arrays.toString(arrayClz.getDeclaredClasses()));
		System.out.println(arrayClz.getComponentType());
	}
	
	@Test
	public void testGeneric() throws Exception {
		Sample<String> ss = new Sample<String>();
		Class<? extends Sample> cls = ss.getClass();
		
//		System.out.println(cls.getName());
		System.out.println(Arrays.toString(cls.getTypeParameters()));
		
		TypeVariable<?> t = cls.getTypeParameters()[0];
		System.out.println(t.getGenericDeclaration());
		System.out.println(t.getTypeName());
		Field f = cls.getField("genericField");
		System.out.println(f);
		System.out.println(f.getGenericType());
		
//		System.out.println(Sample.class);
	}
	
	@Test
	public void testGenericSuper() throws Exception {
		StringSample _ss = new StringSample();
		Type _type = StringSample.class.getGenericSuperclass();
		System.out.println(_type);
		
		Assert.isInstanceOf(ParameterizedType.class, _type);
		
		ParameterizedType pt = (ParameterizedType)_type;
		Stream.of(pt.getActualTypeArguments()).forEach(System.out::println);
	}
}
