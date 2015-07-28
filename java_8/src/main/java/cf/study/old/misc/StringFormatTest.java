package cf.study.misc;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringFormatTest {
	private static final Logger LOG = LoggerFactory.getLogger(StringFormatTest.class);

	@Test
	public void testStringFormat() {
		System.out.println(String.format("1: %2s, 2: %1s", "2", "1"));
		System.out.println(String.format("1: %2$s, 2: %1$s", "2", "1"));
		
		LOG.info("1: {}, 2: {}", "b", "a");
		System.out.println(String.format("1: {}, 2: {}", "b", "a"));
		
	}
}
