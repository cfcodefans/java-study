package cf.study.java8.utils;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class DateTests {
	static Logger log = LogManager.getLogger(DateTests.class);

	@Test
	public void testFormat() throws ParseException {
		Date d = new Date();
		String formated = DateFormatUtils.format(d, "yyyy-MM-dd hh:mm:ss.SSS XXX");
		log.info(formated);
		Date parsed = DateUtils.parseDate(formated, "yyyy-MM-dd hh:mm:ss.SSS XXX");
		Assert.assertEquals(d, parsed);
	}
}
