package cf.study.java.lang;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

@SuppressWarnings("unused")
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
	
	@Test
	public void testEcho() throws Exception {
		ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "echo 'echo'");
		Process proc = pb.start();
		IOUtils.copy(proc.getInputStream(), System.out);
		System.out.flush();
	}
	
	@Test
	public void testRedirect() throws Exception {
		ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "echo 'echo'");
		Process proc = pb.start();
		File file = Paths.get("./test/redirect").toFile();
		if (!file.exists()) {
			file.createNewFile();
		}
		System.out.println(FileUtils.readFileToString(pb.redirectInput(file).redirectInput().file()));
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
