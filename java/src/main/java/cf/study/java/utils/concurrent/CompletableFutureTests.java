package cf.study.java.utils.concurrent;

import misc.MiscUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by fan on 2016/6/25.
 */
public class CompletableFutureTests {
    private static final Logger log = LogManager.getLogger(CompletableFutureTests.class);

    private static Supplier<Long> _long(long sleep) {
        return () -> {
            log.info(MiscUtils.invocInfo());
            MiscUtils.easySleep(sleep);
            log.info("finished");
            return Long.valueOf(sleep);
        };
    }

    @Test
    public void testAsyncRun() {
        CompletableFuture.runAsync(() -> log.info(Thread.currentThread()));
    }

    @Before
    public void before() {
        log.info(System.currentTimeMillis());
    }

    @Test
    public void testGet() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> test = CompletableFuture.supplyAsync(_long(1000));
        log.info(test.get());
    }

    @Test
    public void testThenApply() throws ExecutionException, InterruptedException {
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

    @Test
    public void testThenApplyAsync() throws ExecutionException, InterruptedException {
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

    @Test
    public void testAllOf() throws ExecutionException, InterruptedException {
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

    @Test
    public void testAnyOf() throws ExecutionException, InterruptedException {
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

    private static Supplier<Long> complex(final long count) {
        return () -> {
            try {
                for (long i = 0; i < count; i++) {
//					MiscUtils.interrupted();
                    log.info("{}\t{}", Thread.currentThread(), i);
                    Thread.sleep(100);
                }
            } catch (Throwable e) {
                log.error(Thread.currentThread().toString(), e);
            }
            return count;
        };
    }

    @Test
    public void testComplex() throws ExecutionException, InterruptedException {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(complex(10), exec);
        log.info(future.get());
    }

    @Test
    public void testComplexCancel() throws ExecutionException, InterruptedException {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(complex(10), exec);
        Thread.sleep(300);
        try {
//			future.getNow(Long.valueOf(-1));
//			future.complete(Long.MAX_VALUE);
            future.completeExceptionally(new InterruptedException("cancellation"));
            log.info(future.get());
        } catch (Exception e) {
            log.error("", e);
        }

        future = CompletableFuture.supplyAsync(complex(10), exec);
        log.info(future.get());
    }
}
