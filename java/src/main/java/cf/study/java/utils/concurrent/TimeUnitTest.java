package cf.study.java.utils.concurrent;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimeUnitTest {
	
	@Test
	public void convert() {
		final long now = System.currentTimeMillis();
		System.out.println(now);
		System.out.println(TimeUnit.SECONDS.convert(now, TimeUnit.MILLISECONDS));
	}
}
