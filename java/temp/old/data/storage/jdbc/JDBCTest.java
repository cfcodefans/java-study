package cf.study.data.storage.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hsqldb.jdbcDriver;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.utils.InfoUtils;


public class JDBCTest {
	
	static class Entity {
		int id;
		long number = 0;
		String name;
		String remark;
		
		static final String ID = "ID", NUMBER = "NUM", NAME = "NAME", REMARK = "REMARK";
		private static final String[] COLUMN_NAMES = new String[] {NUMBER, NAME, REMARK};
		static final String INSERT = String.format("insert into %s (%s) values (%s)", 
													TARGET_TABLE_NAME, 
													StringUtils.join(COLUMN_NAMES, ','), 
													StringUtils.repeat("?", COLUMN_NAMES.length));
		
		@Override
		public String toString() {
			return "Entity [id=" + id + ", number=" + number + ", name=" + name + ", remark=" + remark + "]";
		}

		
		
		private Entity findByNumber(Connection conn, long number) throws Exception {
			ResultSet rs = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE).executeQuery("select * from " + TARGET_TABLE_NAME + " where " + NUMBER + " = " + number);
			rs.first();
			return resultSetToEn(rs);
		}
		
		public Entity insert(Connection conn) throws Exception {
			if (conn == null || conn.isClosed() || conn.isReadOnly()) {
				return this;
			}
			
			PreparedStatement pst =  conn.prepareStatement(INSERT, COLUMN_NAMES);
			pst.setLong(ArrayUtils.indexOf(COLUMN_NAMES, NUMBER) + 1, number);
			pst.setString(ArrayUtils.indexOf(COLUMN_NAMES, NAME) + 1, name);
			pst.setString(ArrayUtils.indexOf(COLUMN_NAMES, REMARK) + 1, remark);
			pst.executeUpdate();
			
			pst.close();
			
			return findByNumber(conn, number);
		}
		
		private Entity resultSetToEn(ResultSet rs) throws Exception {
			Entity en = new Entity();
			en.name = rs.getString(NAME);
			en.number = rs.getLong(NUMBER);
			en.remark = rs.getString(REMARK);
			return en;
		}
		
		public List<Entity> resultSetToEns(ResultSet rs) throws Exception {
			if (rs == null || rs.isClosed()) {
				return Collections.emptyList();
			}
			
			List<Entity> ens = new ArrayList<Entity>();
			
			rs.first();
			while (rs.next()) {
				ens.add(resultSetToEn(rs));
			}
			
			return ens;
		}
		
		public List<Entity> resultSetToEns(ResultSet rs, int len) throws Exception {
			if (rs == null || rs.isClosed() || len < 1) {
				return Collections.emptyList();
			}
			
			List<Entity> ens = new ArrayList<Entity>();
			
			rs.first();
			for (int i = 0; i < len && rs.next(); i++) {
				ens.add(resultSetToEn(rs));
			}
			
			return ens;
		}
		
		public Entity() {
		} 
		
		Entity(long number, String name, String remark) {
			super();
			this.number = number;
			this.name = name;
			this.remark = remark;
		}

		static Entity randomEntity() {
			return new Entity(System.currentTimeMillis(), 
					RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(20)), 
					RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(3000)));
		}
	}
	
	private static final String TARGET_TABLE_NAME = "JDBC_STUDY_TABLE";
	private static final String DDL_TABLE = "create table JDBC_STUDY_TABLE ( " +
											" id int identity primary key, " +
											" num bigint, " +
											" name varchar(20), " +
											" remark varchar(3000) " +
											" );";
	private static final String MD_TABLE_NAME = "TABLE_NAME";
	static final Logger log = Logger.getLogger(JDBCTest.class.getSimpleName());
	static final String DB_FILE_NAME_PREFIX = "./test.data/jdbc/hsql";
	
	static Connection conn;
	
	public static void print(ResultSet rs) throws Exception {
		log.info(InfoUtils.prettyTable(InfoUtils.toString(rs)));
	}
	
	private static String getJdbcUrl(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException(String.format("file name: [%s] is empty", fileName));
		}
		return String.format("jdbc:hsqldb:file:%s", fileName);
	}
	
	@BeforeClass
	public static void startUp() throws Exception {
		//Load the HSQL database engine JDBC driver
		//hsqldb.jar should be in the class path or made part of the current jar
		Class.forName(jdbcDriver.class.getName());
		
		//connect to the database. This will load the db files and start the 
		//database if it is not already running
		//DB_FILE_NAME_PREFIX is used to open or create files that hold the state of the database
		//It can contian directory names relative to the current working directory
		//url, user name, password
//		conn = DriverManager.getConnection(getJdbcUrl(DB_FILE_NAME_PREFIX), "sa", "");
		Properties info = new Properties();
		info.put("user", "sa");
		info.put("password", "");
		info.put("database.0", DB_FILE_NAME_PREFIX + "/jdbc_test");
		info.put("dbname.0", "jdbc_test");
		conn = DriverManager.getConnection(getJdbcUrl(DB_FILE_NAME_PREFIX), info);
		
		log.info("Connection creation succeed!");
		
		if (!getTableNames().contains(TARGET_TABLE_NAME)) {
			conn.createStatement().execute(DDL_TABLE);
			conn.commit();
		}
	}

	@AfterClass
	public static void shutDown() throws Exception {
		if (conn == null || conn.isClosed()) {
			return;
		}
		
		Statement st = conn.createStatement();
		//db writes out to files and performs clean shuts down
		//otherwise the will be an unclean shutdown
		// when program exits
		st.execute("SHUTDOWN");
		conn.close();
		log.info("shutDown() succeed");
	}
	
	@Test
	public void metadata() throws Exception {
		log.info("conn.getSchema(): " + conn.getSchema());
		DatabaseMetaData md = conn.getMetaData();
		log.info("MetaData: " + md);
		
		ResultSet rs = md.getTables(null, null, "%", new String[] {"TABLE"});
		print(rs);
	}
	
	@Test
	public void testInsert() throws Exception {
		Entity en = Entity.randomEntity();
		en.insert(conn);
		
		ResultSet rs = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE).executeQuery("select * from " + TARGET_TABLE_NAME);
		print(rs);
		en = en.findByNumber(conn, en.number);
		Assert.assertNotNull(en);
	}
	
	@Test
	public void resultSetUpdate() throws Exception {
//		clearTable();
		Statement st = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);//very important!!!!
		st.execute("select " + StringUtils.join(Entity.COLUMN_NAMES, ',') + " from " + TARGET_TABLE_NAME);
