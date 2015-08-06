package cf.study.java8.utils.concurrent;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

public class CountDownTest {
	@Test
	public void testCountDown() {
		CountDownLatch counter = new CountDownLatch(2);
		
		if (counter.getCount() > 0) {
			System.out.println(String.format("counter.getCount(): %d", counter.getCount()));
		}
	}
}
