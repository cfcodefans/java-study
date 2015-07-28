package cf.study.jdk7.cocurrent;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.jboss.weld.environment.se.threading.RunnableDecorator;
import org.junit.Test;

public class AtomTests {

	@Test
	public void testAtomInt() {
		AtomicInteger ai = new AtomicInteger();
		for (int i = 0; i < 10; i = ai.get()) {
			System.out.println(ai.incrementAndGet());
		}
	}
	
	@Test
	public void testAtomIntInThreads() {
		final AtomicInteger ai = new AtomicInteger();
		final int cnt = 4000;
		final ExecutorService es = Executors.newFixedThreadPool(cnt);
		
		final long startPoint = System.currentTimeMillis() + 100;
		
		final Callable<Void> setter = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				while (System.currentTimeMillis() <= startPoint);
				ai.incrementAndGet();
				return null;
			}
		};
		
		final Callable<Void>[] setters = new Callable[cnt];
		Arrays.fill(setters, setter);
		
		try {
			es.invokeAll(Arrays.asList(setters));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(ai.get(), cnt);
	}
	
	@Test
	public void testIntInThreads() {
		final int[] i = new int[1];
		final int cnt = 4000;
		final ExecutorService es = Executors.newFixedThreadPool(cnt);
		
		final long startPoint = System.currentTimeMillis() + 100;
		
		final Callable<Void> setter = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				while (System.currentTimeMillis() <= startPoint);
				i[0] = i[0] + 1;
				return null;
			}
		};
		
		final Callable<Void>[] setters = new Callable[cnt];
		Arrays.fill(setters, setter);
		
		try {
			es.invokeAll(Arrays.asList(setters));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(i[0], cnt);
	}
	
	private Object shareable;
	
	@Test
	public void testThreads() {
		Runnable runner = new Runnable() {
			public void run() {
				System.out.println(Thread.currentThread().getName() + " {");
				for (int i = 0; i < 5; i++) {
					System.out.println("\t do something " + i);
				}
				System.out.println("} " + Thread.currentThread().getName());
			}
		};
		
		Runnable[] runners = new Runnable[5];
		runners[0] = runner;
		runners[1] = runner;
		runners[2] = runner;
		runners[3] = runner;
		runners[4] = runner;
		final ExecutorService es = Executors.newCachedThreadPool();
		
		try {
			for (Runnable r : runners) {
				es.submit(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
