package cf.study.jmx.egs.helloworld;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import com.sun.jdmk.comm.HtmlAdaptorServer;

public class HelloAgent {
	private MBeanServer mbs = null;

	public HelloAgent() {
		mbs = MBeanServerFactory.createMBeanServer("HelloAgent");

		HtmlAdaptorServer adapter = new HtmlAdaptorServer();

		HelloWorld hw = new HelloWorld();

		ObjectName adapterName = null;

		ObjectName helleWorldName = null;

		try {
			helleWorldName = new ObjectName("HelloAgent:name=helloWorld1");
			mbs.registerMBean(hw, helleWorldName);

			adapterName = new ObjectName("HelloAgent:name=htmladapter, port=9092");
			adapter.setPort(9092);
			mbs.registerMBean(adapter, adapterName);
			adapter.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("HelloAgent is running");
		HelloAgent agent = new HelloAgent();
	}
}
