package cf.study.search.lucene;

import static cf.study.search.lucene.LuceneHelper.indexDocs;
import static cf.study.search.lucene.LuceneHelper.makeDoc;
import static cf.study.search.lucene.LuceneHelper.print;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import misc.MiscUtils;
import misc.ProcTrace;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryTests {
	static StandardAnalyzer analyzer;
	static Directory dir;
	static IndexWriterConfig iwc;
	static IndexWriter writer;
	
//	static IndexReader reader;
//	static IndexSearcher searcher;
	static AtomicLong SEQ;
	
	@BeforeClass
	public static void setUp() throws Exception {
		ProcTrace.start(MiscUtils.invocationInfo());
		
		analyzer = new StandardAnalyzer();
		dir = new RAMDirectory();
		iwc = new IndexWriterConfig(analyzer);
		
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		writer = new IndexWriter(dir, iwc);
		//before open the index by using a reader, call once writer.commit()
		writer.commit();
		
//		reader = DirectoryReader.open(dir);
//		searcher = new IndexSearcher(reader);
		
		SEQ = new AtomicLong(0);
		
		ProcTrace.ongoing("initialization finished");
	}
	
	@Test
	public void testQueryParser() throws Exception {
		prepareTestIndex();
		
//		reader.close();
		
		try (IndexReader reader = DirectoryReader.open(dir)) {
			System.out.println("reader.numDocs(): " + reader.numDocs());
			Assert.assertTrue(reader.numDocs() > 0);
			
			//query by QueryParser
			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser parser = new QueryParser("sn", analyzer);
			
			{
				String queryStr = "month:\"May\"";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "month:\"May\" AND ordinal: 5";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:\"Wednesday\"";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:\"*nes*\"";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:W*nes*";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				//this can't work because the question mark "?" is enclosed in quote, so it is taken as a "?"
				//instead of representing a placeholder
				String queryStr = "day:\"W?dnesday\"";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:W?dnesday";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:\"*day\"";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:(\"*day\")";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:(NOT Monday)";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:(Monday)";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:(+/.*/ -Monday)";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:(+/.*sday/)";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:(+/.*day/ -\"*day\")";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:Monday AND month:February";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:Monday OR month:February";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "days:28";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "days:[28 TO 28]";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "days:[28 TO 31]";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "month:member~";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:Monday~6";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:Monday~1";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "day:Monday~2";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
			{
				String queryStr = "month:December~10";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
				
//				System.out.println(StringUtils.difference("December", "November"));
			}
			
			{
				String queryStr = "proximity:\"one ten\"~5";
				System.out.println();
				System.out.println(queryStr);
				print(searcher, searcher.search(parser.parse(queryStr), Integer.MAX_VALUE));
			}
			
		}
	}
	
	@Test
	public void testQueryBuilder() throws Exception {
		prepareTestIndex();
		
		try (IndexReader reader = DirectoryReader.open(dir)) {
			System.out.println("reader.numDocs(): " + reader.numDocs());
			Assert.assertTrue(reader.numDocs() > 0);

			IndexSearcher searcher = new IndexSearcher(reader);
			
			{
				QueryBuilder qb = new QueryBuilder(analyzer);
				Query booleanQuery = qb.createBooleanQuery("month", "January");
				System.out.println();
				System.out.println("qb.createBooleanQuery(\"month\", \"January\")");
				print(searcher, searcher.search(booleanQuery, Integer.MAX_VALUE));
			}
		}
	}

	private void prepareTestIndex() throws Exception, IOException {
		indexDocs(writer,
				makeDoc("day", "*day", "ordinal", 0),
				makeDoc("day", "Monday", "ordinal", 1),
				makeDoc("day", "Tuesday", "ordinal", 2),
				makeDoc("day", "Wednesday", "ordinal", 3),
				makeDoc("day", "Thursday", "ordinal", 4),
				makeDoc("day", "Friday", "ordinal", 5),
				makeDoc("day", "Saturday", "ordinal", 6),
				makeDoc("day", "Sunday", "ordinal", 7));
		
		indexDocs(writer, makeDoc("month", "January", "ordinal", 1, "days", 31),
				makeDoc("month", "February", "ordinal", 2, "days", 28),
				makeDoc("month", "March", "ordinal", 3, "days", 31),
				makeDoc("month", "April", "ordinal", 4, "days", 30),
				makeDoc("month", "May", "ordinal", 5, "days", 31),
				makeDoc("month", "June", "ordinal", 6, "days", 30),
				makeDoc("month", "July", "ordinal", 7, "days", 31),
				makeDoc("month", "August", "ordinal", 8, "days", 31),
				makeDoc("month", "September", "ordinal", 9, "days", 30),
				makeDoc("month", "Octorber", "ordinal", 10, "days", 31),
				makeDoc("month", "November", "ordinal", 11, "days", 30),
				makeDoc("month", "December", "ordinal", 12, "days", 31));
		
		indexDocs(writer, makeDoc("season", "Spring", "ordinal", 1),
				makeDoc("season", "Summer", "ordinal", 2),
				makeDoc("season", "Autumn", "ordinal", 3),
				makeDoc("season", "Winter", "ordinal", 4));
		
		indexDocs(writer, makeDoc("direction", "East", "ordinal", 1),
				makeDoc("direction", "West", "ordinal", 2),
				makeDoc("direction", "South", "ordinal", 3),
				makeDoc("direction", "North", "ordinal", 4));

		indexDocs(writer, makeDoc("gender", "Male", "ordinal", 1),
				makeDoc("gender", "Female", "ordinal", 2));
		
		indexDocs(writer, makeDoc("proximity", "one two three four five six seven eight nine ten"));
		indexDocs(writer, makeDoc("proximity", "one two three four five six seven eight ten"));
		indexDocs(writer, makeDoc("proximity", "one two three four five six seven ten"));
		indexDocs(writer, makeDoc("proximity", "one two three four five six ten"));
		indexDocs(writer, makeDoc("proximity", "one two three four five ten"));
		indexDocs(writer, makeDoc("proximity", "one two three four ten"));
		indexDocs(writer, makeDoc("proximity", "one two three ten"));
		indexDocs(writer, makeDoc("proximity", "one two ten"));
		indexDocs(writer, makeDoc("proximity", "one ten"));
		
		
		
		writer.commit();
		writer.forceMerge(Integer.MAX_VALUE, true);
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		ProcTrace.ongoing("start cleaning");
		
		if (writer != null) {
			writer.close();
		}

//		if (reader != null) {
//			reader.close();
//		}
		
		if (dir != null) {
			dir.close();
		}
		
		if (analyzer != null) {
			analyzer.close();
		}
		
		ProcTrace.end();
		log.info("\n" + ProcTrace.flush());
	}

	private static final Logger log = Logger.getLogger(QueryTests.class);
}
