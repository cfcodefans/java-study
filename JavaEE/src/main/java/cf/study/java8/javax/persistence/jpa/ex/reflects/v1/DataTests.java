package cf.study.java8.javax.persistence.jpa.ex.reflects.v1;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.java8.javax.cdi.weld.WeldTest;
import cf.study.java8.javax.persistence.dao.JpaModule;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.BaseEn;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.ClassEn;



public class DataTests {
	
	@BeforeClass
	public static void setUp() {
		WeldTest.setUp();
//		ReflectDao dao = ReflectDao.threadLocal.get();
		JpaModule.instance();
	}

	private static final Logger log = Logger.getLogger(DataTests.class);
	
	@Test
	public void test() {
		EntryLoader el = new EntryLoader();
		try {
			el.extractJarStructure("junit");
			el.roots.stream().forEach((baseEn)->{System.out.println(baseEn.name);});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    System.out.println(el.classEnPool.size());
	}
	
	@Test
	public void testWithObject() {
		EntryLoader el = new EntryLoader();
		ClassEn ce = el.preloadClassEnByClz(Object.class);
		
//		el.roots.forEach((en) -> {
//			EntryLoader.traverse(en, act);
//		});
		
		ReflectDao dao = ReflectDao.threadLocal.get();
		dao.beginTransaction();
		el.roots.forEach(dao::persist);
		dao.endTransaction();
	}
	
    static	Consumer<BaseEn> act = (BaseEn)-> {
		ReflectDao dao = ReflectDao.threadLocal.get();
		dao.beginTransaction();
		dao.create(BaseEn);
		dao.getEm().flush();
		dao.endTransaction();
	};
	
	@Test
	public void testRuntimeJar() {
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
		
		EntryLoader el = new EntryLoader();
		try {
			el.extractJarStructure(_f);
//			el.roots.stream().forEach((baseEn)->{System.out.println(baseEn.name);});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		el.roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			EntryLoader.traverse(be, (_be)->{dao.create(_be);}, ()->{dao.getEm().flush();});
			dao.endTransaction();
		});
	}
	
	@Test
	public void testTraverseWithObject() {
		EntryLoader el = new EntryLoader();
		el.preloadClassEnByClz(Object.class);
		
		el.roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			EntryLoader.traverse(be, (_be)->{dao.create(_be);}, ()->{dao.getEm().flush();});
			dao.endTransaction();
		});

		System.out.println("preload is done");
//		
		el.classEnPool.values().parallelStream().forEach(el::inflateClassEnByClz);
//		
		System.out.println("inflation is done");
//		
		ReflectDao dao = ReflectDao.threadLocal.get();//new ReflectDao(JpaModule.getEntityManager());
		dao.beginTransaction();
		el.classEnPool.values().forEach(dao::refresh);
		el.classEnPool.values().forEach((be)->{
			EntryLoader.traverse(be, dao::edit, ()->{});
		});
		dao.getEm().flush();
		dao.endTransaction();
		System.out.println("merge is done");
	}
	
	@Test
	public void verifyDatabase() {
		ReflectDao dao = ReflectDao.threadLocal.get();
		System.out.println(dao.queryCount("select count(1) from BaseEn be"));
	}
	
	@Test
	public void prepareData() {
		EntryLoader el = new EntryLoader();
//		el.preloadClassEnByClz(com.sun.awt.AWTUtilities.class);
		
		el.classEnPool.keySet().parallelStream().forEach((key) -> {
			ClassEn ce = el.classEnPool.get(key);
			el.inflateClassEnByClz(ce);
			System.out.println(key + ": \t" + ce.id + ": \t" + ce.name);
		});
		
		el.classEnPool.values().parallelStream().filter((ce)->(ce.infs.size() > 1)).forEach((ce)->{
			StringBuilder sb = new StringBuilder();
			sb.append(ce.name).append(".infs: \n");
			ce.infs.forEach((ie)->{sb.append("\t").append(ie.name).append("\n");});
			System.out.println(sb);
		});
	}
	
	@Test
	public void testTraverseWithObject1() {
		EntryLoader el = new EntryLoader();
		el.preloadClassEnByClz(Object.class);
		
		el.roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			EntryLoader.traverse(be, dao::create, ()->{dao.getEm().flush();});
			dao.endTransaction();
		});
		System.out.println("Preload class: " + el.classEnPool.size());
		
