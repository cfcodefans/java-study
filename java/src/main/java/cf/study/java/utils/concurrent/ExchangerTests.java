package cf.study.java.utils.concurrent;

import misc.MiscUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class ExchangerProducer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ExchangerProducer.class);
    List<Integer> holder;
    Exchanger<List<Integer>> exchanger;

    public ExchangerProducer(List<Integer> holder, Exchanger<List<Integer>> exchanger) {
        this.holder = holder;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                for (int i = 0; i < 10; i++) {
                    log.info("I give {}", i);
                    holder.add(i);
                    MiscUtils.easySleep(1000);
                    holder = exchanger.exchange(holder);
                }
            }
        } catch (InterruptedException e) {

        }
    }
}

class ExchangerConsumer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ExchangerConsumer.class);
    List<Integer> holder;
    Exchanger<List<Integer>> exchanger;

    private volatile Integer value;
    static int num = 0;

    public ExchangerConsumer(List<Integer> holder, Exchanger<List<Integer>> exchanger) {
        this.holder = holder;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                holder = exchanger.exchange(holder);
                for (int i = 0; i < 10; i++) {
                    log.info("\tI got {} ", holder.get(i));
                }
            }
        } catch (InterruptedException e) {

        }
    }
}

public class ExchangerTests {
    private static final Logger log = LoggerFactory.getLogger(ExchangerTests.class);

    @Test
    public void test() throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        List<Integer> producerList = new CopyOnWriteArrayList<>();
        List<Integer> consumerList = new CopyOnWriteArrayList<>();
        Exchanger<List<Integer>> exchanger = new Exchanger<>();
        exec.execute(new ExchangerProducer(producerList, exchanger));
        exec.execute(new ExchangerConsumer(consumerList, exchanger));
        TimeUnit.SECONDS.sleep(4);
        exec.shutdownNow();
    }

    @Test
    public void test1() throws InterruptedException {
        Exchanger<List<Integer>> ex = new Exchanger<>();

        List<Integer> holder = new ArrayList<>();
        holder.add(10);
        List<Integer> holder1 = new ArrayList<>();
        holder1.add(10);

        ExecutorService es = Executors.newCachedThreadPool();
        es.execute(() -> {
            List<Integer> _holder = holder;
            try {
                while (!Thread.interrupted()) {
                    int v = _holder.get(0).intValue();
                    log.info("I make minus {} ", v - 1);
                    _holder.set(0, v - 1);
                    _holder = ex.exchange(holder);
                    MiscUtils.easySleep(1000);
                }
            } catch (InterruptedException e) {
            }
        });

        es.execute(() -> {
            List<Integer> _holder = holder1;
            try {
                while (!Thread.interrupted()) {
                    _holder = ex.exchange(_holder);
                    int v = _holder.get(0).intValue();
                    log.info("\tI get {} ", v + 1);
                }
            } catch (InterruptedException e) {
            }
        });

        TimeUnit.SECONDS.sleep(4);
        es.shutdownNow();
    }
}
