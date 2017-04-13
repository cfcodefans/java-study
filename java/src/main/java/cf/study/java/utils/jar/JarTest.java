package cf.study.java.utils.jar;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

public class JarTest {

	@Test
	public void getClassPaths() {
		System.out.println(SystemUtils.JAVA_CLASS_PATH.replace(';', '\n'));
		System.out.println(SystemUtils.JAVA_LIBRARY_PATH.replace(';', '\n'));
		System.out.println(SystemUtils.JAVA_HOME);
	}

	@Test
	public void loadRuntimeJar() throws Exception {
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));

		Assert.assertTrue(_f.exists());
		Assert.assertTrue(_f.isFile());

		try (JarFile jf = new JarFile(_f)) {
			System.out.println(String.format("entries: %d", jf.size()));
			try (Stream<JarEntry> entryStream = jf.stream()) {
				entryStream.forEach((je) -> {
					System.out.println(je);
				});
			}
		}
	}

	@Test
	public void readContentFromJar() throws Exception {
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));

		Assert.assertTrue(_f.exists());
		Assert.assertTrue(_f.isFile());

		try (JarFile jf = new JarFile(_f)) {
			JarEntry manifestEntry = jf.getJarEntry("META-INF/MANIFEST.MF");
			try (InputStream is = jf.getInputStream(manifestEntry)) {
				System.out.println(IOUtils.toString(is));
			}
		}
	}
}
