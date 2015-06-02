package cf.study.java8.nio;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class PathsTests {

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

	@Test
	public void testPath() {
		Path _p = Paths.get(".");
		System.out.println(_p.toAbsolutePath());
		System.out.println(_p.normalize().toAbsolutePath());
		System.out.println(_p.isAbsolute());
		System.out.println(_p.getFileName());
		System.out.println(_p.toAbsolutePath().getFileName());
	}

	@Test
	public void testRes() throws Exception {
		URL resUrl = PathsTests.class.getResource(".");
		Path rp = Paths.get(resUrl.toURI());
		System.out.println(rp);
		
		
		resUrl = ClassLoader.getSystemResource(".");
		rp = Paths.get(resUrl.toURI());
		System.out.println(rp);
		
		System.out.println();
		Path metaInf = rp.resolve("META-INF");
		System.out.println(metaInf);
		
		metaInf.iterator().forEachRemaining(System.out::println);
	}
}
