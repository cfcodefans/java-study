package cf.study.framework.spring;

import java.util.Arrays;

import misc.MiscUtils;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;

public class AppCtxTests {

	static StaticApplicationContext sac;

	@BeforeClass
	public static void setUp() throws Exception {
		sac = new StaticApplicationContext();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		if (sac != null && sac.isActive()) {
			sac.close();
		}
	}

	static final Logger log = Logger.getLogger(AppCtxTests.class);

	public static interface IOperBean {
		Object oper(Object...params);
	}
	
	public static class BizBean {
		public static final String BEAN_NAME = "bizBean";

		public BizBean() {
			AppCtxTests.log.info(MiscUtils.invocationInfo());
		}
		
		private IOperBean oper;

		public IOperBean getOper() {
			return oper;
		}

		public void setOper(IOperBean oper) {
			this.oper = oper;
		}
	}
	
	public static class LogOper implements IOperBean {
		public Object oper(Object...params) {
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
	
	@Test
	public void registerBeanByName() {
		// Bean class must be unique in ApplicationContext
		sac.registerBeanDefinition(BizBean.BEAN_NAME, new RootBeanDefinition(BizBean.class));

		//if one class is registered in ApplicationContext repeatedly,
		//Its instance can't be created by ApplicationContext by its class
		//If one class and its sub-class are registered in ApplicationContext,
		//Its instance can't be created by ApplicationContext by its class
//		BizBean beanByClass = sac.getBean(BizBean.class);
//		Assert.assertNotNull(beanByClass);

		Object beanByName = sac.getBean(BizBean.BEAN_NAME);
		Assert.assertTrue(beanByName instanceof BizBean);
	}
	
	@Test
	public void registerBeanWithConstructorParams() {
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addIndexedArgumentValue(0, "named");
		sac.registerBeanDefinition(NamedBizBean.BEAN_NAME, new RootBeanDefinition(NamedBizBean.class, cav, null));
		
		Object namedBeanByName = sac.getBean(NamedBizBean.BEAN_NAME);
		Assert.assertTrue(namedBeanByName instanceof NamedBizBean);
		NamedBizBean namedBizBean = (NamedBizBean) namedBeanByName;
		Assert.assertEquals("named", namedBizBean.name);
	}
	
	@Test
	public void registerBeanWithConstructorParamsAndProperty() {
		sac.registerBeanDefinition("logOper", new RootBeanDefinition(LogOper.class));
		
		String beanName = "logBizBean";

		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addIndexedArgumentValue(0, beanName);
		
		MutablePropertyValues mpv = new MutablePropertyValues(Arrays.asList(new PropertyValue("oper", sac.getBean("logOper"))));
		
		RootBeanDefinition rbd = new RootBeanDefinition(NamedBizBean.class, cav, mpv);
		rbd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
		sac.registerBeanDefinition(beanName, rbd);
		
		Object logBean = sac.getBean(beanName);
		Assert.assertTrue(logBean instanceof NamedBizBean);
		NamedBizBean lb = (NamedBizBean) logBean;
		Assert.assertEquals(beanName, lb.name);
		
		Assert.assertNotNull(lb.getOper());
		lb.getOper().oper("OperBean is autowired");
	}
}
