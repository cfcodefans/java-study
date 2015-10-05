package cf.study.data.storage.lucene;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
//import org.apache.lucene.document.Field;

public class HelloLucene {
	private static void addDoc(IndexWriter w, String value) throws IOException {
		Document doc = new Document();
		doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
		w.addDocument(doc);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, ParseException {
		//0. Specify the analyzer for kokenizing text.
		//	The same analyzer should be used for indexing and searching
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);
		
		// 1. create the index
		Directory index = new RAMDirectory();
		
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34, analyzer);
		
		IndexWriter w = new IndexWriter(index, config);
		addDoc(w, "Lucene in Action");
		addDoc(w, "Lucene for Dummies");
		addDoc(w, "Managing Gigabytes");
		addDoc(w, "The Art of Computer Science");
		
		w.close();
		
		System.out.println(StringUtils.join(index.listAll(), "\n")); 
		
		// 2. query
		String queryStr = args.length > 0 ? args[0] : "lucene";
		
		// the "title" arg specifies the default field to use
		// when no field is explicitly specified in the query.
		Query q = new QueryParser(Version.LUCENE_34, "title", analyzer).parse(queryStr);
		
		// 3. search
		int hitsPerPage = 10;
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		// 4. Display results
		System.out.println("\n" + queryStr + " Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i+1) + "." + d.get("title"));
		}
		
		// searcher can only be closed when there
		// is no need to access the documents any more
		searcher.close();
	}

	@Test
	public void test() throws IOException, ParseException {
//		main(new String[] {"Dummies"});
		main(new String[] {"title:Art"});
		main(new String[] {"title:Lucene AND (NOT title:for)"});
		
		main(new String[] {"Lucene Art"});
		
		main(new String[] {"+Lucene +Action"});
		main(new String[] {"-Lucene +Action"});
	}
	
}
