package cf.study.java8.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
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