//		st.execute("select * from " + TARGET_TABLE_NAME);
		ResultSet rs = st.getResultSet();
		print(rs);
		
		Entity en = Entity.randomEntity();
		rs.absolute(2);
		rs.updateString(Entity.NAME, "cf");
		rs.updateLong(Entity.NUMBER, en.number);
		rs.updateString(Entity.REMARK, en.remark);
		rs.updateRow();
		rs.close();
		
		st.execute("select " + StringUtils.join(Entity.COLUMN_NAMES, ',') + " from " + TARGET_TABLE_NAME);
		rs = st.getResultSet();
		print(rs);
		en = en.findByNumber(conn, en.number);
		Assert.assertNotNull(en);
	}
	
	@Test
	public void resultSetInsert() throws Exception {
//		clearTable();
		Statement st = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);//very important!!!!
		st.execute("select " + StringUtils.join(Entity.COLUMN_NAMES, ',') + " from " + TARGET_TABLE_NAME);
//		st.execute("select * from " + TARGET_TABLE_NAME);
		ResultSet rs = st.getResultSet();
		print(rs);
		
		Entity en = Entity.randomEntity();
		rs.moveToInsertRow();
//		rs.updateInt(Entity.ID, en.id);
		rs.updateString(Entity.NAME, en.name);
		rs.updateLong(Entity.NUMBER, en.number);
		rs.updateString(Entity.REMARK, en.remark);
		rs.insertRow();
		rs.close();
		
		rs = st.getResultSet();
		print(rs);
		en = en.findByNumber(conn, en.number);
		Assert.assertNotNull(en);
	}

	@Test
	public void resultSetDelete() throws Exception {
		clearTable();
		for (int i = 0; i < 10; i++) {
			Entity en = Entity.randomEntity();
			en.insert(conn);
		}
		
		{
			Statement st = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);//very important!!!!
			st.execute("select " + StringUtils.join(Entity.COLUMN_NAMES, ',') + " from " + TARGET_TABLE_NAME);
//			st.execute("select * from " + TARGET_TABLE_NAME);
			ResultSet rs = st.getResultSet();
			print(rs);
			
			rs.first();
			while (rs.next()) {
				log.info("delete: " + rs.getRow());
				rs.deleteRow();
			}
			rs.close();
			st.close();
			conn.commit();
		}
		
		{
			Statement st = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);//very important!!!!
			st.execute("select " + StringUtils.join(Entity.COLUMN_NAMES, ',') + " from " + TARGET_TABLE_NAME);
//			st.execute("select * from " + TARGET_TABLE_NAME);
			ResultSet rs = st.getResultSet();
			print(rs);
		}
	}
	
	protected static void clearTable() throws SQLException {
		conn.createStatement().executeUpdate("delete from " + TARGET_TABLE_NAME);
	}
	
	private static Set<String> getTableNames() throws Exception {
		DatabaseMetaData md = conn.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		Set<String> tableNames = new HashSet<String>();
		while (rs.next()) {
			tableNames.add(rs.getString(MD_TABLE_NAME));
		}
		return tableNames;
	}
	
	@After
	public void commit() throws Exception {
		conn.commit();
	}
}
