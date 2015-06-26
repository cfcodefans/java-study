package cf.study.java8.nio;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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
		
		_p = Paths.get(".", "test");
		System.out.println(_p.toAbsolutePath());
		System.out.println(_p.normalize().toAbsolutePath());
		System.out.println(_p.isAbsolute());
		System.out.println(_p.getFileName());
		System.out.println(_p.toAbsolutePath().getFileName());
		
		_p = Paths.get("./test");
		System.out.println(_p.toAbsolutePath());
		System.out.println(_p.normalize().toAbsolutePath());
		System.out.println(_p.isAbsolute());
		System.out.println(_p.getFileName());
		System.out.println(_p.toAbsolutePath().getFileName());
		
		System.out.println();
		
		_p.forEach(__p->System.out.print(__p + ">"));
		
		System.out.println();
		
		_p.toAbsolutePath().forEach(__p->System.out.print(__p + ">"));
		
		_p.toAbsolutePath().forEach(__p->System.out.println(__p.toAbsolutePath() + ">"));
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
	
	@Test
	public void testToFile() {
		File f = Paths.get("test/f").toAbsolutePath().toFile();
		File _f = Paths.get("test/f").toFile();
		
		System.out.println(f);
		System.out.println(_f);
		System.out.println(Objects.equals(f, _f));
	}
}
