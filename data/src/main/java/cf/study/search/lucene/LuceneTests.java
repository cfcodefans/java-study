package cf.study.search.lucene;

import misc.MiscUtils;
import misc.ProcTrace;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

public class LuceneTests {

	private static final Path PATH = Paths.get("test/data/lucene/index/this");
	static StandardAnalyzer analyzer;
	static Directory dir;
	static IndexWriterConfig iwc;
	static IndexWriter writer;
	
	static IndexReader reader;
	static IndexSearcher searcher;
	static AtomicLong SEQ;
	
	@BeforeClass
	public static void setUp() throws Exception {
		ProcTrace.start(MiscUtils.invocationInfo());
		
		analyzer = new StandardAnalyzer();
		dir = FSDirectory.open(PATH);
		iwc = new IndexWriterConfig(analyzer);
		
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		writer = new IndexWriter(dir, iwc);
		
		reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
		
		SEQ = new AtomicLong(0);
		
		ProcTrace.ongoing("initialization finished");
	}
	
	void indexDoc(IndexWriter writer, Path file) throws IOException {
		ProcTrace.start(String.format("indexDoc(%s)", file));
		
		try (BufferedReader br = Files.newBufferedReader(file)) {
			//make a new, empty document
			Document doc = new Document();
			
			doc.add(new NumericDocValuesField("sn", SEQ.getAndIncrement()));
			
			doc.add(new TextField("path", file.toString(), Field.Store.YES));
			
			doc.add(new NumericDocValuesField("modified", Files.getLastModifiedTime(file).toMillis()));

			NumericDocValuesField sizeFd = new NumericDocValuesField("size", Files.size(file));
//          this FieldType is already frozen and cannot be changed
//			sizeFd.fieldType().setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			doc.add(sizeFd);
			
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
	
	@Test
	public void testQueryParser_asterisk() throws Exception {
		QueryParser queryParser = new QueryParser("path", analyzer);
		String queryStr = "path:\"*Test*\"";
		System.out.println("\n" + queryStr);
		Query query = queryParser.parse(queryStr);
		TopDocs results = searcher.search(query, Integer.MAX_VALUE);
		LuceneHelper.print(searcher, results);
	}
	
	@Test
	public void testQueryParser_question_mark() throws Exception {
		QueryParser queryParser = new QueryParser("path", analyzer);
		String queryStr = "path:\"src\\main\\java\\cf\\study\\java\\nio\\WatchTest.java\"";
		System.out.println("\n" + queryStr);
		Query query = queryParser.parse(queryStr);
		TopDocs results = searcher.search(query, Integer.MAX_VALUE);
		LuceneHelper.print(searcher, results);
	}

	@Test
	public void cleanIndex() {
		File file = PATH.toFile();
		System.out.println(String.format("%s is deleteable: %B", PATH, file.canWrite()));
	    try {
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		ProcTrace.ongoing("start cleaning");
		
		if (writer != null) {
			writer.close();
		}

		if (reader != null) {
			reader.close();
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

	private static final Logger log = Logger.getLogger(LuceneTests.class);
}
