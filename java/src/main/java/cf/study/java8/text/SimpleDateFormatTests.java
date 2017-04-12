package cf.study.java8.text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDateFormatTests {

	@Test
	public void testPatterns() {
		Date now = new Date();
		System.out.println(new SimpleDateFormat("S").format(now));
		log.info(now.toString());
	}
	
	@Test
	public void testYear() throws Exception {
		SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = s1.parse("2014-01-01");
		System.out.println(d1.getTime());
		
		SimpleDateFormat s2 = new SimpleDateFormat("YYYY-MM-dd");
		Date d2 = s1.parse("2014-01-01");		
		System.out.println(d2.getTime());
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, Calendar.DECEMBER, 29);
		System.out.println(DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd"));
		System.out.println(s2.format(calendar.getTime()));
	}

	private static final Logger log = LoggerFactory.getLogger(SimpleDateFormatTests.class);

	@Override
	public String toString() {
		return "SimpleDateFormatTests []";
	}
	
}
