package cf.study.java8.lang;


import java.util.Date;
import java.util.Random;

import misc.MiscUtils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

public class ThreadTests {

	private static final Logger log = Logger.getLogger(ThreadTests.class);

	@Test
	public void testDaemon() throws Exception {
		log.info("starting Daemon thread");

		Thread thread = new Thread(() -> {
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

		Thread thread = new Thread(() -> {
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

	@Test
	public void testInterrupt() {
		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					for (int i = 10; i > 0; i--) {
						Thread.currentThread().sleep(2000);
						System.out.println(i + "\t" + Thread.interrupted());
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					System.out.println(MiscUtils.invocationInfo());
				}
				System.out.println(MiscUtils.invocationInfo() + " end");
			}
		});
		th.setDaemon(true);
		th.start();

		try {
			Thread.currentThread().sleep(4000);
			th.interrupt();
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	static class Base {}
	
	@Test
	public void testFinalizerInThread() {
		// final Base b = new Base();
		Thread th = new Thread(new Runnable() {
			public void run() {
				Base b = new Base();
				// b = null;
				System.out.println(MiscUtils.invocationInfo());
			}
		});
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.gc();
		System.out.println(MiscUtils.invocationInfo());
	}

	@Test
	public void testSleep() {
		for (;;) {
			try {
				Thread.currentThread().sleep(new Random().nextInt() % (new Random().nextInt() % 3000));
				final String randomAlphabetic = RandomStringUtils.randomAlphabetic(new Random().nextInt() % 80);

				System.out.println(String.format("%s %s %s\t %s", new Date(), Thread.currentThread().getName(), MiscUtils.invocationInfo(), randomAlphabetic));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
