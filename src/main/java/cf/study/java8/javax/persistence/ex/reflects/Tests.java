package cf.study.java8.javax.persistence.ex.reflects;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.java8.javax.cdi.weld.WeldTest;
import cf.study.java8.lang.reflect.Reflects;

public class Tests {

	static ReflectDao dao;
	
	@BeforeClass
	public static void setUp() {
		WeldTest.setUp();
		dao = WeldTest.getBean(ReflectDao.class);
	}
	
	@Before
	public void before() {
		if (dao == null) {
			dao = new ReflectDao();
		}
		
		dao.beginTransaction();
	}
	
	@After
	public void after() {
		dao.endTransaction();
	}
	
	
	
	@Test
	public void testWithObject() {
		try {
			Class<Object> clz = Object.class;
			dao.create(clz);
			Stream.of(clz.getFields()).forEach((field)->{dao.create(field);});
			Stream.of(clz.getMethods()).forEach((method)->{dao.create(method);});
		} catch (Exception e) {
			e.printStackTrace();
			dao.setRollback();
		}
	}
	
	public void test() throws Exception {
		String[] classPaths = StringUtils.split(SystemUtils.JAVA_CLASS_PATH, ';');

		Optional<String> opt = Stream.of(classPaths).filter((String str) -> {
			return str.contains("junit");
		}).findFirst();
		Assert.assertTrue(opt.isPresent());

		List<Class<?>> re = Reflects.loadClzzFromJar(new File(opt.get()), ClassLoader.getSystemClassLoader());
		System.out.println(StringUtils.join(re, '\n'));
		System.out.println(re.size());
	}
	
	@AfterClass
	public static void tearDown() {
		
	}
}
