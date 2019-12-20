package study.java.lang.base;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.commons.MiscUtils;

import java.lang.ref.Cleaner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadLocalTests {
    static final Logger log = LoggerFactory.getLogger(ThreadLocalTests.class);
    static ThreadLocal<DummyEntry> tl = null;

    static final Cleaner cleaner = Cleaner.create();

    static class DummyEntry {
        Cleaner.Cleanable c = null;

        public DummyEntry() {
//            log.info(MiscUtils.invocInfo());
            c = cleaner.register(this, this::getCleaned);
        }

        public void getCleaned() {
            log.info(MiscUtils.invocationInfo(5));
        }
    }

    @BeforeClass
    public static void initThreadLocal() {
        tl = ThreadLocal.withInitial(DummyEntry::new);
    }

    @Test
    public void testLocalValueFinalization() throws InterruptedException {
        tl.get();
        tl.remove();
        tl.get();
        tl.get();
        tl.get();

        {
            ExecutorService es = Executors.newCachedThreadPool(); //.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (int i = 0; i < 1000; i++) {
                es.submit(() -> {
                    tl.get();
                });
            }
            es.shutdownNow();
            while (!es.isTerminated())
                es.awaitTermination(2, TimeUnit.SECONDS);
        }
    }
}
