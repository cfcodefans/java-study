package cf.study.nosql.orientdb;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orientechnologies.orient.core.entity.OEntityManager;
import com.orientechnologies.orient.core.metadata.schema.OClass;
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

import cf.study.nosql.orientdb.domain.TestEntity;
import cf.study.nosql.orientdb.domain._Schema;
import cf.study.nosql.orientdb.sample.QueryDbExample;
import misc.Jsons;
import misc.MiscUtils;

public class OrientDBTests {

	private static final String ORIENTDB_HOME = "/data/orientdb";
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "orientdb";
	private static final String DATABASE_PATH = "/data/orientdb/databases/study";
	private static final String DATABASE_URL = "plocal:" + DATABASE_PATH;
	private static OServer os = null;
	private static OObjectDatabaseTx db = null;
	private static OEntityManager oem = null;

	@BeforeClass
	public static void setUpClass() {
		try {
			Path databasePath = Paths.get(DATABASE_PATH);
			if (Files.notExists(databasePath)) {
				log.info(String.format("database path is created at %s", Files.createDirectories(databasePath)));
			}

			String orientdbHome = new File(ORIENTDB_HOME).getAbsolutePath(); 
			System.setProperty("ORIENTDB_HOME", orientdbHome);
			System.setProperty("ORIENTDB_ROOT_PASSWORD", PASSWORD);

			os = OServerMain.create();

			OServerConfiguration cfg = new OServerConfiguration();
			{
				OServerHandlerConfiguration shc = new OServerHandlerConfiguration();
				shc.clazz = OrientTokenHandler.class.getName();
				OServerParameterConfiguration enable = new OServerParameterConfiguration("enabled", Boolean.TRUE.toString());

				shc.parameters = new OServerParameterConfiguration[] {};
				cfg.handlers = Arrays.asList(shc);
			}
			{
				OServerUserConfiguration uc = new OServerUserConfiguration();
				uc.name = USER_NAME;
				cfg.users = new OServerUserConfiguration[] { uc };
			}

			os.startup(cfg);
			os.activate();

			db = new OObjectDatabaseTx(DATABASE_URL);

			if (!db.exists()) {
				log.info(String.format("Database: %s doesn't exist, needs to be created first!", DATABASE_PATH));
				db.create();
			}
			
			db.open("admin", "admin");

			db.activateOnCurrentThread();

			// if (db.isClosed()) {
			//
			// db.open(USER_NAME, PASSWORD);
			// }

			oem = db.getEntityManager();

			// can call this repeatedly
			oem.registerEntityClasses(TestEntity.class.getPackage().getName());

		} catch (Throwable e) {
			log.error("failed to start OrientDB", e);
		}
	}

	@AfterClass
	public static void tearDownClass() {
		if (db != null && !db.isClosed()) {
			
			db.activateOnCurrentThread();
			log.info("ODatabase.drop()");
			//db.drop();
			log.info("ODatabase.close()");
			db.close();
		}

		if (os != null) {
			log.info("OServer.shutdown()");
			os.shutdown();
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
	public void testEntityManager() {
		Assert.assertNotNull(oem);
		oem.getRegisteredEntities().stream().map(Class::getName).forEach(log::info);
		
		OClass ocls = db.getMetadata().getSchema().getClass(TestEntity.class);
		log.info("ocls:\t" + ocls);
		log.info("properties:\n\t" + StringUtils.join(ocls.properties(), "\n"));
	}

	@Test
	public void testSchemaCreation() {
		List<OClass> schemaList = oem.getRegisteredEntities().stream().map(Class::getName).map(clsName -> {
			log.info(clsName);
			return db.getMetadata().getSchema().getOrCreateClass(clsName);
		}).collect(Collectors.toList());

		log.info("create entity classes:");
		schemaList.forEach(log::info);
	}

	@Test
	public void testSaveEntity() {
		log.info("raw List");
		List<TestEntity> rawList = IntStream.range(0, 10).mapToObj(i -> RandomStringUtils.randomAlphabetic(i % 10)).map(s -> new TestEntity(s)).collect(Collectors.toList());
		log.info(StringUtils.join(rawList, "\n\t"));

		MiscUtils.easySleep(2000);
		
		List<Object> savedList = rawList.stream().map(db::save).collect(Collectors.toList());
		log.info("saved List");
		log.info(StringUtils.join(savedList, "\n\t"));
		
		log.info("query");
		StreamSupport.stream(db.browseClass(TestEntity.class).spliterator(), false).forEach(log::info); 
	}

	@Test
	public void testLoadEntity() {
		// List<ClassEn> reList = db.query(new OSQLSynchQuery<ClassEn>("select
		// from cf.study.data.mining.entity.ClassEn"));
		// log.info("found " + reList.size());
		// reList.forEach(log::info);
	}
	
	@Test
	public void testSample() {
//		QueryDbExample.createPerson(db);
		QueryDbExample.queryPersons(db);
	}

	@Test
	public void _testSave() throws Exception {
		_Schema cs = new _Schema();
		cs.setSid(RandomUtils.nextLong(100, 400));
		cs.setName(RandomStringUtils.randomAlphanumeric(10));
		_Schema saved = db.save(cs);
		saved = db.detachAll(saved, true);
		
		log.info(Jsons.toString(cs));
		log.info("");
		log.info(Jsons.toString(saved));
	}

	@Test
	public void _testSearch() throws Exception {
		Iterable<_Schema> schemas = db.browseClass(_Schema.class);
		log.info(StringUtils.join(schemas, "\n"));
	}

	private static final Logger log = Logger.getLogger(OrientDBTests.class);
}
