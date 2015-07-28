package cf.study.jdk7.cocurrent.lock;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;

public class ReadWriteLockTest {

	static class Value {
		int i = 0;
	}

	static Value v = new Value();

	ReentrantReadWriteLock rl = new ReentrantReadWriteLock();

	int read() {
		final String threadName = Thread.currentThread().getName();
		if (rl.readLock().tryLock()) {
			System.out.println(threadName + " tryLock");
		} else {
			System.out.println(threadName + " get not Lock");
		}
		try {
			System.out.println(threadName + " reading");
			sleep(3);
			return v.i;
		} finally {
			System.out.println(threadName + " read done: " + v.i);
			rl.readLock().unlock();
		}
	}

	private void sleep(int i) {
		try {
			TimeUnit.SECONDS.sleep(i);
		} catch (InterruptedException e) {
			System.out.println(Thread.currentThread().getName());
			e.printStackTrace();
		}
	}

	void write(int i) {
		rl.writeLock().lock();
		try {
			System.out.println(Thread.currentThread().getName() + " write");
			sleep(3);
			v.i = i;
		} finally {
			System.out.println(Thread.currentThread().getName() + " write done: " + v.i);
			rl.writeLock().unlock();
		}
	}

	ExecutorService es = Executors.newFixedThreadPool(5);
	
	Callable<Integer> read3 = new Callable<Integer>() {
		@Override
		public Integer call() {
			for (int i = 0; i < 3; i++) {
				int readValue = ReadWriteLockTest.this.read();
				System.out.println(readValue);
			}
			return ReadWriteLockTest.this.v.i;
		}
	};
	
	Callable<Integer> write3 = new Callable<Integer>() {
		@Override
		public Integer call() {
			for (int i = 0; i < 3; i++) {
				ReadWriteLockTest.this.write(i);
//				System.out.println(readValue);
			}
			return ReadWriteLockTest.this.v.i;
		}
	};
	
	@Test
	public void readAndRead() {
		try {
			es.invokeAny(Arrays.asList(read3, read3, read3));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void readAndWrite() {
		try {
			es.invokeAny(Arrays.asList(write3, write3, write3));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
