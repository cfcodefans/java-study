package cf.study.java8.utils.concurrent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class QueueTest {
	public void testConcurrentLinkedQueue() {
//		 final Queue<Integer> q = new LinkedBlockingQueue<Integer>();
		final Queue<Integer> q = new ConcurrentLinkedDeque<Integer>();

		final AtomicLong sum = new AtomicLong(0);
		int supplierNum = 1;
		int consumerNum = 32;
		final int elementNum = 1 << 21;

		for (; supplierNum <= 32; supplierNum = supplierNum << 1) {
			System.out.printf("producer: %d \t", supplierNum);
			for (int i = 0; i < 4; i++) {
				System.out.printf("%d ms \t", doQueueTest(q, sum, supplierNum, consumerNum, elementNum));
				System.out.printf("result %d \t\t", sum.get());
			}
			System.out.println();
		}
	}

	private long doQueueTest(final Queue<Integer> q, final AtomicLong sum, final int supplierNum, int consumerNum, final int elementNum) {
		sum.set(0);
		ExecutorService executors = Executors.newFixedThreadPool(supplierNum + consumerNum);
//		ExecutorService consumerExecutors = Executors.newFixedThreadPool(supplierNum);

		long start = System.currentTimeMillis();

		Runnable supplier = new Runnable() {
			public void run() {
				for (int i = 0, j = elementNum / supplierNum; i < j; i++) {
					q.offer(1);
				}
			}
		};
		
		for (int i = 0; i < supplierNum; i++) {
			executors.submit(supplier);
		}

		Runnable consumer = new Runnable() {
			public void run() {
				for (Integer i = null; sum.get() < elementNum; i = q.poll()) {
					if (i == null) continue;
					sum.addAndGet(i);
				}
			}
		};
		for (int i = 0; i < consumerNum; i++) {
			executors.submit(consumer);
		}

		executors.shutdown();
//		consumerExecutors.shutdown();

		try {
//			while (!(supplyExecutors.isTerminated() && consumerExecutors.isTerminated())) {
//				Thread.sleep(1);
//			}
			executors.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return System.currentTimeMillis() - start;
	}

	public static void main(String[] args) {
		QueueTest qt = new QueueTest();
		qt.testConcurrentLinkedQueue();
	}
}
