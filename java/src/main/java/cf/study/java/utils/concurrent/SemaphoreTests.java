package cf.study.java.utils.concurrent;

import misc.MiscUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SemaphoreTests {

    private static final Logger log = LoggerFactory.getLogger(SemaphoreTests.class);

    ExecutorService es = Executors.newCachedThreadPool(MiscUtils.namedThreadFactory("something"));

    @Test
    public void testSamphore() throws Exception {
        final Semaphore s = new Semaphore(3);

        IntStream.range(0, 6)
            .mapToObj(i -> (Runnable) (() -> {
                log.info(":\tcan {} enter?", i);
                try {
                    while (!s.tryAcquire(1, TimeUnit.SECONDS)) {
                        log.info(":\tstill need to wait in line of " + s.getQueueLength());
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
                log.info(":\t{} is in! will stay here for 3 seconds", i);
                MiscUtils.easySleep(3000);
                log.info(":\t{} is out of here", i);
                s.release();
            })).forEach(r -> es.submit(r));

        es.shutdown();
        while (!es.awaitTermination(1, TimeUnit.SECONDS)) ;
        log.info("finished");
    }
}
