package cf.study.framework.spring;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import misc.MiscUtils;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

public class AnnotationTests {
	public static interface IOperBean {
		Object oper(Object... params);
	}

	public static class BizBean {
		public static final String BEAN_NAME = "bizBean";

		public BizBean() {
			AppCtxTests.log.info(MiscUtils.invocationInfo());
		}

		@PostConstruct
		public void init() {
			AppCtxTests.log.info(MiscUtils.invocationInfo());
		}

		@PreDestroy
		public void destroy() {
			AppCtxTests.log.info(MiscUtils.invocationInfo());
		}

		@Resource(name = "logOper")
		private IOperBean oper;

		public IOperBean getOper() {
			return oper;
		}

		public void setOper(IOperBean oper) {
			this.oper = oper;
		}
	}

	@Component("logOper")
	public static class LogOper implements IOperBean {
		public Object oper(Object... params) {
			log.info(Arrays.toString(params));
			return params;
		}
	}

	public static class NamedBizBean extends BizBean {
		public static final String BEAN_NAME = "namedBizBean";

		public NamedBizBean(String name) {
			this.name = name;
			AppCtxTests.log.info(MiscUtils.invocationInfo() + "_name: " + name + ": String");
		}

		String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class LogBizBean extends NamedBizBean {
		public static final String BEAN_NAME = "logBizbean";
		public LogBizBean() {
			super(BEAN_NAME);
		}
	}

	static AbstractApplicationContext nac;

	@BeforeClass
	public static void setUp() throws Exception {
		nac = new AnnotationConfigApplicationContext();
		nac.refresh();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		if (nac != null && nac.isActive()) {
			nac.close();
		}
	}

	static final Logger log = Logger.getLogger(AnnotationTests.class);

	@Test
	public void test() {
		String beanName = LogBizBean.BEAN_NAME;
		Object logBean = nac.getBean(beanName);
		Assert.assertTrue(logBean instanceof NamedBizBean);
		NamedBizBean lb = (NamedBizBean) logBean;
		Assert.assertEquals(beanName, lb.name);

		Assert.assertNotNull(lb.getOper());
		lb.getOper().oper("OperBean is autowired");
	}
}
