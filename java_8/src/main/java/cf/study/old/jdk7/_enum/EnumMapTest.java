package cf.study.jdk7._enum;

import java.util.EnumMap;

import org.apache.commons.lang.enums.EnumUtils;
import org.junit.Test;

public class EnumMapTest {
	enum Metal {
		iron, gold, copper, lead, aluminum, silver;
	}
	
	@Test
	public void testEnumMapTest() {
		EnumMap<Metal, String> m = new EnumMap<EnumMapTest.Metal, String>(Metal.class);
		System.out.println(m.size());
		
	}
	
	@Test
	public void enumMapTest() {
		Metal m = Metal.valueOf("iron");
		System.out.println(m);
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
