package cf.study.java8.nio;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class PathsTest {

	@Test
	public void testPaths() {
		Path p = Paths.get("folder1", "folder2");
		System.out.println(p);

		p = Paths.get("folder1/", "folder2");
		System.out.println(p);

		p = Paths.get("folder1", "/folder2");
		System.out.println(p);

		p = Paths.get("folder1/", "/folder2");
		System.out.println(p);

		URI uri = URI.create("folder1");
		System.out.println(uri);
	}
}
