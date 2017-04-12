package cf.study.java8.nio;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class PathsTests {

	private static final Logger log = LogManager.getLogger(PathsTests.class);
	
	@Test
	public void testPaths() {
		Path p = Paths.get("folder1", "folder2");
		log.info(p);

		p = Paths.get("folder1/", "folder2");
		log.info(p);

		p = Paths.get("folder1", "/folder2");
		log.info(p);

		p = Paths.get("folder1/", "/folder2");
		log.info(p);

		URI uri = URI.create("folder1");
		log.info(uri);
	}
	
	@Test
	public void testRelatize() {
		Path p1 = Paths.get("/a/b/c/d/e");
		Path p2 = Paths.get("/a/b/c");
		
		log.info(p2.relativize(p1));
		log.info(p1.relativize(p2));
	}

	@Test
	public void testPath() {
		Path _p = Paths.get(".");
		log.info(_p.toAbsolutePath());
		log.info(_p.normalize().toAbsolutePath());
		log.info(_p.isAbsolute());
		log.info(_p.getFileName());
		log.info(_p.toAbsolutePath().getFileName());
		
		_p = Paths.get(".", "test");
		log.info(_p.toAbsolutePath());
		log.info(_p.normalize().toAbsolutePath());
		log.info(_p.isAbsolute());
		log.info(_p.getFileName());
		log.info(_p.toAbsolutePath().getFileName());
		
		_p = Paths.get("./test");
		log.info(_p.toAbsolutePath());
		log.info(_p.normalize().toAbsolutePath());
		log.info(_p.isAbsolute());
		log.info(_p.getFileName());
		log.info(_p.toAbsolutePath().getFileName());
		
		
		_p.forEach(__p->log.info(__p + ">"));
		
		
		_p.toAbsolutePath().forEach(__p->log.info(__p + ">"));
		
		_p.toAbsolutePath().forEach(__p->log.info(__p.toAbsolutePath() + ">"));
	}

	@Test
	public void testRes() throws Exception {
		URL resUrl = PathsTests.class.getResource(".");
		Path rp = Paths.get(resUrl.toURI());
		log.info(rp);
		
		
		resUrl = ClassLoader.getSystemResource(".");
		rp = Paths.get(resUrl.toURI());
		log.info(rp);
		
		Path metaInf = rp.resolve("META-INF");
		log.info(metaInf);
		
		metaInf.iterator().forEachRemaining(System.out::println);
	}
	
	@Test
	public void testToFile() {
		File f = Paths.get("test/f").toAbsolutePath().toFile();
		File _f = Paths.get("test/f").toFile();
		
		log.info(f);
		log.info(_f);
		log.info(Objects.equals(f, _f));
	}
	
}
