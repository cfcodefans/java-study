package cf.study.framework.spring.script.groovy;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringGroovyTests {

	@Autowired
	private IEmployeeCacheService _ecs;
	
	@Autowired
	private ApplicationContext ac;
	
	@Autowired
	private String _const;
	
	@Test
	public void testAppCtx() {
		Assert.assertNotNull(ac);
		
		Stream.of(ac.getBeanDefinitionNames()).forEach(System.out::println);
	}
	
	@Test
	public void testService() {
//		Assert.assertNotNull(ecs);
		Object ecs = ac.getBean("groovyEmployeeCacheService");
		System.out.println(ecs);
		Assert.assertTrue(ecs instanceof IEmployeeCacheService);
	}
	
	@Test
	public void testAutowired() {
		Assert.assertTrue(_ecs instanceof IEmployeeCacheService);
		_ecs.addEmployeeToCache(new Employee("fan", "chen"));
	}
}
