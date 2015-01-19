package cf.study.java8.lang.reflect;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

public class ClassLoaderTest {
	@Test
	public void loadClassesFromRuntimeJar() throws Exception {
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));

		Assert.assertTrue(_f.exists());
		Assert.assertTrue(_f.isFile());

		ClassLoader cl = ClassLoader.getSystemClassLoader();

		List<Class<?>> clsList = new LinkedList<Class<?>>();
		try (JarFile jf = new JarFile(_f)) {
			System.out.println(String.format("entries: %d", jf.size()));

			try (Stream<JarEntry> entryStream = jf.stream()) {
				entryStream.parallel().map((je) -> {
					try {
						return cl.loadClass(StringUtils.removeEnd(je.getName().replace('/', '.'), ".class"));
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
					return null;
				}).forEach((cls) -> {
					if (cls != null) {
						clsList.add(cls);
					}
				});

			}
		}

		System.out.println(clsList.size());
	}
}
