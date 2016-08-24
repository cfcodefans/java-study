package cf.study.java8.javax.management;

import misc.MiscUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by fan on 2016/7/23.
 */
public class MBeanTests implements MBeanTestsMBean {

	private static MBeanServer mbs = null;

	@BeforeClass
	public static void setUpClass() {
		mbs = ManagementFactory.getPlatformMBeanServer();

		ObjectName adapterName = null;

		ObjectName helleWorldName = null;

		try {
			helleWorldName = new ObjectName("cf.study.java8.javax.management:name=MBeanTests");
			mbs.registerMBean(new MBeanTests(), helleWorldName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MBeanTests() {

	}

	public static void main(String[] args) {
		System.out.println("HelloAgent is running");
		setUpClass();

		Thread t = new Thread(() -> {
			while (true) MiscUtils.easySleep(1000);
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMain() {
		System.out.println("HelloAgent is running");

		Thread t = new Thread(() -> {
			while (true) MiscUtils.easySleep(1000);
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
