package cf.study.framework.spring.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cf.study.framework.spring.aop.service.IMockService;
import misc.MiscUtils;

@ContextConfiguration("application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
//@Configuration
@EnableAspectJAutoProxy
public class AOPTests {

	public final static Logger log = LogManager.getLogger(AOPTests.class);
	
	@Aspect
	public static class NotVeryUsefulAspect {
		
	}
	
	@Aspect
	public class BeforeExample {
		@Before("execution(* cf.study.framework.spring.aop.service.*.*(..))")
		public void doAccessCheck() {
			log.info(MiscUtils.invocationInfo());
		}
	}
	
	@Autowired
	IMockService ms;
	
	@Autowired
	ApplicationContext ac;
	
	@Test
	public void testAspect() {
		Assert.assertNotNull(ms);
		log.info(ms.getClass());
		ms.foo();
		
	}
	
}
