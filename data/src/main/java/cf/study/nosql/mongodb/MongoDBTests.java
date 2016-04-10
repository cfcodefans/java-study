package cf.study.nosql.mongodb;


import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

public class MongoDBTests {

	static MongoClient mc = null;
	static MongoDatabase md = null;
	
	@BeforeClass
	public static void setupClass() {
		mc = new MongoClient();
		md = mc.getDatabase("test"); 
	}
	
	@AfterClass
	public static void tearDownClass() {
		if (mc != null) {
			mc.close();
		}
	}
	
	@Test
	public void testConn() {
		// 127.0.0.1:27017 default port
		System.out.println(ToStringBuilder.reflectionToString(md, ToStringStyle.MULTI_LINE_STYLE));
		StreamSupport.stream(md.listCollections().spliterator(), false).map(c -> ToStringBuilder.reflectionToString(c, ToStringStyle.MULTI_LINE_STYLE)).forEach(System.out::println);
	}
	
	public Document testBSON() throws Exception {
		Document one = new Document("address", new Document().append("street", "2 Avenue")
				  .append("zipcode", "10075")
				  .append("building", "1480")
				  .append("coord", asList(-73.9557413, 40.7720266)))
					.append("borough", "Manhattan")
					.append("cuisine", "Italian")
					.append("grades", asList(
					new Document()
					.append("date", format.parse("2014-10-01 00:00:00"))
					.append("grade", "A")
					.append("score", 11),
					new Document()
					.append("date", format.parse("2014-01-16 00:00:00"))
					.append("grade", "B")
					.append("score", 17)))
					.append("name", "Vella")
					.append("restaurant_id", "417046420");
		
		return one;
	}
//
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	
	@Test 
	public void testInsert() throws Exception {
		Document one = testBSON();
		System.out.println(one.toJson(new JsonWriterSettings(true)));
		col.insertOne(one);
	}
	
	MongoCollection<Document> col = md.getCollection("restaurants");
	
	@Test
	public void testFind() {
		FindIterable<Document> find = col.find();
		
		System.out.println(ToStringBuilder.reflectionToString(find, ToStringStyle.MULTI_LINE_STYLE));
		
		find.limit(2);
		
		StreamSupport.stream(find.spliterator(), false)
			.forEach(doc -> System.out.println(doc.toJson()));
	}
	
	@Test
	public void testFindConds() {
		col.find(eq("name", "Vella")).forEach(printer);
		System.out.println();
		col.find(new Document("address.zipcode", "10075")).projection(include("name")).limit(5).forEach(printer);
		System.out.println();
		print(col.find(eq("grades.grade", "B")).first());
		System.out.println();
		print(col.find(and(eq("grades.grade", "B"), eq("grades.grade", "A"))).projection(include("name", "grades.grade")).first());
		System.out.println();
		col.find(gt("grades.score", 5)).projection(include("name", "grades.score")).limit(5).forEach(printer);
		System.out.println();
		col.find(lt("grades.score", 5)).projection(include("name", "grades.score")).limit(5).forEach(printer);
		
		System.out.println();
		col.find(and(lt("grades.score", 6), gt("grades.score", 4))).projection(include("grades.score")).limit(5).forEach(printer);
		
	}
	
	@Test
	public void testSort() {
		System.out.println("sort ascendently");
		col.find()
			.sort(new Document("borough", 1).append("address.zipcode", 1))
			.projection(include("name", "address.zipcode"))
			.limit(5)
			.forEach(printer);
		
		System.out.println("sort compound fields ascendently");
		col.find()
			.sort(new Document("borough", 1).append("address.zipcode", 1).append("name", 1))
			.projection(include("name", "address.zipcode"))
			.limit(5)
			.forEach(printer);
		
		System.out.println("sort decendently");
		col.find()
			.sort(new Document("borough", 1).append("address.zipcode", -1))
			.projection(include("name", "address.zipcode"))
			.limit(5)
			.forEach(printer);
		
		System.out.println("sorts decendently");
		col.find()
			.sort(ascending("name", "address.zipcode"))
			.projection(include("name", "address.zipcode"))
			.limit(5)
			.forEach(printer);

	}
	
	public void print(Document d) {
		System.out.println(d.toJson());
	}
	
	Block<Document> printer = this::print;
	
	@Test 
	public void testProjection() {
		System.out.println("include");
		col.find().projection(include("name", "address.zipcode")).limit(1).forEach(printer);
		
		System.out.println("fields");
		col.find().projection(fields(include("name"), include("address.zipcode"))).limit(1).forEach(printer);
		
		System.out.println("elemMatch");
		col.find().projection(Projections.elemMatch("grades.score", and(lt("grades.score", 6), gt("grades.score", 4)))).limit(1).forEach(printer);
	}
}
