package cf.study.java8.utils.concurrent.executors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import misc.MiscUtils;

import org.junit.Test;

public class ExecutorTests {

	private static final Logger log = Logger.getLogger(ExecutorTests.class.getSimpleName());

	@Test
	public void testSingleThreadExecutor() {
		ExecutorService es = Executors.newSingleThreadExecutor();
		es.submit(() -> {
			while (!Thread.interrupted()) {
				log.info("running");
			}
		});

		join();
	}

	public static void join() {
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Test
	public void callExample() {
		Callable<Date> something = () -> {
			System.out.println("going to do something");
			MiscUtils.easySleep(5000);
			System.out.println("before I return the time");
			return new Date();
		};

		ExecutorService es = Executors.newCachedThreadPool();
		List<Future<Date>> results = new ArrayList<Future<Date>>();
		for (int i = 0; i < 10; i++) {
			Future<Date> result = es.submit(something);
			results.add(result);
		}

		for (Future<?> result : results)
			try {
				System.out.println(result.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
