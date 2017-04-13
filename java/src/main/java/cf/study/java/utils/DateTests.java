package cf.study.java.utils;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.regex.Pattern;

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
	@Test
	public void testFormatWithTimezone() throws ParseException {
		Date d = DateUtils.parseDate("2016-07-13T05:04:35+02:00", "yyyy-MM-dd'T'HH:mm:ss+HH:mm");
		log.info(d);
		TemporalAccessor parsed = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse("2016-07-13T05:04:35-02:00");
		log.info(parsed);
		log.info(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

	@Test
	public void testPattern() throws Exception {
		String patternStr = DateFormatUtils.format(new Date(), "yyyy\\-MM\\-dd");
		log.info(patternStr);
		Pattern p = Pattern.compile(patternStr);
		String dateStr = DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss+HH:mm");
		log.info(dateStr);
		log.info(p.matcher(dateStr).find());
	}
}
