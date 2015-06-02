package cf.study.java8.utils.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

public class FutureTests {

	@Test
	public void testSimpleFuture() throws Exception {
		Future<Long> nextSecond = Executors.newSingleThreadExecutor().submit(() -> {
			long t = System.currentTimeMillis();
			for (; 1000 - (t % 1000) > 1; t = System.currentTimeMillis()) {
				Thread.sleep(1);
			}
			return t;
		});
		
		System.out.println(nextSecond.get());
	}
}
