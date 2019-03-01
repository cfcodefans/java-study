package cf.study.java.utils.concurrent;

import misc.MiscUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class CyclicBarrierTests {

    private static final Logger log = LoggerFactory.getLogger(CyclicBarrierTests.class);

    @Test
    public void test() {
        List<List<Double>> data = new ArrayList<>();
        int totalNumber = 4;
        for (int r = 0; r < totalNumber; r++) {
            List<Double> row = new ArrayList<>();
            for (int c = 0, rowSize = RandomUtils.nextInt(0, 100); c < rowSize; c++) {
                row.add(RandomUtils.nextDouble(0, rowSize));
            }
            data.add(row);
        }

        List<Double> sumList = new ArrayList<>(totalNumber);
        IntStream.range(0, totalNumber).mapToDouble(Double::valueOf).forEach(sumList::add);

        CyclicBarrier cb = new CyclicBarrier(totalNumber,
            () -> {
                log.info("\n\tsum of all:\n\t{}", sumList.stream().mapToDouble(e -> e).summaryStatistics());
            });

        ExecutorService es = Executors.newCachedThreadPool();
//            Executors.newFixedThreadPool(totalNumber);

        for (int r = 0, s = data.size(); r < s; r++) {
            int rowIdx = r;
            es.execute(() -> {
                List<Double> row = data.get(rowIdx);

                int sleepTime = 1000 + (int) (1000 * RandomUtils.nextFloat(0.0f, 1.0f));
                log.info("row {} is working on {}", rowIdx, sleepTime);
                MiscUtils.easySleep(sleepTime);
                DoubleSummaryStatistics rowStat = row.stream().mapToDouble(e -> e).summaryStatistics();
                double rowSum = rowStat.getSum();
                log.info("row: {} sum: {} done while there are already {} ROW done",
                    rowIdx,
                    rowSum,
                    cb.getNumberWaiting());

                sumList.set(rowIdx, Double.valueOf(rowSum));

                try {
                    cb.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    log.error("exception while row " + rowIdx + " waiting", e);
                }
            });
        }

        es.shutdown();

//        for (int wc = cb.getNumberWaiting(); wc < totalNumber; wc = cb.getNumberWaiting()) {
//            log.info("main finds {} waiting workers", wc);
//            MiscUtils.easySleep(1000);
//            if (es.isTerminated()) {
//                MiscUtils.easySleep(1000);
//                return;
//            }
//        }

        MiscUtils.easySleep(4000);
    }


    @Test
    public void test2() {
        ExecutorService es = Executors.newCachedThreadPool();
        final CyclicBarrier cb = new CyclicBarrier(4, () -> log.info("all done"));//创建CyclicBarrier对象并设置3个公共屏障点
        for (int i = 0; i < 4; i++) {
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        int waitFor = 1000;
                        Thread.sleep((long) (Math.random() * waitFor));
                        System.out.println("线程" + Thread.currentThread().getName() +
                            "即将到达集合地点1，当前已有" + cb.getNumberWaiting() + "个已经到达，正在等候");
                        cb.await();//到此如果没有达到公共屏障点，则该线程处于等待状态，如果达到公共屏障点则所有处于等待的线程都继续往下运行

//                        Thread.sleep((long) (Math.random() * waitFor));
//                        System.out.println("线程" + Thread.currentThread().getName() +
//                            "即将到达集合地点2，当前已有" + cb.getNumberWaiting() + "个已经到达，正在等候");
//                        cb.await();
//                        Thread.sleep((long) (Math.random() * waitFor));
//                        System.out.println("线程" + Thread.currentThread().getName() +
//                            "即将到达集合地点3，当前已有" + cb.getNumberWaiting() + "个已经到达，正在等候");
//                        cb.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            es.execute(runnable);
        }
        es.shutdown();

        while (!es.isTerminated()) {
            MiscUtils.easySleep(1000);
        }
    }


}
