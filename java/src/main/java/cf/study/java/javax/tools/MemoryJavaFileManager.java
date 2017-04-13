package cf.study.java.javax.tools;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * JavaFileManager that keeps compiled .class bytes in memory.
 * 
 * @author fan
 *
 */
public class MemoryJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	
	/**
	 * Java source file extension
	 */
	private final static String EXT = ".java";
	
	private Map<String, byte[]> classBytes;

	protected MemoryJavaFileManager(StandardJavaFileManager fileManager) {
		super(fileManager);
		classBytes = new ConcurrentHashMap<>();
	}

	public Map<String, byte[]> getClassBytes() {
		return classBytes;
	}
	
	public void close() throws IOException {
		classBytes = null;
	}
	
	public void flush() throws IOException {
		
	}

	/**
	 *	A file object used to represent Java source coming from a string.
	 */
	private static class StringInputBuffer extends SimpleJavaFileObject {
		final String source;
		
		StringInputBuffer(String filename, String source) {
			super(toURI(filename), Kind.SOURCE);
			this.source = source;
		}
		
		public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
			return CharBuffer.wrap(source);
		}
	}
	
	private class ClassOutputBuffer extends SimpleJavaFileObject {
		private String name;
		ClassOutputBuffer(String name) {
			super(toURI(name), Kind.CLASS);
			this.name = name;
		}
		
		public OutputStream openOutputStream() {
			return new FilterOutputStream(new ByteArrayOutputStream() {
				public void close() throws IOException {
//					out.close();
//					ByteArrayOutputStream bos = (ByteArrayOutputStream)out;
					classBytes.put(name, toByteArray());
				}
			});
		}
	}
	
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String clsName, Kind kind, FileObject sibling) throws IOException {
		if (kind == Kind.CLASS) 
			return new ClassOutputBuffer(clsName);
		
		return super.getJavaFileForOutput(location, clsName, kind, sibling);
	}
	
	static JavaFileObject makeStringSource(String filename, String source) {
		return new StringInputBuffer(filename, source);
	}
	
	static URI toURI(String name) {
		Path path = Paths.get(name);
		if (Files.exists(path)) {
			return path.toUri();
		}
		
		try {
			final StringBuilder newUri = new StringBuilder();
			newUri.append("mfm:///");
			newUri.append(name.replace('.', '/'));
			if (name.endsWith(EXT))
				newUri.replace(newUri.length() - EXT.length(), newUri.length(), EXT);
			return URI.create(newUri.toString());
		} catch (Exception e) {
			// TODO: handle exception
			return URI.create("mfm:///com/sun/script/java/java_source");
		}
	}
}
