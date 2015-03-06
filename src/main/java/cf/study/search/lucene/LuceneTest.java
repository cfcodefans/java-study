package cf.study.search.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import misc.MiscUtils;
import misc.ProcTrace;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LuceneTest {

	static StandardAnalyzer analyzer;
	static Directory dir;
	static IndexWriterConfig iwc;
	static IndexWriter writer;
	
	
	@BeforeClass
	public static void setUp() throws Exception {
		ProcTrace.start(MiscUtils.invocationInfo());
		
		analyzer = new StandardAnalyzer();
		dir = FSDirectory.open(Paths.get("test/data/lucene/index"));
		iwc = new IndexWriterConfig(analyzer);
		
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		writer = new IndexWriter(dir, iwc);
		
		ProcTrace.ongoing("initialization finished");
	}
	
	void indexDoc(IndexWriter writer, Path file) throws IOException {
		ProcTrace.start(String.format("indexDoc(%s)", file));
		
		try (BufferedReader br = Files.newBufferedReader(file)) {
			//make a new, empty document
			Document doc = new Document();
			
			Field pathFd = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathFd);
			
			doc.add(new LongField("modified", Files.getLastModifiedTime(file).toMillis(), Field.Store.YES));
			
			doc.add(new LongField("size", Files.size(file), Field.Store.YES));
			
			doc.add(new TextField("content", IOUtils.toString(br), Field.Store.YES));
			
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				System.out.println("adding " + file);
				writer.addDocument(doc);
			} else {
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path"), doc);
			}
		}
		
		ProcTrace.end();
	}
	
	@Test
	public void testIndexSources() throws Exception {
		Path path = Paths.get("src/main/java");
		Assert.assertTrue(Files.isDirectory(path));
		
		ProcTrace.start();
		
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				indexDoc(writer, path);
				return FileVisitResult.CONTINUE;
			}
		});
		
		ProcTrace.end();
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		ProcTrace.ongoing("start cleaning");
		
		if (writer != null) {
			writer.close();
		}
		
		if (dir != null) {
			dir.close();
		}
		
		if (analyzer != null) {
			analyzer.close();
		}
		
		ProcTrace.end();
		log.info("\n" + ProcTrace.flush());
	}

	private static final Logger log = Logger.getLogger(LuceneTest.class);
}
