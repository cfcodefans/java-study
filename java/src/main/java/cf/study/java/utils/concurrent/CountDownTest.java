package cf.study.java.utils.concurrent;

import misc.MiscUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class CountDownTest {

    private static final Logger log = LoggerFactory.getLogger(CountDownTest.class);

    Random rd = new Random();

    @Test
    public void testCountDown() throws Exception {
        int count = 18;
        CountDownLatch counter = new CountDownLatch(count);

        if (counter.getCount() > 0) {
            log.info("counter.getCount(): {}", counter.getCount());
        }
        ExecutorService tp = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        IntStream.range(0, count)
            .mapToObj(i -> (Runnable) (() -> {
                int delay = (int) (1000 * rd.nextFloat());
                MiscUtils.easySleep(delay);
                log.info("\t\t{} slept {} ms", i, delay);
                counter.countDown();
            })).forEach(task -> tp.submit(task));

        for (long left = counter.getCount(); left > 0; left = counter.getCount()) {
            log.info("still {} not finished", left);
            counter.await(100, TimeUnit.MILLISECONDS);
        }
        log.info("finish");
    }
}
