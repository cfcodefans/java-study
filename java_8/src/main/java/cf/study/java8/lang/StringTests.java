package cf.study.java8.lang;

import java.nio.charset.Charset;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

public class StringTests {

	@Test
	public void getAllCharsets() {
		System.out.println(Charset.availableCharsets());
	}
	
	@Test
	public void testEncoding() throws Exception {
		String str = "abc";
		System.out.println(new String(str.getBytes(), "UTF-8"));
	}
	
	@Test
	public void testUnicode() throws Exception {
		System.out.println((char)65);
		System.out.println('\101'); // 8 * 8 + 1
		System.out.println('\u0041'); //16 * 4 + 1
	}
	
	@Test
	public void testFormat() throws Exception {
		System.out.println(String.format("%02d", 4));
		System.out.println(String.format("%02d", 14));
		System.out.println(String.format("%02d", 124));
		
		System.out.println(DateUtils.parseDate("2015-06-27 00:00:00", "yyyy-MM-dd hh:mm:ss"));
	}
}
