package cf.study.java8.lang;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ThreadTests {

	private static final Logger log = Logger.getLogger(ThreadTests.class);

	@Test
	public void testDaemon() throws Exception {
		log.info("starting Daemon thread");

		Thread thread = new Thread(()->{
			log.info("thread started");
			try {
				Thread.sleep(1000);
				log.info("sleep 1");
				Thread.sleep(1000);
				log.info("sleep 2");
				Thread.sleep(1000);
				log.info("sleep 3");
			} catch (Exception e) {
				log.error("?", e);
			}
			
			log.info("thread stopped");
		});
		
		thread.setDaemon(true);
		thread.start();
	}
	
	@Test
	public void testDaemonInfiniteLoop() throws Exception {
		log.info("starting Daemon thread");

		Thread thread = new Thread(() -> {
			log.info("thread started");

			try {
				while (true) {
					Thread.sleep(1000);
					log.info("sleep 1");
					Thread.sleep(1000);
					log.info("sleep 2");
					Thread.sleep(1000);
					log.info("sleep 3");
				}
			} catch (Exception e) {
				log.error("?", e);
			}

			log.info("thread stopped");
		});
		
		thread.setDaemon(true);
		thread.start();
	}
	
	
	@Test
	public void testJoin() throws Exception {
		log.info("starting Daemon thread");

		Thread thread = new Thread(()->{
			log.info("thread started");
			try {
				Thread.sleep(1000);
				log.info("sleep 1");
				Thread.sleep(1000);
				log.info("sleep 2");
				Thread.sleep(1000);
				log.info("sleep 3");
			} catch (Exception e) {
				log.error("?", e);
			}
			
			log.info("thread stopped");
		});
		
		thread.setDaemon(true);
		thread.start();
		
		Thread.currentThread().join();
	}
	
}
