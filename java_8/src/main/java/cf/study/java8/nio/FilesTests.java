package cf.study.java8.nio;

import java.io.File;
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
import org.junit.Test;

public class FilesTests {

	public static class FileMatcher implements FileVisitor<Path> {
		public final List<Path> results = new LinkedList<Path>(); 
		private final Pattern fileNamePattern; 
		
		public FileMatcher(String patternStr) {
			fileNamePattern = Pattern.compile(patternStr);
		}
		
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//			System.out.println(dir);
			return FileVisitResult.CONTINUE;
		}

		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//			System.out.println(file);
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
		System.out.println(StringUtils.join(matcher.results, '\n'));
		System.out.println(String.format("%d java files found", matcher.results.size()));
		
		matcher = new FileMatcher(".*\\.class");
		Files.walkFileTree(Paths.get(".", "target", "test-classes"), matcher);
		System.out.println(StringUtils.join(matcher.results, '\n'));
		System.out.println(String.format("%d class files found", matcher.results.size()));
	}
	
	@Test
	public void testPattern() throws Exception {
		System.out.println("AnnotationTests.java".matches(".*\\.java"));
	}
	
}
