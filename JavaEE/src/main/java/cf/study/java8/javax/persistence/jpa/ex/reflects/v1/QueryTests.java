package cf.study.java8.javax.persistence.jpa.ex.reflects.v1;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.java8.javax.cdi.weld.WeldTest;
import cf.study.java8.javax.persistence.dao.JpaModule;
import cf.study.java8.javax.persistence.jpa.ex.reflects.v1.entity.ClassEn;

public class QueryTests {
	@BeforeClass
	public static void setUp() {
		WeldTest.setUp();
		JpaModule.instance();
	}

	private static final Logger log = Logger.getLogger(QueryTests.class);
	private ReflectDao			rd;
	
	@Before
	public void setUpTest() {
		rd = ReflectDao.threadLocal.get();
	}
	
	@Test
	public void testQuery() {
		ClassEn comparable = (ClassEn) rd.findOne("select ce from ClassEn ce where ce.name=?1", Comparable.class.getName());
		ClassEn serializable = (ClassEn) rd.findOne("select ce from ClassEn ce where ce.name=?1", Serializable.class.getName());
		
		Assert.assertNotNull(comparable);
		Assert.assertNotNull(serializable);
		
		HashSet<ClassEn> param = new HashSet<ClassEn>(Arrays.asList(comparable, serializable));
		
//		HashSet<Long> param = new HashSet<Long>(Arrays.asList(comparable.id, serializable.id));
//		List<Object> reList = rd.query("select ce from ClassEn ce join ce.infs inf "
//				+ "where inf.id = any(select _in.id from ClassEn _in where _in.id in (?1))",
//				param);
		
//		List<Object> reList = rd.query("select ce from ClassEn ce where ?1 member of ce.infs", param);
		
		List<Object> reList = rd.query("select ce from ClassEn ce, ClassEn inf where inf member ce.infs and inf in (?1) group by ce.id having count(ce.id)=?2", param, new Long(param.size()));
		
		reList.forEach(System.out::println);
		reList.stream().map(obj->(ClassEn)obj).forEach(ce->Assert.assertTrue(CollectionUtils.containsAll(ce.infs, param)));
	}
}
