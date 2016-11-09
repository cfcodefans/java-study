package cf.study.java8.time.format;

import org.junit.Test;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

import static java.time.temporal.ChronoField.*;

/**
 * Created by fan on 2016/11/2.
 */
public class DateTimeFormatterTests {
	@Test
	public void testParser() {
		DateTimeFormatter dtf1 = new DateTimeFormatterBuilder().appendValue(HOUR_OF_AMPM, 2)
			.appendLiteral(':')
			.appendValue(MINUTE_OF_HOUR, 2)
			.appendLiteral(':')
			.appendValue(SECOND_OF_MINUTE, 2)
//			.appendLiteral(' ')
			.appendValue(AMPM_OF_DAY, 2).toFormatter();

		DateTimeFormatter dtf2 = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").optionalStart().toFormatter();

		TemporalAccessor parsed = dtf2.parse("07:05:45");
		System.out.println(dtf1.format(parsed));
	}
}
