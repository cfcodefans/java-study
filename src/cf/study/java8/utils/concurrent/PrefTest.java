package cf.study.java8.utils.concurrent;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

public class PrefTest implements Runnable {
//	static volatile long counter = 0;
	static AtomicLong counter = new AtomicLong(0);
	
	static void inc() {
		try {
			Paths.get("/").toFile().list();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		counter++;
		counter.incrementAndGet();
	}

	private int rounds = 1;
	
	public PrefTest(int num) {
		this.rounds = num;
	}
	
	@Override
	public void run() {
		for (long i = 0; i < rounds; i++) {
			inc();
		}
	}

	static final int THREAD_NUM = 8;
	static final long EXPECTED_COUNTER = 1 << 16; //65536
	
	public static void main(String[] args) {
		System.out.printf("cpu core: %d \n", Runtime.getRuntime().availableProcessors());
		
		System.out.printf("start\n");
		
		for (int ci = 1; ci <= 256; ci = ci << 1) {
			System.out.print(ci + " \t");
			for (int i = 0; i < 3; i++) {
//				counter = 0;
				counter.set(0);
				System.out.print(doTest(ci) + " \t ");
			}
			System.out.print(counter + " \t ");
			
			System.out.println();
		}
	}
	
	static long doTest(int testThreadNum) {
		long start = System.currentTimeMillis();
		
		Thread[] threads = new Thread[testThreadNum];
		for (int i = 0; i < testThreadNum; i++) {
			threads[i] = new Thread(new PrefTest((int) (EXPECTED_COUNTER / testThreadNum)));
		}
		
		for (Thread thread : threads) {
			thread.start();
		}
		
		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return System.currentTimeMillis() - start;
	}
}
