package cf.study.java8.text;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class SimpleDateFormatTests {

	@Test
	public void testPatterns() {
		Date now = new Date();
		
		System.out.println(new SimpleDateFormat("S").format(now));
	}
}
