package cf.study.java8.lang;


import misc.MiscUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ThreadTests {

	private static final Logger log = LoggerFactory.getLogger(ThreadTests.class);

	public static boolean interrupted(String msg) throws InterruptedException {
		if (Thread.interrupted()) {
			throw new InterruptedException(msg);
		}
		return false;
	}

	@Test
	public void testIsInterrupted() {
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			for (int i = 0; i < 500000; i++) {
				interrupted("test");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sw.stop();
		log.info("{}", sw.getTime());
	}

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

//		thread.setDaemon(true);
		thread.start();
		Thread.sleep(10);
		thread.join();
		System.out.println("exit");
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
						Thread.sleep(2000);
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
			Thread.sleep(4000);
			th.interrupt();
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInterruptBlocked() {
		Object obj = new Object();
		Thread holder = new Thread(() -> {
			try {
				Thread.sleep(1000);
				log.info("holder ready");
				synchronized (obj) {
					Thread.sleep(2000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Thread taker = new Thread(() -> {
			try {
				Thread.sleep(1000);
				log.info("taker ready");
				synchronized (obj) {
					log.info("get the obj " + Thread.interrupted());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		holder.start();
		taker.start();

		MiscUtils.easySleep(1500);
		log.info("taker stop!");

		taker.interrupt();

		MiscUtils.easySleep(1500);
	}

	@Test
	public void testNotInterrupte() {
		Thread th = new Thread(() -> {
			for (int i = 10; i > 0; i--) {
				try {
					Thread.sleep(2000);
					System.out.println(i + "\t" + Thread.interrupted());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		th.setDaemon(true);
		th.start();
		try {
			Thread.sleep(4000);
			th.interrupt();
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static class Base {
	}

	@Test
	public void testFinalizerInThread() {
		// final Base b = new Base();
		Thread th = new Thread(new Runnable() {
			@SuppressWarnings("unused")
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
		for (; ; ) {
			try {
				Thread.sleep(new Random().nextInt() % (new Random().nextInt() % 3000));
				final String randomAlphabetic = RandomStringUtils.randomAlphabetic(new Random().nextInt() % 80);

				System.out.println(String.format("%s %s %s\t %s", new Date(), Thread.currentThread().getName(), MiscUtils.invocationInfo(), randomAlphabetic));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void sleepExample() {
		Thread thread = new Thread(() -> {
			System.out.println("running");
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("slept 5 s");
		});

		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void waitExample() {
		final Object object = new Object();
		final Object object1 = new Object();
		Runnable access = () -> {
			String threadName = Thread.currentThread().getName();
			try {
				System.out.println(threadName + " I am going to get object");
				synchronized (object) {
					synchronized (object1) {
						//do something else
					}
					System.out.println(threadName + " I got object, then I sleep");
					Thread.sleep(3000);
					System.out.println(threadName + " I don't hold the object, then I wait");
					object.wait();
					System.out.println(threadName + " someone notified me");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		Runnable notifier = () -> {
			String threadName = Thread.currentThread().getName();
			try {
				Thread.sleep(100);
				System.out.println(threadName + " need the object");
				synchronized (object) {
					System.out.println("wake up!");
					object.notify();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		Thread thread1 = new Thread(access);
		Thread thread2 = new Thread(notifier);
		thread1.start();
		thread2.start();

		try {
			thread1.join();
			thread2.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void interruptSample() throws Exception {
		Runnable something = () -> {
			String threadName = Thread.currentThread().getName();
			for (int i = 0; i < 5; i++) {
				try {
					///
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				System.out.println(threadName + "/t" + Calendar.getInstance().getTime());
			}
		};

		Thread thread = new Thread(something);
		thread.start();
		thread.join(3000);
		thread.interrupt();
		thread.join();
	}
}
