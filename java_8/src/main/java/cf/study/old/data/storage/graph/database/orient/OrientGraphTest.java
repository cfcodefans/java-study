package cf.study.data.storage.graph.database.orient;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class OrientGraphTest {
	static OrientGraph og;

	@BeforeClass
	public static void init() {
		og = new OrientGraph("local:./test.data/orientDB");
	}

	@AfterClass
	public static void end() {
		for (Edge e : og.getEdges()) {
			e.remove();
		}
		for (Vertex v : og.getVertices()) {
			v.remove();
		}

		og.commit();

		if (og != null) {
			og.shutdown();
		}
	}

	@After
	public void showAll() {
		System.out.println(StringUtils.repeat("-", 20));
		print(og, "all");
	}

	@Test
	public void testAll() {
		testCreateVertex();
		testEdge();
		testQuery();
	}

	@Test
	public void testCreateVertex() {
		Vertex fan = og.addVertex("cf");
		fan.setProperty("name", "chenfan");

		Vertex cc = og.addVertex("cc");
		cc.setProperty("name", "chencheng");

		Vertex apt = og.addVertex("apartment");
		apt.setProperty("name", "3_204");
		apt.setProperty("location", "NanJing");
		apt.setProperty("price", 122);

		og.commit();
	}

	private Vertex getVertexByName(String name) {
		return og.query().has("name", name).vertices().iterator().next();
	}

	private void print(Graph g, String name) {
		System.out.println(name);
		System.out.println("\t" + StringUtils.join(g.getVertices().iterator(), "\n\t"));
	}

	static String toString(Vertex v) {
		if (v == null)
			return String.valueOf(v);
		StringBuilder sb = new StringBuilder(v.toString());
		sb.append("{");
		for (String key : v.getPropertyKeys()) {
			sb.append('"').append(key).append('"').append(':').append('"').append(v.getProperty(key)).append('"').append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append('}');
		return sb.toString();
	}

	@Test
	public void testEdge() {
		Vertex fan = getVertexByName("chenfan");
		Vertex cc = getVertexByName("chencheng");
		Vertex apt = getVertexByName("3_204");

		Assert.assertNotNull(fan);
		Assert.assertNotNull(cc);
		Assert.assertNotNull(apt);

		Edge cousin = og.addEdge(null, fan, cc, "cousin");
		og.addEdge(null, cc, fan, "cousin");
		cousin.setProperty("type", "relative");

		Edge ownership = og.addEdge("purchase", fan, apt, "own");
		ownership.setProperty("type", "ownership");
		og.commit();

		System.out.println(og);
	}

	@Test
	public void testQuery() {
		Vertex fan = getVertexByName("chenfan");
		Assert.assertNotNull(fan);
		Vertex v = fan.query().direction(Direction.OUT).labels("own").vertices().iterator().next();
		System.out.println(toString(v));
		
		Vertex cc = fan.query().direction(Direction.OUT).labels("cousin").vertices().iterator().next();
		System.out.println(toString(cc));
		
		Vertex cc1 = fan.query().direction(Direction.IN).labels("cousin").vertices().iterator().next();
		System.out.println(toString(cc1));
		
		long c = fan.query().direction(Direction.OUT).labels("cousin").has("name", "chencheng").count();
		System.out.println(c);
		
		c = fan.query().direction(Direction.OUT).labels("cousin").has("type", "relative").count();
		System.out.println(c);
		
		c = fan.query().direction(Direction.OUT).count();
		System.out.println(c);
		
		c = fan.query().direction(Direction.OUT).has("type", "ownership").count();
		System.out.println(c);
	}
}
