package cf.study.java8.lang;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

public class ProcessTests {

	@Test
	public void testClassPath() {
		System.out.println(SystemUtils.JAVA_CLASS_PATH);
		String[] classPaths = SystemUtils.JAVA_CLASS_PATH.split(SystemUtils.PATH_SEPARATOR);
		System.out.println(StringUtils.join(classPaths, "\n"));
	}
	
	public static class JavaProcBuilder {
		
	}
	
	public static class ProcProxy {
		private Process proc;
	}
	
	public static class ProcWorker {
		private String delimiter;
		
		protected void mainLoop() {
			
		}
		
		public static void main(String[] args) {
//			Comm
		}
	}
}
