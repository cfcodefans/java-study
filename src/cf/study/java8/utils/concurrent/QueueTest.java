package cf.study.java8.utils.concurrent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;


public class QueueTest {
	public void testConcurrentLinkedQueue() {
//		 final Queue<Integer> q = new LinkedBlockingQueue<Integer>();
		final Queue<Integer> q = new ConcurrentLinkedDeque<Integer>();

		final AtomicLong sum = new AtomicLong(0);
		int supplierNum = 1;
		int consumerNum = 2;
		final int elementNum = 1 << 20;

		for (; supplierNum <= 32; supplierNum = supplierNum << 1) {
			System.out.printf("%d \t", supplierNum);
			for (int i = 0; i < 4; i++) {
				System.out.printf("%d \t", doQueueTest(q, sum, supplierNum, consumerNum, elementNum));
				System.out.printf(" %d \t\t", sum.get());
			}
			System.out.println();
		}
	}

	private long doQueueTest(final Queue<Integer> q, final AtomicLong sum, int supplierNum, int consumerNum, final int elementNum) {
		sum.set(0);
		ExecutorService supplyExecutors = Executors.newFixedThreadPool(supplierNum);
		ExecutorService consumerExecutors = Executors.newFixedThreadPool(supplierNum);

		long start = System.currentTimeMillis();

		Runnable supplier = () -> {
			for (int i = 0, j = elementNum / supplierNum; i < j; i++) {
				q.offer(1);
			}
		};
		for (int i = 0; i < supplierNum; i++) {
			supplyExecutors.submit(supplier);
		}

		Runnable consumer = () -> {
			while (sum.addAndGet(q.poll()) < elementNum);
		};
		for (int i = 0; i < consumerNum; i++) {
			consumerExecutors.submit(consumer);
		}

		supplyExecutors.shutdown();
		consumerExecutors.shutdown();

		try {
			while (!(supplyExecutors.isTerminated() && consumerExecutors.isTerminated())) {
				Thread.sleep(1);
			}
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
