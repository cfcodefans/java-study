package cf.study.nosql.orientdb;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.metadata.OMetadataObject;
import com.orientechnologies.orient.object.metadata.schema.OSchemaProxyObject;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.config.OServerConfiguration;
import com.orientechnologies.orient.server.config.OServerHandlerConfiguration;
import com.orientechnologies.orient.server.config.OServerParameterConfiguration;
import com.orientechnologies.orient.server.config.OServerUserConfiguration;
import com.orientechnologies.orient.server.token.OrientTokenHandler;

public class OrientDBTests {
	
	private static final String ORIENTDB_HOME = "/data/orientdb";
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "orientdb";
	private static final String DATABASE_PATH = "/data/orientdb/databases/study";
	private static final String DATABASE_URL = "plocal:" + DATABASE_PATH;
	private static OServer os = null;
	private static OObjectDatabaseTx db = null;
	
	@BeforeClass
	public static void setUpClass() {
		try {
			Path databasePath = Paths.get(DATABASE_PATH);
			if (Files.notExists(databasePath)) {
				log.info(String.format("database path is created at %s", Files.createDirectories(databasePath)));
			}
			
			String orientdbHome = new File(ORIENTDB_HOME).getAbsolutePath(); //Set OrientDB home to current directory
		    System.setProperty("ORIENTDB_HOME", orientdbHome);
		    System.setProperty("ORIENTDB_ROOT_PASSWORD", PASSWORD);
			
			os =  OServerMain.create();
			
			OServerConfiguration cfg = new OServerConfiguration();
			{
				OServerHandlerConfiguration shc = new OServerHandlerConfiguration();
				shc.clazz = OrientTokenHandler.class.getName();
				OServerParameterConfiguration enable = new OServerParameterConfiguration("enabled", Boolean.TRUE.toString());
				
				shc.parameters = new OServerParameterConfiguration[] {};
				cfg.handlers = Arrays.asList(shc);
				os.startup(cfg);
			} 
			{
				OServerUserConfiguration uc = new OServerUserConfiguration();
				uc.name = USER_NAME;
				cfg.users = new OServerUserConfiguration[] {uc};
			}
			
			os.activate();
			
			
			
			db = new OObjectDatabaseTx(DATABASE_URL);
			
			if (!db.exists()) {
				log.info(String.format("Database: %s doesn't exist, needs to be created first!", DATABASE_PATH));
				db.create();
			}
			
			db.activateOnCurrentThread();
			
//			if (db.isClosed()) {
//				
//				db.open(USER_NAME, PASSWORD);
//			}
		} catch (Throwable e) {
			log.error("failed to start OrientDB", e);
		}
	}
	
	@Before
	public void setUp() {
		if (db.isClosed()) {
			log.warn("db is closed?");
			db.open("admin", "admin");
		}
	}
	
	@Test
	public void testServer() {
		Assert.assertNotNull(os);
		Assert.assertTrue(os.isActive());
		log.info(os.getAvailableStorageNames());
	}
	
	@Test
	public void testDatabase() {
		Assert.assertNotNull(db);
		log.info(db);
		log.info(db.getName());
		OMetadataObject md = db.getMetadata();
		log.info(md);
		OSchemaProxyObject schema = md.getSchema();
		log.info(schema);
		log.info("classes in this schema:");
		schema.getClasses().forEach(log::info);
	}
	
	@Test
	public void testCreateEntity() {
		
	}
	
	@AfterClass
	public static void tearDownClass() {
		if (db != null && !db.isClosed()) {
			log.info("ODatabase.close()");
			db.close();
		}
		
		if (os != null) {
			log.info("OServer.shutdown()");
			os.shutdown();
		}
	}

	private static final Logger	log	= Logger.getLogger(OrientDBTests.class);
}
