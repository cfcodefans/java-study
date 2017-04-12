package cf.study.java8.utils.concurrent.lock;

import static misc.MiscUtils.easySleep;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import misc.MiscUtils;

public class ReadWriteLockTest {

	static class Value {
		int	i	= 0;
	}

	static Value			v	= new Value();

	ReentrantReadWriteLock	rl	= new ReentrantReadWriteLock();

	int read() {
		if (rl.readLock().tryLock()) {
			log.info(" tryLock");
		} else {
			log.info(" get not Lock");
		}
		try {
			log.info(" reading");
			easySleep(3);
			return v.i;
		} finally {
			log.info(" read done: " + v.i);
			rl.readLock().unlock();
		}
	}

	void write(int i) {
		rl.writeLock().lock();
		try {
			log.info(" write");
			easySleep(3);
			v.i = i;
		} finally {
			log.info(" write done: " + v.i);
			rl.writeLock().unlock();
		}
	}

	ExecutorService	es	= Executors.newFixedThreadPool(5, MiscUtils.namedThreadFactory("something"));

	@SuppressWarnings("static-access")
	int callable_read() {
		for (int i = 0; i < 3; i++) {
			int readValue = ReadWriteLockTest.this.read();
			System.out.println(readValue);
		}
		return ReadWriteLockTest.this.v.i;
	}

	@SuppressWarnings("static-access")
	int callable_write() {
		for (int i = 0; i < 3; i++) {
			ReadWriteLockTest.this.write(i);
			// System.out.println(readValue);
		}
		return ReadWriteLockTest.this.v.i;
	}

	private static final Logger	log	= LoggerFactory.getLogger(ReadWriteLockTest.class);

	@Test
	public void readAndRead() {
		try {
			Callable<Integer> read3 = this::callable_read;
			es.invokeAny(Arrays.asList(read3, read3, read3));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void readAndWrite() {
		try {
			Callable<Integer> write3 = this::callable_write;
			es.invokeAny(Arrays.asList(write3, write3, write3));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
