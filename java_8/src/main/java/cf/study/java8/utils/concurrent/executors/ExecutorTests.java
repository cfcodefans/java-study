package cf.study.java8.utils.concurrent.executors;

import misc.MiscUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorTests {

	private static final Logger log = LogManager.getLogger(ExecutorTests.class);

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
			log.error("", e);
		}
	}

	@Test
	public void callExample() {
		Callable<Date> something = () -> {
			log.info("going to do something");
			MiscUtils.easySleep(5000);
			log.info("before I return the time");
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
				log.info(result.get());
			} catch (Exception e) {
				log.error("", e);
			}
	}
}
