package cf.study.jmx.egs.ibm;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

public class Main {
	private static ObjectName objectName;
	private static MBeanServer mBeanServer;

	public static void main(String[] args) throws Exception {
		init();
		manage();
	}

	private static void init() throws Exception {
		ServerImpl serverImpl = new ServerImpl();
		ServerMonitorMBean serverMonitor = new ServerMonitor(serverImpl);
		mBeanServer = MBeanServerFactory.createMBeanServer();
		objectName = new ObjectName("objectName:id=ServerMonitor1");
		mBeanServer.registerMBean(serverMonitor, objectName);
	}

	private static void manage() throws Exception {
		System.out.println(mBeanServer.getObjectInstance(objectName));
		
		MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objectName);
		System.out.println(mBeanInfo);
		
		Long upTime = (Long) mBeanServer.getAttribute(objectName, "upTime");
		System.out.println(upTime);
	}
}
