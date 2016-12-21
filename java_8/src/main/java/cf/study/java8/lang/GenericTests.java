package cf.study.java8.lang;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class GenericTests {
	public static class Holder<T> {
		T t;
	}

	public static class StringHolder extends Holder<String> {
		
	}
	
	
	@Test
	public void testSuperTypeParam() {
		StringHolder sh = new StringHolder();
		Type gs = sh.getClass().getGenericSuperclass();
		System.out.println(gs);
		System.out.println(gs instanceof ParameterizedType);
		ParameterizedType pt = (ParameterizedType) gs;
		System.out.println(pt.getActualTypeArguments()[0]);
	}
	
	@Test
	public void testTypeParam() {
		Holder<String> strHolder = new Holder<String>();
		Class<? extends Holder> clz = strHolder.getClass();
		TypeVariable<?>[] tps = clz.getTypeParameters();
		System.out.println(Arrays.toString(tps));
		TypeVariable<?> tv = tps[0];
		
		System.out.println(ToStringBuilder.reflectionToString(tv));
		System.out.println( tv.getBounds()[0].getTypeName());
		
		System.out.println();
		Type genericSuperclass = clz.getGenericSuperclass();
		System.out.println(genericSuperclass);
	}
	
	public static class Cmp implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
	}
	
	@Test
	public void testTypeParam1() {
		Cmp cmp = new Cmp();
		Class<? extends Cmp> clz = cmp.getClass();
		TypeVariable<?>[] tps = clz.getTypeParameters();
		System.out.println(Arrays.toString(tps));
		System.out.println(Arrays.toString(clz.getGenericInterfaces()));
		Type t = clz.getGenericInterfaces()[0];
		ParameterizedType pt = (ParameterizedType) t;
		System.out.println(t.toString());
		System.out.println(Arrays.toString(pt.getActualTypeArguments()));

		System.out.println(ToStringBuilder.reflectionToString(pt));
	}

	@SuppressWarnings("null")
	@Test
	public void loop() {
		StopWatch sw = new StopWatch();
		sw.start();
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0, j = list.size(); i < j; i++) {
			System.out.println(list.get(i));
		}

		String str = "123";
		Integer intVal = Integer.valueOf(str);
		for (Integer i : list) {
			// list.remve
			if (i.equals(intVal)) {
				// ..
			}
		}
		sw.stop();

		System.out.println(sw.getNanoTime());

		Object a = null;
		Object b = "s";
		a.equals(b);

		a.equals(CONST);
		CONST.equals(a);
		"a".equals(a);
	}

	static final String CONST = "";

	@Test
	public void testInstanceof() {
		System.out.println((null instanceof Integer));
	}

	@SuppressWarnings("rawtypes")
	public List query(String queryStr) {
		List result = Collections.emptyList();
		if (StringUtils.isBlank(queryStr)) {
			return result;
		}
		// ......

		// ....

		return result;
	}

	public static class User {
	}

	public User byId(int id) {
		return new User();
	}

	public List<User> byIds(int... ids) {
		List<User> list = new ArrayList<User>();

		for (int id : ids) {
			list.add(byId(id));
		}
		return list;
	}
}
