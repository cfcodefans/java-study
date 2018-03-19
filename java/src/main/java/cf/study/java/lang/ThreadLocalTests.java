package cf.study.java.lang;

import misc.MiscUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadLocalTests {
    public static final Logger log = LogManager.getLogger(ThreadLocalTests.class);

    static ThreadLocal<DummyEntry> tl = null;

    static class DummyEntry {
        public DummyEntry() {
            log.info(MiscUtils.invocInfo());
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            log.info(MiscUtils.invocInfo());
        }
    }

    @BeforeClass
    public static void initThreadLocal() {
        tl = ThreadLocal.withInitial(DummyEntry::new);
    }

    @Test
    public void testRemove() throws InterruptedException {
        tl.get();
        tl.remove();
        tl.get();
        tl.get();
        tl.get();

        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < 1000; i++) {
            es.submit(() -> tl.get());
        }

        while (!es.isTerminated())
            es.awaitTermination(1, TimeUnit.SECONDS);
    }
}
