package cf.study.utils;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class MiscUtils {
	public static String invocationInfo() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		int i = 2;
		return String.format("%s\t%s.%s", ste[i].getFileName(), ste[i].getClassName(), ste[i].getMethodName());
	}

	public static String invocationInfo(final int i) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return String.format("%s\t%s.%s", ste[i].getFileName(), ste[i].getClassName(), ste[i].getMethodName());
	}

	@Test
	public void testPref() {
		long t = System.nanoTime();

		for (int i = 0; i < 100; i++) {
			invocationInfo();
		}

		System.out.println(System.nanoTime() - t);

		t = System.nanoTime();

		for (int i = 0; i < 100; i++) {
			invocationInfo();
			invocationInfo();
		}

		System.out.println(System.nanoTime() - t);
	}

}
