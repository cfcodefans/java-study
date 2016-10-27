package cf.study.java8.lang;

import org.junit.Test;

/**
 * Created by fan on 2016/10/25.
 */
public class ExceptionTests {
	static class TestException extends Exception {
		public TestException(String message, Throwable cause,
		                     boolean enableSuppression,
		                     boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

	}

	@Test
	public
}
