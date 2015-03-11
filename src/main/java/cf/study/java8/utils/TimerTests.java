package cf.study.java8.utils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import misc.MiscUtils;

import org.junit.Test;

public class TimerTests {

	@Test
	public void testTimer() throws Exception {
		Timer timer = new Timer("test_timer");
		
		timer.schedule(new TimerTask() {
			public void run() {
				System.out.println(new Date());
			}
		}, 0, 1000);
		
		Thread.sleep(5000);
		
		timer.cancel();
	}
	
	@Test
	public void testScheduledExecutionTime() throws Exception {
		Timer timer = new Timer("test_timer");
		
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				System.out.println(new Date() + " " + new Date(this.scheduledExecutionTime()));
			}
		}, 0, 1000);
		
		Thread.sleep(5000);
		
		timer.cancel();
	}
	
	@Test
	public void testTimerThread() throws Exception {
		Timer timer = new Timer("test_timer");
		
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				System.out.println(Thread.currentThread());
				System.out.println(MiscUtils.stackInfo());
			}
		}, 0, 1000);
		
		Thread.sleep(5000);
		
		timer.cancel();
	}
}
