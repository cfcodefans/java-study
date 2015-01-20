package cf.study.java8.text;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Test;

public class SimpleDateFormatTests {

	@Test
	public void testPatterns() {
		Date now = new Date();
		
		System.out.println(new SimpleDateFormat("S").format(now));
		
		log.info(now);
	}

	private static final Logger log = Logger.getLogger(SimpleDateFormatTests.class);

	@Override
	public String toString() {
		return "SimpleDateFormatTests []";
	}
	
}
