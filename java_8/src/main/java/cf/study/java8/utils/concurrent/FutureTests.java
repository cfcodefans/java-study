package cf.study.java8.utils.concurrent;

import misc.MiscUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureTests {
	public static final Logger log = LogManager.getLogger(FutureTests.class);

	@Test
	public void testSimpleFuture() throws Exception {
		Future<Long> nextSecond = Executors.newSingleThreadExecutor().submit(() -> {
			long t = System.currentTimeMillis();
			for (; 1000 - (t % 1000) > 1; t = System.currentTimeMillis()) {
				Thread.sleep(1);
			}
			return t;
		});

		log.info(nextSecond.get());
	}

	@Test
	public void testCancel() throws Exception {
		Callable<Integer> callable = () -> {
			log.info(Thread.currentThread() + "\t" + Thread.currentThread().isInterrupted());
			int result = 0;
			try {
				for (int i = 0; i < 10; i++, result++) {
//					MiscUtils.interrupted();
					log.info("{}", i);
					Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				log.info(Thread.currentThread() + "\t" + Thread.currentThread().isInterrupted());
				log.error("got interrupted!", e);
			}
			return result;
		};
		ExecutorService thread = Executors.newSingleThreadExecutor();
		Future<Integer> future = thread.submit(callable);

		MiscUtils.easySleep(300);
		future.cancel(true);
		log.info(future.isCancelled());
//		log.info(future.get());

		future = thread.submit(callable);
		log.info(future.get());
	}
}
