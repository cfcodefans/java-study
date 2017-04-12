package cf.study.jmx.egs.mxbean;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import com.sun.jdmk.comm.HtmlAdaptorServer;

public class TestAgent {
	private MBeanServer mbs = null;

	public TestAgent() {
		mbs = MBeanServerFactory.createMBeanServer("TestAgent");

		HtmlAdaptorServer adapter = new HtmlAdaptorServer();

		TestMXBean mx = new TestMXBean();

		ObjectName adapterName = null;

		ObjectName helleWorldName = null;

		try {
			helleWorldName = new ObjectName("TestAgent:name=mx1");
			mbs.registerMBean(mx, helleWorldName);

			adapterName = new ObjectName("TestAgent:name=htmladapter, port=9092");
			adapter.setPort(9092);
			mbs.registerMBean(adapter, adapterName);
			adapter.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("TestAgent is running");
		TestAgent agent = new TestAgent();
	}
}
