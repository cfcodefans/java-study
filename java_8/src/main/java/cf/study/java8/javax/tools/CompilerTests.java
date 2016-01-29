package cf.study.java8.javax.tools;

import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Map;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.Before;

public class CompilerTests {

	private JavaCompiler			compiler;

	private StandardJavaFileManager	javaSrcMgr;

	@Before
	public void setUp() {
		compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new RuntimeException("Could not get Java compiler. Pleasee ensure that JDK is used instead of JRE.");
		}
		javaSrcMgr = compiler.getStandardFileManager(null, null, null);
	}

	/**
	 * compile given String source and return bytecodes as a Map.
	 * 
	 * @param fileName
	 *            source filename to be used for error message etc.
	 * @param source
	 *            source java source as String
	 * @param err
	 *            error writer where diagnostic message are written
	 * @param srcPath
	 *            sourcePath locatoin of additional .java source files
	 * @param clsPath
	 *            classPath location of additinal .class files
	 * @return
	 * @throws Exception
	 */
	private Map<String, byte[]> compile(String fileName, String source, Writer err, String srcPath, String clsPath) throws Exception {
		// to collect errors, warnings etc.
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		// create a new memory JavaFileManager
		return null;
	}

	public Method compileStaticMethod(final String methodName, final String clsName, final String source) throws Exception {
		return null;
	}
}
