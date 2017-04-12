package cf.study.testing.junit;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cf.study.utils.MiscUtils;

public class JUnit4Test {

	private static final Logger log = Logger.getLogger(JUnit4Test.class.getSimpleName()); 
	
	public JUnit4Test() {
		log.info(MiscUtils.invocationInfo());
	}
	
	@Before
	public void before() {
		log.info(MiscUtils.invocationInfo());
	}
	
	@After
	public void after() {
		log.info(MiscUtils.invocationInfo());
	}
	
	@Rule
	public ExternalResource res = new ExternalResource() {
		Logger log = Logger.getLogger(ExternalResource.class.getSimpleName()); 
		protected void before() throws Throwable {
			this.log.info(MiscUtils.invocationInfo());
		}

		protected void after() {
			this.log.info(MiscUtils.invocationInfo());
		}
	};
	
	@BeforeClass
	public static void beforeClass() {
		log.info(MiscUtils.invocationInfo());
	}
	
	@AfterClass
	public static void AfterClass() {
		log.info(MiscUtils.invocationInfo());
	}
	
	@Test
	public void testMd1() {
		log.info(MiscUtils.invocationInfo());
	}
	@Test
	public void testMd2() {
		log.info(MiscUtils.invocationInfo());
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testException() throws Exception {
		int[] ia = new int[0];
		ia[1] = 0;
	}
	
	@Test(timeout = 3000) 
	public void testTimeout() throws Exception {
		Thread.currentThread().sleep(4000);
	}
	
	@Ignore
	@Test
	public void testIgnore() {
		log.info("you don't need to commment me out");
	}
}
