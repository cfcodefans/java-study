package cf.study.framework.spring.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cf.study.framework.spring.script.groovy.Employee;
import cf.study.framework.spring.script.groovy.IEmployeeCacheService;

@ContextConfiguration("application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class XmlConfigurationTests {
	@Autowired
	private IEmployeeCacheService _ecs;
	
	@Autowired
	private ApplicationContext ac;
	
	@Test
	public void testAutowired() {
		Assert.assertTrue(_ecs instanceof EmployeeCacheService);
		Employee head = _ecs.getEmployee(0);
		Assert.assertNotNull(head);
		System.out.println(head);
//		_ecs.addEmployeeToCache(new Employee("fan", "chen"));
	}
}
