package cf.study.java.lang.reflect;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

public class JarTests {
	static class Inner{
		Inner() {
			new Private();
		}
		private class Private{
			{
				System.out.println(this.getClass().getName());
			}
			private String powerof2(int num){
				return ((num&num-1)==0)?"power of 2":"not a power of 2";
			}
		}
	}//end of Inner

	@Test
	public void getPrivateInnerClass() throws Exception {
//		System.getSecurityManager().

		final SecurityManager securityManager = new SecurityManager() {
			@Override
			public void checkPermission(Permission permission) {
				System.out.println(permission);
			}
		};
//		System.setSecurityManager(securityManager);

		Class pcls = Class.forName(Inner.class.getName() + "$Private");
		Constructor[] constructors = pcls.getDeclaredConstructors();
		Assert.assertTrue(ArrayUtils.isNotEmpty(constructors));
		Constructor c = constructors[0];
		c.setAccessible(true);
		Object obj = c.newInstance(new Inner());
		Assert.assertNotNull(obj);
		Method powerof2 = pcls.getDeclaredMethod("powerof2", int.class);
		powerof2.setAccessible(true);
	}


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
