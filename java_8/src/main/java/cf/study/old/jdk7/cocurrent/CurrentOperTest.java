package cf.study.jdk7.cocurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

public class CurrentOperTest {
	public static class Counter {
		private int c = 0;

		public void increment() {
			if (c<1)
			c++;
		}

		public void decrement() {
			if (c>0)
			c--;
		}

		public int value() {
			return c;
		}
	}

	private ExecutorService pool1 = Executors.newFixedThreadPool(6);
	
	@Test
	public void testCurrentOper1() {
		final Counter ct = new Counter();
		pool1.submit(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
				ct.increment();
				int value = ct.value();
				System.out.println("ct.increment(): " + value + (value < 1 ? "unexpected" : ""));
				}
			}
		});
		
		pool1.submit(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
				ct.decrement();
				int value = ct.value();
				System.out.println("ct.decrement(): " + value + (value > 0 ? "unexpected" : ""));
				}
			}
		});
	}

	@Test
	public void testCurrentOper2() {
		final Counter ct = new Counter();
		pool1.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < 1000; i++) {
				ct.increment();
				System.out.println("ct.increment(): " + ct.value());
				}
			}
		});
		
		pool1.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < 1000; i++) {
				ct.decrement();
				System.out.println("ct.decrement(): " + ct.value());
				}
			}
		});
		
		
	}
}
