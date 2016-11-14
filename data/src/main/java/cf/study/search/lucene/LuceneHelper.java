package cf.study.search.lucene;

import misc.MiscUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
//import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class LuceneHelper {

	public static String toString(Document doc) {
		if (doc == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("doc: fields_count: %d\n", doc.getFields().size()));

		doc.forEach((fd) -> {
			sb.append(String.format("field: %s\t%s\t=\t %s\n", fd.fieldType(), fd.name(), StringUtils.substringBefore(fd.stringValue(), "\n")));
		});

		return sb.toString();
	}

	public static void print(IndexSearcher searcher, TopDocs results) {
		System.out.println(String.format("%d total matching documents", results.totalHits));

		Object[] docs = Stream.of(results.scoreDocs).map((sd) -> {
			try {
				return searcher.doc(sd.doc);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}).toArray();

		Stream.of(docs).forEach(doc -> System.out.println(toString((Document) doc)));
	}

	@SuppressWarnings("unchecked")
	public static Document makeDoc(Object... kvs) {
		return makeDoc(MiscUtils.map(kvs));
	}

	public static Document makeDoc(Map<String, Object> data) {
		if (MapUtils.isEmpty(data))
			return null;

		data.put("sn", SEQ.getAndIncrement());

		Document doc = new Document();

		data.forEach((k, v) -> {
			if (v instanceof Number) {
				Number n = (Number) v;
				if ((n instanceof Float) || (n instanceof Double)) {
					FieldType ft = new FieldType();
					ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
//					ft.setNumericType(FieldType.LegacyNumericType.DOUBLE);
					ft.setDocValuesType(DocValuesType.NUMERIC);
					ft.setStored(true);
					doc.add(new DoubleDocValuesField(k, n.doubleValue()));
				} else {
					FieldType ft = new FieldType();
					ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
					ft.setDocValuesType(DocValuesType.NUMERIC);
					ft.setStored(true);
					doc.add(new NumericDocValuesField(k, n.longValue()));
				}
			} else {
				doc.add(new TextField(k, String.valueOf(v), Store.YES));
			}
		});

		return doc;
	}

	public static void indexDocs(IndexWriter writer, Document... docs) throws Exception {
		for (Document doc : docs) {
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				writer.addDocument(doc);
			} else {
				writer.updateDocument(new Term("sn"), doc);
			}
		}
	}

	static AtomicLong SEQ = new AtomicLong(0);

}
