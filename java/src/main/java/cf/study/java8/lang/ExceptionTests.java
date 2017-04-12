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

	static void trigger() throws Exception {
		throw new Exception("trigger");
	}

	static void wrap() throws Exception {
		try {
			trigger();
		} catch (Exception e) {
			throw new Exception("wrapper", e);
		}
	}

	static void callSuppression(boolean enableSuppression) throws TestException {
		try {
			doubleTriggers();
		} catch (Exception e) {
			TestException te = new TestException("suppression: " + enableSuppression, e, enableSuppression, true);
			te.addSuppressed(e);
			throw te;
		}
	}

	@Test
	public void testSuppression() {
		try {
			callSuppression(true);
		} catch (TestException te) {
			te.printStackTrace();
		}

		System.out.println();

		try {
			callSuppression(false);
		} catch (TestException te) {
			te.printStackTrace();
		}
	}

	static void doubleTriggers() throws Exception {
		try {
			throw new Exception("trigger");
		} finally {
			throw new Exception("trigger in finally");
		}
	}

	static class ProblematicClosable implements AutoCloseable {

		public void close() throws Exception {
			throw new Exception("Exception in close");
		}
	}
}
