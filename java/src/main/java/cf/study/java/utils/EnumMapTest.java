package cf.study.java.utils;

import java.util.EnumMap;

import org.apache.commons.lang3.EnumUtils;
import org.junit.Assert;
import org.junit.Test;

public class EnumMapTest {
	enum Metal {
		iron, gold, copper, lead, aluminum, silver;
	}
	
	@Test
	public void testEnumMapTest() {
		EnumMap<Metal, String> m = new EnumMap<EnumMapTest.Metal, String>(Metal.class);
		System.out.println(m);
		Assert.assertEquals(m.size(), Metal.values().length);
	}
	
	@Test
	public void enumMapTest() {
		Metal m = Metal.valueOf("iron");
		EnumMap<Metal, String> _m = new EnumMap<EnumMapTest.Metal, String>(Metal.class);
		System.out.println(_m.get(m));
	}
	
	@Test
	public void enumUtilsTest() {
		System.out.println(EnumUtils.getEnumList(Metal.class));
	}
	
	@Test
	public void enumTest() {
		System.out.println(Metal.valueOf("yellow"));
	}
}
