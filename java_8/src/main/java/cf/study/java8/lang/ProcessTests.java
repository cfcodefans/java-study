package cf.study.java8.lang;

import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
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
	
	@Test
	public void testCallPython() throws Exception {
		ProcessBuilder pb = new ProcessBuilder();
		Process proc = pb.command("python2.7", "--version").start();
		IOUtils.copy(proc.getInputStream(), System.out);
		System.out.flush();
	}
	
	@Test
	public void testEnvVars() throws Exception {
		ProcessBuilder pb = new ProcessBuilder();
		Map<String, String> envVars = pb.environment();
		envVars.entrySet().forEach(System.out::println);
		
		String path = envVars.get("Path");
		Stream.of(StringUtils.split(path, ';')).map(String::trim).forEach(System.out::println);
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
		
		public static void main(String[] args) throws Exception {
			CommandLine cl = new PosixParser().parse(null, args);
		}
	}
}
