package cf.study.java8.javax.persistence.transactions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import misc.MiscUtils;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.java8.javax.persistence.dao.BaseDao;

public class TransactionTests {

	public static class TestDao extends BaseDao<TestEntity> {
		public TestDao(EntityManager _em) {
			super(_em);
		}
		
		protected Class<TestEntity> getEntityClass() {
			return TestEntity.class;
		}
	}
	
	public static EntityManager getEm() {
		EntityManager em = emf.createEntityManager();
		em.setFlushMode(FlushModeType.COMMIT);
		return em;
	}
	
	private static EntityManagerFactory emf = null;
	
	@BeforeClass
	public static void setUp() {
		emf = Persistence.createEntityManagerFactory("transactions");
	}
	
	@AfterClass
	public static void tearDown() {
//		TestDao td = new TestDao(getEm());
//		td.doFuncInTransaction((_td)->{
//			return _td.executeNativeSqlUpdate("delete from test where true;");
//		});
		emf.close();
	}

	private static final Logger log = Logger.getLogger(TransactionTests.class);
	
	@Test
	public void testConnection() {
		TestDao td = new TestDao(getEm());
		System.out.println(td.findAll());
	}
	
//	@Before 
	public void setDatabase() {
		TestDao td = new TestDao(getEm());
		td.doFuncInTransaction((_td)->{
			return _td.executeNativeSqlUpdate("set @innodb_lock_wait_timeout = 1;");
		});
	}
	
	@Test
	public void testUpdate() throws Exception {
		TestEntity en = null; 
		{
			TestDao td = new TestDao(getEm());
			en = (TestEntity)td.doFuncInTransaction((_td)->{
				return _td.create(new TestEntity(10l));
			});
			Assert.assertNotNull(en);
			log.info(en);
			MiscUtils.easySleep(1000);
		}
		
		final Long id = en.id;
		{
			TestDao td = new TestDao(getEm());
			log.info("\n\tstart updating at " + System.currentTimeMillis());
			td.doFuncInTransaction((_td)->{
				TestEntity _en = (TestEntity) _td.find(id);
				
				log.info("\t\nlock mode: " + td.getEm().getLockMode(_en));
				
				_en.value = _en.value + 1;
				log.info("before update: " + _en);
				Object edited = _td.edit(_en);
				_td.getEm().flush();
				log.info("after update: " + edited);
				return edited;
			});
			log.info("\n\tend updating at " + System.currentTimeMillis());
		}
		
		testConnection();
	}
	
	@Test
	public void testTransactionWait() throws Exception {
		TestEntity en = null; 
		{
			TestDao td = new TestDao(getEm());
			en = (TestEntity)td.doFuncInTransaction((_td)->{
				return _td.create(new TestEntity(10l));
			});
			Assert.assertNotNull(en);
			log.info(en);
			MiscUtils.easySleep(1000);
		}
		
		ScheduledExecutorService es = Executors.newScheduledThreadPool(MiscUtils.AVAILABLE_PROCESSORS);
		
		final Long id = en.id;
		
		Runnable task1 = ()->{
			TestDao td = new TestDao(getEm());
			log.info("\n\tstart occupying at " + System.currentTimeMillis());
			td.doFuncInTransaction((_td)->{
				_td.executeNativeSqlUpdate("begin;");
				
				TestEntity _en = (TestEntity) _td.find(id);
				_en.value = _en.value + 1;
				for (int i = 0; i < 40; i++) {
					log.info(Thread.currentThread().getName() + " sleep " + i);
					MiscUtils.easySleep(1000);
				}
				Object edited = _td.edit(_en);
				_td.executeNativeSqlUpdate("commit;");
				log.info(edited);
				return edited;
			});
			log.info("\n\tend occupying at " + System.currentTimeMillis());
		};
	
		for (int i = 0; i < 2; i++) {
			es.submit(task1);
		}
		
		
		Runnable task2 = ()->{
			TestDao td = new TestDao(getEm());
			log.info("\n\tstart updating at " + System.currentTimeMillis());
			td.doFuncInTransaction((_td)->{
				_td.executeNativeSqlUpdate("begin;");
				TestEntity _en = (TestEntity) _td.find(id);
				_en.value = _en.value + 1;
				Object edited = _td.edit(_en);
				_td.executeNativeSqlUpdate("commit;");
				log.info(edited);
				return edited;
			});
			log.info("\n\tend updating at " + System.currentTimeMillis());
		};
		for (int i = 0; i < 2; i++) {
//			es.submit(task1);
			es.schedule(task2, 500, TimeUnit.MILLISECONDS);
		}
		
		es.shutdown();
		es.awaitTermination(100, TimeUnit.SECONDS);
		testConnection();
	}
	
	@Test
	public void testInnodbLockWaitTimeOut() {
		TestDao td = new TestDao(getEm());
		List<Object[]> re = td.queryByNativeSqlWithIndexedParams("show Variables where Variable_name like 'innodb_lock_wait_timeout';", 0, 0);
//		log.info(re);
		re.forEach(e->log.info(Arrays.toString(e)));
	}
	
	@Test
	public void testReadUncommitted() {
		
	}
}
