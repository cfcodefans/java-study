package cf.study.java8.lang;

import java.nio.charset.Charset;

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
}
