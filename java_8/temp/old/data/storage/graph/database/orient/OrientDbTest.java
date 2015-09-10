package cf.study.data.storage.graph.database.orient;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class OrientDbTest {
	private OServer server;

	//@Before
	void startUp() throws Throwable {
		FileInputStream is = FileUtils.openInputStream(new File("./cfg/db.xml"));
		System.out.println(is);
		server = OServerMain.create();
		System.out.println(server);
		server.startup(is);
		server.activate();
	}
	
	//@After
	public void clean() throws Throwable {
		server.shutdown();
	}
	
	@Test
	public void testGraph() {
		OrientGraph graph = null;
		try {
		  graph = new OrientGraph("local:e:/temp/graph/db");
		}finally{
		  if( graph != null )
		    graph.shutdown();
		}
	}
}
