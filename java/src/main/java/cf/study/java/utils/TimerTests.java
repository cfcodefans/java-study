package cf.study.java.utils;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import misc.Lambdas.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import misc.MiscUtils;

public class TimerTests {

	static Logger log = LogManager.getLogger(TimerTests.class);

	@Test
	public void testTimer() throws Exception {
		Timer timer = new Timer("test_timer");
		timer.schedule(new LambdaTimerTask(() -> log.info(new Date())), 0, 1000);
		Thread.sleep(5000);
		timer.cancel();
	}

	@Test
	public void testScheduledExecutionTime() throws Exception {
		Timer timer = new Timer("test_timer");
		LambdaTimerTask task = new LambdaTimerTask((t) -> {
			log.info(new Date() + " " + new Date(t.scheduledExecutionTime()));
		});
		timer.schedule(task, 0);
		Thread.sleep(1000);
		timer.cancel();
		log.info("execution time: " + task.scheduledExecutionTime());
	}
	
	@Test
	public void testScheduleWithPeriod() throws Exception {
		Timer timer = new Timer("test_timer");
		long now = System.currentTimeMillis();
		AtomicInteger counter = new AtomicInteger();
		LambdaTimerTask task = new LambdaTimerTask((t) -> {
			log.info(System.currentTimeMillis() - now);
			if (counter.incrementAndGet() == 2) {
				log.info("delay 250ms");
				MiscUtils.easySleep(250);
			}
		});
		timer.schedule(task, 0, 200);
		Thread.sleep(1000);
		timer.cancel();
		log.info("execution time: " + task.scheduledExecutionTime());
	}
	
	@Test
	public void testScheduleAtFixedRate() throws Exception {
		Timer timer = new Timer("test_timer");
		long now = System.currentTimeMillis();
		AtomicInteger counter = new AtomicInteger();
		LambdaTimerTask task = new LambdaTimerTask((t) -> {
			log.info(System.currentTimeMillis() - now);
			if (counter.incrementAndGet() == 2) {
				log.info("delay 250ms");
				MiscUtils.easySleep(250);
			}
		});
		timer.scheduleAtFixedRate(task, 0, 200);
		Thread.sleep(1000);
		timer.cancel();
		log.info("execution time: " + task.scheduledExecutionTime());
	}

	@Test
	public void testTimerThread() throws Exception {
		Timer timer = new Timer("test_timer");
		timer.scheduleAtFixedRate(new LambdaTimerTask((t) -> {
			log.info(Thread.currentThread());
			log.info(MiscUtils.stackInfo());
		}), 0, 1000);

		Thread.sleep(5000);
		timer.cancel();
	}

	@Test
	public void testMultipleTasks() throws Exception {
		Timer timer = new Timer("test_timer");

		long now = System.currentTimeMillis();
		timer.schedule(new LambdaTimerTask(() -> {
			log.info("task alpha at {}", System.currentTimeMillis() - now);
		}), 500);

		timer.schedule(new LambdaTimerTask(() -> {
			log.info("task beta at {}", System.currentTimeMillis() - now);
		}), 500);

		Thread.sleep(1000);
		timer.cancel();
	}

	@Test
	public void testMultipleTasksWithDelay() throws Exception {
		Timer timer = new Timer("test_timer");

		long now = System.currentTimeMillis();
		timer.schedule(new LambdaTimerTask(() -> {
			log.info("task alpha at {}", System.currentTimeMillis() - now);
			MiscUtils.easySleep(200);
			log.info("task alpha finished at {}", System.currentTimeMillis() - now);
		}), 500);

		timer.schedule(new LambdaTimerTask(() -> {
			log.info("task beta at {}", System.currentTimeMillis() - now);
		}), 500);

		Thread.sleep(1000);
		timer.cancel();
	}
	
	
}
