package cf.study.java8.utils.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import misc.MiscUtils;

import org.junit.Test;

public class CountDownTest {

	Random	rd	= new Random();

	@Test
	public void testCountDown() throws Exception {
		int count = 8;
		CountDownLatch counter = new CountDownLatch(count);

		if (counter.getCount() > 0) {
			System.out.println(String.format("counter.getCount(): %d", counter.getCount()));
		}

		Runnable task = () -> {
			int delay = (int) (1000 * rd.nextFloat());
			MiscUtils.easySleep(delay);
			System.out.println(String.format("%s slept %d ms", Thread.currentThread(), delay));
			counter.countDown();
		};
		ExecutorService tp = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		IntStream.range(0, count).forEach(i -> tp.submit(task));

		counter.await();
		System.out.println("finish");
	}
}
