package cf.study.java8.nio;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class FilesTests {
	private static final Logger log = LogManager.getLogger(FilesTests.class);

	public static class FileMatcher implements FileVisitor<Path> {
		public final List<Path> results = new LinkedList<Path>();
		private final Pattern fileNamePattern;

		public FileMatcher(String patternStr) {
			fileNamePattern = Pattern.compile(patternStr);
		}

		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			// log.info(dir);
			return FileVisitResult.CONTINUE;
		}

		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			// log.info(file);
			if (fileNamePattern.matcher(file.toFile().getName()).find()) {
				results.add(file);
			}
			return FileVisitResult.CONTINUE;
		}

		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}
	}

	@Test
	public void testWalker() throws Exception {
		FileMatcher matcher = new FileMatcher(".*\\.java");
		Files.walkFileTree(Paths.get(".", "src", "main", "java"), matcher);
		log.info(StringUtils.join(matcher.results, '\n'));
		log.info(String.format("%d java files found", matcher.results.size()));

		matcher = new FileMatcher(".*\\.class");
		Files.walkFileTree(Paths.get(".", "target", "test-classes"), matcher);
		log.info(StringUtils.join(matcher.results, '\n'));
		log.info(String.format("%d class files found", matcher.results.size()));
		
	}

	@Test
	public void testPattern() throws Exception {
		log.info("AnnotationTests.java".matches(".*\\.java"));
	}

}
