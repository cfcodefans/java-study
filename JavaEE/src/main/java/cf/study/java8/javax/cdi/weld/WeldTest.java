package cf.study.java8.javax.cdi.weld;

import java.util.List;
import java.util.Set;

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
import org.jboss.weld.literal.DefaultLiteral;
import org.jboss.weld.literal.NamedLiteral;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.java8.javax.cdi.weld.beans.AppBean;
import cf.study.java8.javax.cdi.weld.beans.BizBean;
import cf.study.java8.javax.cdi.weld.beans.BizRequest;
import cf.study.java8.javax.cdi.weld.beans.IBizProcessor;
import cf.study.java8.javax.cdi.weld.beans.annotated.AnyBean;
import cf.study.java8.javax.cdi.weld.beans.annotated.AppScopedBean;
import cf.study.java8.javax.cdi.weld.beans.annotated.BlankBean;
import cf.study.java8.javax.cdi.weld.beans.annotated.DefaultBean;
import cf.study.java8.javax.cdi.weld.beans.annotated.NamedBean;
import cf.study.java8.javax.cdi.weld.beans.annotated.ReqScopedBean;

public class WeldTest {

	public static Weld weld;
	public static WeldContainer container;
	public static BeanManager bm;
	@SuppressWarnings("rawtypes")
	public static CDI _CDI;

	// @Inject

	@BeforeClass
	public static void setUp() {
		try {
			weld = new Weld();
			container = weld.initialize();
			bm = container.getBeanManager();
			_CDI = CDI.current();
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
	public void testDefaultBean() throws Exception {
		System.out.println(bm.getBeans(DefaultBean.class));
		System.out.println(bm.getBeans(DefaultBean.class, DefaultLiteral.INSTANCE));
		System.out.println(bm.getBeans(AnyBean.class));
		System.out.println(bm.getBeans(AnyBean.class, AnyLiteral.INSTANCE));
		System.out.println(bm.getBeans(NamedBean.class));
		System.out.println(bm.getBeans(NamedBean.class, NamedLiteral.DEFAULT));
		
		System.out.println(bm.getBeans(AppScopedBean.class));
		System.out.println(bm.getBeans(AppScopedBean.class, UnboundLiteral.INSTANCE));
		
		System.out.println(bm.getBeans(ReqScopedBean.class));
		System.out.println(bm.getBeans(ReqScopedBean.class, UnboundLiteral.INSTANCE));
	}
	
	@Test
	public void testBizReq() throws Exception {
		/**
		 * In this case Weld is using unbound RequestContext that is associated with a thread (RequestContext). 
		 * You need to manually initialize new RequestContext in a thread that You're creating
		 */
		RequestContext reqCtx = (RequestContext) _CDI.select(RequestContext.class, UnboundLiteral.INSTANCE).get();
		reqCtx.activate();
		
		System.out.println(bm.getBeans("bizRequest"));
		Set<Bean<?>> bizReqs = bm.getBeans(BizRequest.class);
		System.out.println(bizReqs);
		bizReqs.forEach((bean) -> {
			System.out.println(ToStringBuilder.reflectionToString(bean, ToStringStyle.MULTI_LINE_STYLE));
			System.out.println(bm.getReference(bean, BizRequest.class, bm.createCreationalContext(bean)));
		});
	}
	
	@Test
	public void testSelectClzzByScope() {
		bm.getBeans(BlankBean.class, AnyLiteral.INSTANCE).stream()
			.map(b->ToStringBuilder.reflectionToString(b, ToStringStyle.MULTI_LINE_STYLE))
			.forEach(System.out::println);
	}

	
	@Test
	public void testInterceptor() {
		//in JAVA SE, only application scope and request scope are available
		RequestContext reqCtx = (RequestContext) _CDI.select(RequestContext.class, UnboundLiteral.INSTANCE).get();
		reqCtx.activate();
		BizRequest req = getBean(BizRequest.class);
		req.setParam("param 1");
	}
	
	@Test
	public void testInjection() {
		RequestContext rc = (RequestContext)_CDI.select(RequestContext.class, UnboundLiteral.INSTANCE).get();
		rc.activate();
		BizBean sb = getBean(BizBean.class);
		IBizProcessor<Object, List<Class>> infProc = sb.getInfProc();
		
		System.out.println(infProc.getClass());
		
		infProc.proc(new String()).forEach(System.out::println);
	}
	
	@Test
	public void testEvent()	{
		RequestContext rc = (RequestContext)_CDI.select(RequestContext.class, UnboundLiteral.INSTANCE).get();
		rc.activate();
		BizBean sb = getBean(BizBean.class);
		
		BizRequest req = getBean(BizRequest.class);
		req.setParam(new Object());
		
		sb.process(req);
		
		BizRequest req1 = getBean(BizRequest.class);
		req1.setParam(new String("abc"));
		sb.process(req1);
	}
	
	@AfterClass
	public static void tearDown() {
		if (weld != null)
			weld.shutdown();
	}
	
	public static <T> T getBean(Class<T> cls) {
		return CDI.current().select(cls).get();
	}
	
	public static <T> T getBeanInReqScope(Class<T> cls) {
		RequestContext reqCtx = (RequestContext) _CDI.select(RequestContext.class, UnboundLiteral.INSTANCE).get();
		reqCtx.activate();
		return CDI.current().select(cls).get();
	}
}
