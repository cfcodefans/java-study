package cf.study.java8.javax.cdi.weld;

import java.util.Set;
import java.util.function.Consumer;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.unbound.UnboundLiteral;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.literal.AnyLiteral;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.java8.javax.cdi.weld.beans.AppBean;
import cf.study.java8.javax.cdi.weld.beans.BizRequest;

public class WeldTest {

	public static Weld weld;
	public static WeldContainer container;
	public static BeanManager bm;
	public static CDI cdi;

	// @Inject

	@BeforeClass
	public static void setUp() {
		try {
			weld = new Weld();
			container = weld.initialize();
			bm = container.getBeanManager();
			cdi = CDI.current();
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Test
	public void testAppBean() throws Exception {
		System.out.println(bm.getBeans("appBean"));
		Set<Bean<?>> appBeans = bm.getBeans(AppBean.class);
		System.out.println(appBeans);
		appBeans.forEach((bean) -> {
			System.out.println(ToStringBuilder.reflectionToString(bean, ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(bm.getReference(bean, AppBean.class, bm.createCreationalContext(bean)));
		});
	}
	
	@Test
	public void testBizReq() throws Exception {
		/**
		 * In this case Weld is using unbound RequestContext that is associated with a thread (RequestContext). 
		 * You need to manually initialize new RequestContext in a thread that You're creating
		 */
		RequestContext reqCtx = (RequestContext) cdi.select(RequestContext.class, UnboundLiteral.INSTANCE).get();
		reqCtx.activate();
		
		System.out.println(bm.getBeans("bizRequest"));
		Set<Bean<?>> bizReqs = bm.getBeans(BizRequest.class);
		System.out.println(bizReqs);
		bizReqs.forEach((bean) -> {
			System.out.println(ToStringBuilder.reflectionToString(bean, ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(bm.getReference(bean, BizRequest.class, bm.createCreationalContext(bean)));
		});
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSelectClzzByScope() {
		Consumer printer = (i) -> {
			try {
				System.out.println(ToStringBuilder.reflectionToString(i, ToStringStyle.MULTI_LINE_STYLE));
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
//		cdi.forEach(printer);
		
		bm.getBeans(Object.class, AnyLiteral.INSTANCE).forEach(printer);
	}

	@AfterClass
	public static void tearDown() {

		if (weld != null)
			weld.shutdown();
	}
	
	public static <T> T getBean(Class<T> cls) {
		return CDI.current().select(cls).get();
	}
}
