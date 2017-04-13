package cf.study.java.utils.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import misc.MiscUtils;

public class PerformanceComparison {

	private static final Logger log = LoggerFactory.getLogger(PerformanceComparison.class);

	long value = 0;
	final long target = 400000000L;
	
	StopWatch sw = new StopWatch();
	
	@Before
	public void startWatch() {
		sw.start();
	}
	
	@After
	public void stopWatch() {
		sw.stop();
		log.info(String.format("took %d ms %d ns to got %d", sw.getTime(), sw.getNanoTime(), target));
		Assert.assertEquals(target, value);
	}
	
	@Test
	public void testSingleThread() {
		for (long i = 0; i < target; i++) {
			something();
			value++;
		}
	}
	
	synchronized void addOne() {
		something();
		value++;
	}
	
	void something() {
		for (int i = 0; i < 20; i++)Math.sqrt(123456);
	}

	ExecutorService es = Executors.newFixedThreadPool(MiscUtils.AVAILABLE_PROCESSORS);
	
	@Test
	public void testMultiThread() throws Exception {
		long part = target / MiscUtils.AVAILABLE_PROCESSORS;
		Runnable addOne = ()->{
			for (long i = 0, j = part; i < j; i++) {
				something();
				value++;
			}
		};
		
		for (int i = 0; i < MiscUtils.AVAILABLE_PROCESSORS; i++)
			es.submit(addOne);
		
		es.shutdown();
		while (!es.awaitTermination(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testSynchronized() throws Exception {
		long part = target / MiscUtils.AVAILABLE_PROCESSORS;
		Runnable addOne = ()->{
			for (long i = 0, j = part; i < j; i++) {
				addOne();
			}
		};
		
		for (int i = 0; i < MiscUtils.AVAILABLE_PROCESSORS; i++)
			es.submit(addOne);
		
		es.shutdown();
		while (!es.awaitTermination(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testAtomic() throws Exception {
		AtomicLong _value = new AtomicLong(value);
		long part = target / MiscUtils.AVAILABLE_PROCESSORS;
		Runnable addOne = ()->{
			for (long i = 0, j = part; i < j; i++) {
				something();
				_value.incrementAndGet();
			}
		};
		
		for (int i = 0; i < MiscUtils.AVAILABLE_PROCESSORS; i++)
			es.submit(addOne);
		
		es.shutdown();
		while (!es.awaitTermination(1, TimeUnit.SECONDS));
		value = _value.get();
	}
}
