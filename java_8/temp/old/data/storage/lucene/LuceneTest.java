package cf.study.data.storage.lucene;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class LuceneTest {
	StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);
	
	Directory index = new RAMDirectory();
	
	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34, analyzer);
	
	IndexWriter writer = null;
	
	//@Before
	public void setup() throws Exception {
		writer = new IndexWriter(index, config);
		addDoc(getObjMap(new Article("Michael McCandless", "Lucene in Action, Second Edition: Covers Apache Lucene 3.0", "When Lucene first hit the scene five years ago, it was nothing short of amazing. By using this open-source, highly scalable, super-fast search engine, developers could integrate search into applications quickly and efficiently. A lot has changed since then-search has grown from a \"nice-to-have\" feature into an indispensable part of most enterprise applications. Lucene now powers search in diverse companies including Akamai, Netflix, LinkedIn, Technorati, HotJobs, Epiphany, FedEx, Mayo Clinic, MIT, New Scientist Magazine, and many others.")));
		addDoc(getObjMap(new Article("David Smiley", "Apache Solr 3 Enterprise Search Server", "Enhance your search with faceted navigation, result highlighting, relevancy ranked sorting, and more Comprehensive information on Apache Solr 3 with examples and tips so you can focus on the important parts Integration examples with databases, web-crawlers, XSLT, Java and embedded-Solr, PHP and Drupal, JavaScript, Ruby frameworks Advice on data modeling, deployment considerations to include security, logging, and monitoring, and advice on scaling Solr and measuring performance An update of the best-selling title on Solr 1.4")));
		addDoc(getObjMap(new Article("Rafal Ku", "Apache Solr 3.1 Cookbook", "Over 100 recipes to discover new ways to work with Apache's Enterprise Search Server Improve the way in which you work with Apache Solr to make your search engine quicker and more effective Deal with performance, setup, and configuration problems in no time Discover little-known Solr functionalities and create your own modules to customize Solr to your company's needs Part of Packt's Cookbook series; each chapter covers a different aspect of working with Solr")));
		addDoc(getObjMap(new Article("Christopher D. Manning", "Introduction to Information Retrieval", "Class-tested and coherent, this groundbreaking new textbook teaches web-era information retrieval, including web search and the related areas of text classification and text clustering from basic concepts. Written from a computer science perspective by three leading experts in the field, it gives an up-to-date treatment of all aspects of the design and implementation of systems for gathering, indexing, and searching documents; methods for evaluating systems; and an introduction to the use of machine learning methods on text collections. All the important ideas are explained using examples and figures, making it perfect for introductory courses in information retrieval for advanced undergraduates and graduate students in computer science. Based on feedback from extensive classroom experience, the book has been carefully structured in order to make teaching more natural and effective. Although originally designed as the primary text for a graduate or advanced undergraduate course in information retrieval, the book will also create a buzz for researchers and professionals alike.")));
		addDoc(getObjMap(new Article("Haralambos Marmanis", "Algorithms of the Intelligent Web", "Web 2.0 applications provide a rich user experience, but the parts you can't see are just as important-and impressive. They use powerful techniques to process information intelligently and offer features based on patterns and relationships in data. Algorithms of the Intelligent Web shows readers how to use the same techniques employed by household names like Google Ad Sense, Netflix, and Amazon to transform raw data into actionable information.")));
		addDoc(getObjMap(new Article("Manu Konchady", "Building Search Applications: Lucene, LingPipe, and Gate", "Lucene, LingPipe, and Gate are popular open source tools to build powerful search applications. Building Search Applications describes functions from Lucene that include indexing, searching, ranking, and spelling correction to build search engines. With this book you will learn to: Extract tokens from text using custom tokenizers and analyzers from Lucene, LingPipe, and Gate. Construct a search engine index with an optional backend database to manage large document collections. Explore the wide range of Lucene queries to search an index, understand the ranking algorithm for a query, and suggest spelling corrections. Find the names of people, places, and other entities in text using LingPipe and Gate. Categorize documents by topic using classifiers and build groups of self-organized documents using clustering algorithms from LingPipe. Create a Web crawler to scan the Web, Intranet, or desktop using Nutch. Track the sentiment of articles published on the Web with LingPipe.")));
		addDoc(getObjMap(new Article("Matthew A. Russell", "Mining the Social Web: Analyzing Data from Facebook, Twitter, LinkedIn, and Other Social Media Sites", "Want to tap the tremendous amount of valuable social data in Facebook, Twitter, LinkedIn, and Google+? This refreshed edition helps you discover who's making connections with social media, what they're talking about, and where they're located. You'll learn how to combine social web data, analysis techniques, and visualization to find what you've been looking for in the social haystack'as well as useful information you didn't know existed.")));
		addDoc(getObjMap(new Article("Toby Segaran", "Programming Collective Intelligence: Building Smart Web 2.0 Applications", "ant to tap the power behind search rankings, product recommendations, social bookmarking, and online matchmaking? This fascinating book demonstrates how you can build Web 2.0 applications to mine the enormous amount of data created by people on the Internet. With the sophisticated algorithms in this book, you can write smart programs to access interesting datasets from other web sites, collect data from users of your own applications, and analyze and understand the data once you've found it. Programming Collective Intelligence takes you into the world of machine learning and statistics, and explains how to draw conclusions about user experience, marketing, personal tastes, and human behavior in general--all from information that you and others collect every day. Each algorithm is described clearly and concisely with code that can immediately be used on your web site, blog, Wiki, or specialized application. This book explains:")));
		writer.close();
		
	}

	private void addDoc(Map<String, String> objMap) throws IOException {
		System.out.println(objMap);
		
		Document doc = new Document();
		for (Map.Entry<String, String> entry : objMap.entrySet()) {
			doc.add(new Field(entry.getKey(), entry.getValue(), Field.Store.YES, Field.Index.ANALYZED));
		}
		writer.addDocument(doc);
	}
	
	private Map<String, String> getObjMap(Object obj) throws Exception {
		if (obj == null) {
			return null;
		}
		
		Map m = PropertyUtils.describe(obj);
		Map<String, String> objMap = new HashMap<String, String>();
		
		for (Object key : m.keySet()) {
			objMap.put(String.valueOf(key), String.valueOf(m.get(key)));
		}
		
		return objMap;
	}
	
	@Test
	public void testSearch() throws Exception {
		search("title", "Lucene", null);
		
		search("title", "Lucene", new DefaultSimilarity() {
			public float tf(float freq) {
				return super.tf(freq);
			}
		});
	}
	
	private Document[] search(String fieldName, String queryStr, Similarity sim) throws Exception {
		Query q = new QueryParser(Version.LUCENE_CURRENT, fieldName, analyzer).parse(queryStr);
		
		int hitsPerPage = 10;
		
		IndexSearcher searcher = new IndexSearcher(index);
		
		if (sim != null) {
			searcher.setSimilarity(sim);
		}
		
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		
		searcher.search(q, collector);
		
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		System.out.println("\n" + queryStr + " Found " + hits.length + " hits.");
		
		List<Document> docs = new ArrayList<Document>();
		for (ScoreDoc sd : hits) {
			Document doc = searcher.doc(sd.doc);
			docs.add(doc);
			System.out.println(String.format("score: %f \tdoc: %s", sd.score, doc.get(fieldName)));
		}
		
		searcher.close();
		return docs.toArray(new Document[0]);
	}
	
	
	@Test
	public void testScoring() {
		
	}
	
	@Test
	public void TestAnalyzers() throws Exception {
		System.out.println();
		Analyzer aAnalyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		String sentence = "This is a sentence used to test lucene standard analyzer. \n There are three different types of analyzer, StandardAnalyzer, StopAnalyzer and WhitespaceAnalyzer";
		
		printTokens(aAnalyzer, sentence);
		
		printTokens(new StopAnalyzer(Version.LUCENE_CURRENT), sentence);
		
		printTokens(new WhitespaceAnalyzer(Version.LUCENE_CURRENT), sentence);
	}

	public void printTokens(Analyzer aAnalyzer, String sentence) throws IOException {
		StringReader sr = new StringReader(sentence);

		TokenStream ts = aAnalyzer.tokenStream("token", sr);
		System.out.println();
		while (ts.incrementToken()) {
			CharTermAttribute attribute = ts.getAttribute(CharTermAttribute.class);
			System.out.println(attribute);
		}
	}
	
	public static class Article {
		
		private String author;
		private String title;
		private String content;
		
		public String getAuthor() {
			return author;
		}

		public String getTitle() {
			return title;
		}

		public String getContent() {
			return content;
		}

		public Article(String author, String title, String content) {
			super();
			this.author = author;
			this.title = title;
			this.content = content;
		}
	}
	
	@Test
	public void testNormsDocBoost() throws Exception {
		System.out.println();
//		File indexDir = new File("testNormsDocBoost");
//		IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.LIMITED);
		IndexWriter writer = new IndexWriter(index, config);
//		writer.setUseCompoundFile(false);
		
		Document doc1 = new Document();
		Field f1 = new Field("contents", "common hello hello", Field.Store.YES, Field.Index.ANALYZED);
		doc1.add(f1);
		doc1.setBoost(100);
		writer.addDocument(doc1);
		
		Document doc2 = new Document();
		Field f2 = new Field("contents", "common common hello", Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);
		doc2.add(f2);
		writer.addDocument(doc2);
		
		Document doc3 = new Document();
		Field f3 = new Field("contents", "common common common", Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);
		doc3.add(f3);
		writer.addDocument(doc3);
		
		writer.close();
		
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(new TermQuery(new Term("contents", "common")), 10);
		for (ScoreDoc doc : docs.scoreDocs) {
			String docStr = new String( searcher.doc(doc.doc).getFieldable("contents").stringValue());
			System.out.println(String.format("doc id: %d \tscore: %f \tcontents: %s", doc.doc, doc.score, docStr));
		}
	}
	
	@Test
	public void testNormsFieldBoost() throws Exception {
		System.out.println();
		// File indexDir = new File("testNormsDocBoost");
		// IndexWriter writer = new IndexWriter(index, new
		// StandardAnalyzer(Version.LUCENE_CURRENT), true,
		// IndexWriter.MaxFieldLength.LIMITED);
		IndexWriter writer = new IndexWriter(index, config);
		// writer.setUseCompoundFile(false);

		Document doc1 = new Document();
		Field f1 = new Field("title", "common hello hello", Field.Store.YES, Field.Index.ANALYZED);
		f1.setBoost(100);
		doc1.add(f1);
		writer.addDocument(doc1);
		
		Document doc2 = new Document();
		Field f2 = new Field("contents", "common common hello", Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);
		doc2.add(f2);
		writer.addDocument(doc2);

		writer.close();

		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		  QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "contents", new StandardAnalyzer(Version.LUCENE_CURRENT)); 
		  Query query = parser.parse("title:common contents:common"); 
		TopDocs docs = searcher.search(query, 10);
		for (ScoreDoc doc : docs.scoreDocs) {
			Fieldable field = searcher.doc(doc.doc).getFieldable("contents");
			String docStr = field != null ? field.stringValue() : "";
			System.out.println(String.format("doc id: %d \tscore: %f \tcontents: %s", doc.doc, doc.score, docStr));
		}
	}

}

