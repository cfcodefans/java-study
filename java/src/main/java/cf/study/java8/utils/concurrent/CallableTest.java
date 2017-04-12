package cf.study.java8.utils.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class CallableTest {
	public static final Logger log = LogManager.getLogger(CallableTest.class);

	private ExecutorService pool1 = Executors.newFixedThreadPool(1);
	private ExecutorService pool0 = Executors.newFixedThreadPool(6);
	private Semaphore requestSize = new Semaphore(15, true);
	
	private static class Callee implements Callable<Void> {
		private int d;

		public Callee(int d) {
			this.d = Math.abs(d);
		}

		public Callee() {
			this.d = 0;
		}

		@Override
		public Void call() throws Exception {
			log.info("I am started");
			try {
				for (int i = 0; i < d; i++) {
					log.info(String.format("[%s] I am called after delay of %d", Thread.currentThread(), i));
				}
				log.info("I am called in: " + Thread.currentThread());
			} catch (Exception e) {
				log.info("I am killed");
				log.error("", e);
			} finally {
				log.info("I am done");
			}
			return null;
		}
	}

	@Test
	public void testCancel() throws Exception {
		Future<Void> f = pool1.submit(new Callee(10));
		log.info("start to wait 5 secs");
		try {
			f.get(4, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			if (!(f.isCancelled() || f.isDone())) {
				log.info("you are cancelled");
				f.cancel(true);

				pool1.submit(new Runnable() {
					@Override
					public void run() {
						log.info("second task");
					}
				}).get();
			}
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	@Test
	public void testCallable() throws Exception {
		pool1.submit(new Callee()).get(30, TimeUnit.SECONDS);
	}

	@Test
	public void testDoubleCallable() throws Exception {
		for (long s = 0; s < 900000; s++)
			pool0.submit(new Runnable() {
				@Override
				public void run() {
					try {
						requestSize.acquire();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					log.info("I am going to call callee");
					try {
						pool1.submit(new Callee()).get(30, TimeUnit.SECONDS);
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						e.printStackTrace();
					}
					try {
						requestSize.release();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
	}

	@Test
	public void testCallableTimeout() throws Exception {
		for (long s = 0; s < 7; s++) {
			try {
				pool1.submit(new Callee(3)).get(1, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				log.info(e);
			} catch (Exception e) {
				log.info(e);
			}
		}
		
		log.info("finish submitting 7 tasks");
		
/*		for (long s = 0; s < 7; s++) {
			try {
				pool1.submit(new Callee(0)).get(4, TimeUnit.SECONDS);
			} catch (Exception e) {
				log.info(e);
			}
		}*/
	}
	
	public static class HelloRunnable implements Runnable {
		public void run() {
			log.info("Hello from a thread!");
		}
	}
	
	@Test
	public void testHelloRunnable() {
		(new Thread(new HelloRunnable())).start();
	}
	
	
	public static class HelloThread extends Thread {
	    public void run() {
	        log.info("Hello from a thread!");
	    }
	}
	@Test
	public void testHelloThread() {
		(new HelloThread()).start();
	}

	@Test
	public void testSleep() throws InterruptedException {
		String importantInfo[] = { "Mares eat oats", "Does eat oats", "Little lambs eat ivy", "A kid will eat ivy too" };

		for (int i = 0; i < importantInfo.length; i++) {
			// Pause for 4 seconds
			Thread.sleep(4000);
			// Print a message
			log.info(importantInfo[i]);
		}
	}
	
	public static class InterruptableWorker implements Runnable {
		private int i = 0;

		public void run() {
			log.info("worker starts");
			for (int _i = 0; _i < 20000; _i++) {
				if (Thread.interrupted()) {
					log.info("interrupted during execution at step: " + i);
					return;
				}
				try {
					Thread.sleep(1000);
					log.info("step: " + i++);
				} catch (InterruptedException e) {
					log.info("interrupted during sleeping at step: " + i);
					return;
				}
			}
		}
	}
	
	@Test
	public void testInterrupt() throws InterruptedException {
		Thread thread = new Thread(new InterruptableWorker());
		thread.start();
		Thread.sleep(2900);
		thread.interrupt();
		thread.join();
	}
}
