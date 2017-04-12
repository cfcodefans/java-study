package cf.study.misc.system;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;

import sun.management.VMManagement;

public class FindJavaProcessId {

	public static void main(String[] args) {

		try {
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			Field jvmField = runtimeMXBean.getClass().getDeclaredField("jvm");
			jvmField.setAccessible(true);
			VMManagement vmManagement = (VMManagement) jvmField.get(runtimeMXBean);
			Method getProcessIdMethod = vmManagement.getClass().getDeclaredMethod("getProcessId");
			getProcessIdMethod.setAccessible(true);
			Integer processId = (Integer) getProcessIdMethod.invoke(vmManagement);
			System.out.println("################    ProcessId = " + processId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExec() {
		String line = "dir -a " + Paths.get(".").toAbsolutePath();
		CommandLine cmdLine = new CommandLine();
		DefaultExecutor executor = new DefaultExecutor();
		int exitValue = executor.execute(cmdLine);
	}

}
