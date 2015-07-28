package cf.study.jdk7.lang;

import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import cf.study.utils.MiscUtils;

public class ThreadTest {

	@Test
	public void testJoin() {
		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.currentThread().sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(MiscUtils.invocationInfo());
			}
		});
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(MiscUtils.invocationInfo());
	}
	
	@Test
	public void testInterrupt() {
		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					for (int i = 10; i > 0; i--) {
						Thread.currentThread().sleep(2000);
						System.out.println(i + "\t" + Thread.interrupted());
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					System.out.println(MiscUtils.invocationInfo());
				}
				System.out.println(MiscUtils.invocationInfo() + " end");
			}
		});
		th.setDaemon(true);
		th.start();
		
		try {
			Thread.currentThread().sleep(4000);
			th.interrupt();
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testFinalizerInThread() {
//		final Base b = new Base();
		Thread th = new Thread(new Runnable() {
			public void run() {
				Base b = new Base();
				//b = null;
				System.out.println(MiscUtils.invocationInfo());
			}
		});
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.gc();
		System.out.println(MiscUtils.invocationInfo());
	}
	
	@Test
	public void testSleep() {
		for (;;) {
			try {
				Thread.currentThread().sleep(RandomUtils.nextInt() % (RandomUtils.nextInt() % 3000));
				final String randomAlphabetic = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt() % 80);
				
				System.out.println(String.format("%s %s %s\t %s", new Date(), Thread.currentThread().getName(), MiscUtils.invocationInfo(), randomAlphabetic));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
