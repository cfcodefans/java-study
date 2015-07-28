package cf.study.jdk7.cocurrent.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

public class ExecutorTests {

	private static final Logger log = Logger.getLogger(ExecutorTests.class.getName());
	
	@Test
	public void testSingleThreadExecutor() {
		ExecutorService es = Executors.newSingleThreadExecutor();
		es.submit(new Runnable() {
			@Override
			public void run() {
				
				while (!Thread.interrupted()) {
					log.info("running");
				}
			}
		});
		
		join();
	}
	
	
	



	public static void join() {
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
