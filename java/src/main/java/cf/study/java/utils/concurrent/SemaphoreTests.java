package cf.study.java.utils.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import misc.MiscUtils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemaphoreTests {

	private static final Logger log = LoggerFactory.getLogger(SemaphoreTests.class);
	
	ExecutorService es = Executors.newCachedThreadPool(MiscUtils.namedThreadFactory("something"));
	
	@Test
	public void testSamphore() throws Exception {
		final Semaphore s = new Semaphore(3);
		
		Runnable something = ()-> {
			log.info(":\tcan I enter?");
			
			try {
				while (!s.tryAcquire(1, TimeUnit.SECONDS)) {
					log.info(":\tstill need to wait");
				}
			} catch (Exception e) {
				log.error("", e);
			}
				
			log.info(":\tI am in! will stay here for 3 seconds");
			MiscUtils.easySleep(3000);
			log.info(":\tI am out of here");
			
			s.release();
		};
		
		IntStream.range(0, 4).forEach(i -> es.submit(something));
		
		es.shutdown();
		while (!es.awaitTermination(1, TimeUnit.SECONDS));
		log.info("finished");
	}
}