//		el.classEnPool.clear();
		
		{
			ReflectDao dao = ReflectDao.threadLocal.get();
			List<Object[]> idAndNames = dao.queryEntity("select id, name from ClassEn ce");
			idAndNames.parallelStream().forEach((idAndName) -> {
				Long id = (Long) idAndName[0];
				String name = (String) idAndName[1];
				
				ClassEn ce = el.classEnPool.get(name);
				if (ce != null)
					ce.id = id;
			});
		}
		
		el.classEnPool.keySet().parallelStream().forEach((key) -> {
			ClassEn ce = el.classEnPool.get(key);
			el.inflateClassEnByClz(ce);
			System.out.println(key + ": \t" + ce.id + ": \t" + ce.name);
		});
		System.out.println("Inflate class: " + el.classEnPool.size());
		
		ReflectDao dao = ReflectDao.threadLocal.get();
		el.classEnPool.keySet().forEach((key) -> {
			dao.beginTransaction();
			ClassEn ce = el.classEnPool.get(key);
			dao.inflateClassEnByNativeSql(ce);
			dao.endTransaction();
		});
	}
	
	@Test
	public void testTraverse() {
		EntryLoader el = new EntryLoader();
		try {
			el.extractJarStructure("junit");
//			el.roots.stream().forEach((baseEn)->{System.out.println(baseEn.name);});
		} catch (Exception e) {
			e.printStackTrace();
		}
		el.roots.parallelStream().forEach((be)->{
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			EntryLoader.traverse(be, (_be)->{dao.create(_be);}, ()->{dao.getEm().flush();});
			dao.endTransaction();
		});

		System.out.println("preload is done");
		
		el.classEnPool.values().parallelStream().forEach((ce) -> {
			el.inflateClassEnByClz(ce);
		});
		
		System.out.println("inflation is done");
		
		ReflectDao dao = ReflectDao.threadLocal.get();
		dao.beginTransaction();
		el.roots.parallelStream().forEach((be)->{
			EntryLoader.traverse(be, (_be)->{dao.edit(_be);}, ()->{});
		});
		System.out.println("merge is done");
		dao.getEm().flush();
		dao.endTransaction();
	}
	
	static class PersistActor implements Runnable {
		final List<BaseEn> stuff;
		
		public static void persist(Collection<BaseEn> beCol) {
			if (CollectionUtils.isEmpty(beCol)) return;
		}
		
		public PersistActor(List<BaseEn> stuff) {
			super();
			this.stuff = stuff;
		}

		public void run() {
			ReflectDao dao = ReflectDao.threadLocal.get();
			dao.beginTransaction();
			stuff.forEach((be)->{dao.persist(be);});
			dao.endTransaction();
		}
	}
	
	@AfterClass
	public static void tearDown() {
		WeldTest.tearDown();
	}

	public static void main(String[] args) {
		WeldTest.setUp();
		JpaModule.instance();
		File _f = new File(String.format("%s/lib/rt.jar", SystemUtils.JAVA_HOME));
		
		EntryLoader el = new EntryLoader();
		try {
//			el.extractJarStructure(_f);
//			el.extractJarStructure("junit");
			el.preloadClassEnByClz(Object.class);

			long size = el.classEnPool.size();
			
			log.info("have found classes: " + size);
			
			{
				AtomicLong counter = new AtomicLong(0);
				el.roots.parallelStream().forEach((be) -> {
					ReflectDao dao = ReflectDao.threadLocal.get();
					dao.beginTransaction();
					EntryLoader.traverse(be, (_be) -> {
						dao.create(_be); 
						if (_be instanceof ClassEn){
							long _c = counter.incrementAndGet();
							log.info(String.format("%d/%d %f%% %s", _c, size, _c/(double)size * 100, _be.name));
						} else {
							//log.info(String.format("%s \t%s.%s", _be.category, (_be.enclosing == null ? "_" : _be.enclosing.name), _be.name));
						}
					}, () -> {
						dao.getEm().flush();
					});
					dao.getEm().flush();
					dao.endTransaction();
				});
				System.out.println("Preload class: " + el.classEnPool.size());
			}
			
			{
				ReflectDao dao = ReflectDao.threadLocal.get();
				List<BaseEn> beList = dao.queryEntity("select be from BaseEn be");	

				beList.stream().filter((be)->(be instanceof ClassEn)).parallel().forEach((_ce)-> {
					ClassEn ce = (ClassEn)_ce;
					ClassEn detached = el.classEnPool.put(_ce.name, ce);
					ce.clazz = detached.clazz;
					
					EntryLoader.traverse(_ce, (_be)->{
						if (_be.id == null) {
							log.error(_be.category + "\t" + _be.name + " has null id!!");
						}
					}, ()->{});
				});
			}
//			if (true) return;

			el.classEnPool.keySet().parallelStream().forEach((key) -> {
				ClassEn ce = el.classEnPool.get(key);
				el.inflateClassEnByClz(ce);
				System.out.println(key + ": \t" + ce.id + ": \t" + ce.name);
			});
			System.out.println("Inflate class: " + el.classEnPool.size());

			ReflectDao dao = ReflectDao.threadLocal.get();
			el.classEnPool.keySet().forEach((key) -> {
				dao.beginTransaction();
				
				ClassEn ce = el.classEnPool.get(key);
				log.info(String.format("%d/%d %f%% %s", ++counter, size, counter/(double)size * 100, ce.name));
				dao.inflateClassEnByNativeSql(ce);
				dao.endTransaction();
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JpaModule.instance().destory();
		}
	}
	static long counter = 0;
}
