package cf.study.java8.utils.concurrent.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import misc.MiscUtils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockTests {

	private static final Logger	log	= LoggerFactory.getLogger(LockTests.class);
	ExecutorService				es	= Executors.newFixedThreadPool(2, MiscUtils.namedThreadFactory("something"));

	@Test
	public void testLock1() throws Exception {
		Lock lock = new ReentrantLock();

		Runnable something = () -> {
			try {
				lock.lock();
				log.info("I got the lock, will lock it for 3 seconds");
				
				IntStream.range(0, 4).forEach(i -> {
					MiscUtils.easySleep(1000);
					log.info("doing something");
				});
				
				log.info("done, unlock");
			} catch (Exception e) {
				log.error("", e);
			} finally {
				lock.unlock();
			}
		};

		IntStream.range(0, 4).forEach(i -> es.submit(something));
		es.shutdown();
		while (!es.awaitTermination(1, TimeUnit.SECONDS));
		log.info("finished");
	}
}
