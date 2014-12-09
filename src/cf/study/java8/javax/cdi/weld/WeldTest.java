package cf.study.java8.javax.cdi.weld;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import misc.MiscUtils;

import org.apache.log4j.Logger;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class WeldTest {

	static Weld weld;
	static WeldContainer container;
	static BeanManager bm;
	
	@Inject
	private Logger log;
	
	@BeforeClass
	public void setUp() {
		log.info(MiscUtils.invocationInfo());
		try {
			weld = new Weld();
			container = weld.initialize();
			bm = container.getBeanManager();
		} catch (final Exception e) {
			log.error("", e);
			System.exit(-1);
		}
	}
	
	
	
	@AfterClass
	public void tearDown() {
		log.info(MiscUtils.invocationInfo());
	}
}
