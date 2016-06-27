package cf.study.java8.utils.concurrent;

import misc.MiscUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by fan on 2016/6/25.
 */
public class CompletableFutureTests {
	private static final Logger log = LogManager.getLogger(CompletableFutureTests.class);

	private Supplier<Long> _long(long sleep) {
		return () -> {
			log.info(MiscUtils.invocInfo());
			MiscUtils.easySleep(sleep);
			log.info("finished");
			return Long.valueOf(sleep);
		};
	}

	@Before public void before() {
		log.info(System.currentTimeMillis());
	}

	@Test public void testGet() throws ExecutionException, InterruptedException {
		CompletableFuture<Long> test = CompletableFuture.supplyAsync(_long(1000));
		log.info(test.get());
	}

	@Test public void testThenApply() throws ExecutionException, InterruptedException {
		Function<Long, Long> foo = (_l) -> {
			log.info(MiscUtils.invocInfo());
			return _l;
		};
		CompletableFuture<Long> test = CompletableFuture.supplyAsync(_long(1000))
			.thenApply(foo)
			.thenApply(foo)
			.thenApply(foo)
			.thenApply(foo);
		log.info(test.get());
	}

	@Test public void testThenApplyAsync() throws ExecutionException, InterruptedException {
		Function<Long, Long> foo = (_l) -> {
			log.info(MiscUtils.invocInfo());
			MiscUtils.easySleep(1000);
			return _l;
		};
		CompletableFuture<Long> test = CompletableFuture.supplyAsync(_long(1000))
			.thenApplyAsync(foo)
			.thenApplyAsync(foo)
			.thenApplyAsync(foo)
			.thenApplyAsync(foo);
		log.info(test.get());
	}

	@Test public void testAllOf() throws ExecutionException, InterruptedException {
		Function<Long, Long> foo = (_l) -> {
			log.info(MiscUtils.invocInfo());
			MiscUtils.easySleep(1000);
			return _l;
		};
		CompletableFuture<Long> test = CompletableFuture.allOf(
			CompletableFuture.supplyAsync(_long(1000)),
			CompletableFuture.supplyAsync(_long(2000)),
			CompletableFuture.supplyAsync(_long(100)),
			CompletableFuture.supplyAsync(_long(500))
		).thenApply((v) -> 0l);
		log.info(test.get());
	}

	@Test public void testAnyOf() throws ExecutionException, InterruptedException {
		Function<Long, Long> foo = (_l) -> {
			log.info(MiscUtils.invocInfo());
			MiscUtils.easySleep(1000);
			return _l;
		};
		CompletableFuture<Object> test = CompletableFuture.anyOf(
			CompletableFuture.supplyAsync(_long(1000)),
			CompletableFuture.supplyAsync(_long(2000)),
			CompletableFuture.supplyAsync(_long(100)),
			CompletableFuture.supplyAsync(_long(500))
		);
		log.info(test.get());
	}
}
