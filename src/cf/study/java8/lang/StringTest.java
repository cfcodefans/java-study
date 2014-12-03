package cf.study.java8.lang;

import java.nio.charset.Charset;

import org.junit.Test;

public class StringTest {

	@Test
	public void getAllCharsets() {
		System.out.println(Charset.availableCharsets());
	}
	
	@Test
	public void testEncoding() throws Exception {
		String str = "abc";
		System.out.println(new String(str.getBytes(), "UTF-8"));
	}
}
