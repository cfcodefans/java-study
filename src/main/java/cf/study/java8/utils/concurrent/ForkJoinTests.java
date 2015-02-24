package cf.study.java8.utils.concurrent;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

import org.junit.Test;

public class ForkJoinTests {

	static class Fibonacci extends RecursiveTask<Integer> {
		private static final long serialVersionUID = 1L;
		final int n;

		Fibonacci(int n) {
			this.n = n;
		}

		public Integer compute() {
			if (n <= 1)
				return n;

			System.out.println(String.format("%d is processed in thread: %s", n, Thread.currentThread().getName()));

			Fibonacci f1 = new Fibonacci(n - 1);
			f1.fork();
			Fibonacci f2 = new Fibonacci(n - 2);
			return f2.compute() + f1.join();
		}
	}

	@Test
	public void fibonacciRecursiveTask() {
		Fibonacci f = new Fibonacci(5);
		System.out.println(f.compute());
	}

	static class QuickSortTask extends RecursiveAction {
		final long[] array;
		final int lo, hi;
		static final int THRESHOLD = 10;

		QuickSortTask(long[] array, int lo, int hi) {
			this.array = array;
			this.lo = lo;
			this.hi = hi;
		}

		public QuickSortTask(long[] array) {
			this(array, 0, array.length);
		}

		@Override
		protected void compute() {
			if (hi - lo < THRESHOLD) {
				Arrays.sort(array, lo, hi);
				return;
			}

			int mid = (lo + hi) >>> 1;
			invokeAll(new QuickSortTask(array, lo, mid), new QuickSortTask(array, mid, hi));
			merge(lo, mid, hi);
		}

		void merge(int lo, int mid, int hi) {
			long[] buf = Arrays.copyOfRange(array, lo, mid);
			for (int i = 0, j = lo, k = mid; i < buf.length; j++) {
				array[j] = (k == hi || buf[i] < array[k]) ? buf[i++] : array[k++];
			}
		}
	}
	
	@Test
	public void testQuickSortTask() {
		Random r = new Random(System.currentTimeMillis());
		long[] ary = new long[100000];
		for (int i = 0; i < ary.length; i++) {
			ary[i] = Math.abs(r.nextLong()) % ary.length;
		}
		
		for (int i = 0; i < 10; i++) {
			System.out.println(ary[i]);
		}
		
		System.out.println("\nstart sorting\n");
		QuickSortTask qst = new QuickSortTask(ary);
		qst.invoke();
		
		for (int i = 0; i < 10; i++) {
			System.out.println(ary[i]);
		}
	}
}
