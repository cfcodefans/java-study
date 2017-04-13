package cf.study.java.utils.concurrent.example.ifeve;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyLock extends ReentrantLock {
	private static final long serialVersionUID = 1L;

	public String getOwnerName() {
		if (this.getOwner() == null) {
			return "None";
		}
		return this.getOwner().getName();
	}
	
	public Collection<Thread> getThreads() {
		return this.getQueuedThreads();
	}
	
	public static class Task implements Runnable {

		private Lock lock;
		
		public Task(Lock lock) {
			super();
			this.lock = lock;
		}

		@Override
		public void run() {
			for (int i = 0; i < 5; i++) {
				lock.lock();
				System.out.printf("%s: Get the lock. \n", Thread.currentThread().getName());
				try {
					TimeUnit.MILLISECONDS.sleep(500);
					System.out.printf("%s: Free the lock. \n", Thread.currentThread().getName());
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		}
		
	}

	public static void main(String[] args) throws Exception {
		MyLock lock = new MyLock();
		Thread threads[] = new Thread[5];
		
		for (int i = 0; i < 5; i++) {
			Task task = new Task(lock);
			threads[i] = new Thread(task);
			threads[i].start();
		}
		
		for (int i = 0; i < 15; i++) {
			System.out.println("Main: Logging the lock");
			System.out.println("**********************");
			System.out.println("Lock's owner: " + lock.getOwnerName());
			
			System.out.println("Lock: Queued Threads: " + lock.hasQueuedThreads());
			
			if (lock.hasQueuedThreads()) {
				System.out.println("Lock: Queue Length: " + lock.getQueueLength());
			}
			
			Collection<Thread> lockedThreads = lock.getThreads();
			for (Thread thread : lockedThreads) {
				System.out.println(thread.getName());
			}
			
			System.out.println("Fair: " + lock.isFair());
			System.out.println("locked: " + lock.isLocked());
			
			TimeUnit.SECONDS.sleep(1);
		}
	}
}
