package cf.study.java8.lang.reflect;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.xbean.classloader.JarFileClassLoader;
import org.junit.Test;

public class Reflects {

	public static List<Class<?>> loadClzzFromJar(File jarFile, ClassLoader defaultLoader) throws Exception {
		List<Class<?>> clsList = new LinkedList<Class<?>>();
		if (jarFile == null || !jarFile.exists() || !jarFile.isFile() || !jarFile.canRead()) {
			return clsList;
		}

		JarFileClassLoader cl = new JarFileClassLoader("jfcl", new URL[] { jarFile.toURI().toURL() }, defaultLoader);

		try {
			try (JarFile jf = new JarFile(jarFile)) {
				System.out.println(String.format("entries: %d", jf.size()));

				try (Stream<JarEntry> entryStream = jf.stream()) {
					entryStream.filter((je) -> {
						return !je.isDirectory();
					}).map((je) -> {
						String clsName = StringUtils.removeEnd(je.getName().replace('/', '.'), ".class");
						try {
							return cl.loadClass(clsName);
						} catch (Exception e) {
							System.err.println(clsName + ": " + e.getMessage());
						}
						return null;
					}).forEach((cls) -> {
						if (cls != null) {
							clsList.add(cls);
						}
					});

				}
			}
		} finally {
			cl.close();
		}

		return clsList;
	}
	
	public static File getJarFileInClassPath(String libName) {
		if (StringUtils.isBlank(libName)) return null;
		
		String[] classPaths = StringUtils.split(SystemUtils.JAVA_CLASS_PATH, ';');

		Optional<String> opt = Stream.of(classPaths).filter((String str) -> {
			return str.contains(libName);
		}).findFirst();
		
		return opt.isPresent() ? new File(opt.get()) : null;
	}

	@Test
	public void test() throws Exception {
		String[] classPaths = StringUtils.split(SystemUtils.JAVA_CLASS_PATH, ';');

		Optional<String> opt = Stream.of(classPaths).filter((String str) -> {
			return str.contains("junit");
		}).findFirst();
		Assert.assertTrue(opt.isPresent());

		List<Class<?>> re = loadClzzFromJar(new File(opt.get()), ClassLoader.getSystemClassLoader());
		System.out.println(StringUtils.join(re, '\n'));
		System.out.println(re.size());
	}
}
