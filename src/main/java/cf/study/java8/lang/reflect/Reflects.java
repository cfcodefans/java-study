package cf.study.java8.lang.reflect;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.xbean.classloader.JarFileClassLoader;
import org.junit.Test;

import cf.study.java8.javax.persistence.ex.reflects.entity.PackageEn;

public class Reflects {

	public static Class<?> preloadClazzByName(String name) {
		if (StringUtils.isBlank(name)) return null;
		try {
			return Class.forName(name, false, ClassLoader.getSystemClassLoader());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Class<?>> extractClazz(final File f) {
		List<Class<?>> clzList = new LinkedList<Class<?>>();
		Consumer<JarEntry> visitor = (JarEntry je) -> {
			String enName = je.getName();
			
			if (StringUtils.endsWith(enName, "/")) {
				enName = StringUtils.removeEnd(enName, "/").replace('/', '.');
				return;
			}
			
			if (StringUtils.endsWith(enName, ".class")) {
				enName = enName.replace('/', '.');
				clzList.add(preloadClazzByName(StringUtils.substringBeforeLast(enName, ".class")));
			}
		};
		
		Predicate<JarEntry> filter = (je) -> {
			return !(je.isDirectory() || je.getName().contains("META-INF") || je.getName().endsWith("package-info"));
		};
		
		try {
			extractJarStructure(f, filter, visitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return clzList;
	}
	
	public static void extractJarStructure(final File f, final Predicate<JarEntry> filter, final Consumer<JarEntry> visitor) throws MalformedURLException, IOException {
		if (f == null || !f.exists() || !f.isFile() || !f.canRead()) {
			return;
		}
		
		try (JarFileClassLoader cl = new JarFileClassLoader("jfcl", new URL[] { f.toURI().toURL() }, ClassLoader.getSystemClassLoader())) {
			try (JarFile jf = new JarFile(f)) {
				System.out.println(String.format("entries: %d", jf.size()));
				try (Stream<JarEntry> entryStream = jf.stream()) {
					Stream<JarEntry> _stream = entryStream;
					if (filter != null) {
						_stream = _stream.filter(filter);
					}
					if (visitor != null) {
						_stream.forEach(visitor);
					}
				}
			}
		}
	}
	
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
