package cf.study.oo.cdi.guice.interceptor;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

public class InterceptorTest {
	
	private Injector injector;
	private Module setTargetMethod;
	private AbstractModule addInterceptor;

	@Before
	public void init() {
		setTargetMethod = new AbstractModule() {
			@Override
			protected void configure() {
				bind(String.class).annotatedWith(Names.named(TestedInterceptor.TARGET_METHOD)).toInstance("targetMethod");
			}
		};
		
		addInterceptor = new AbstractModule() {
			@Override
			protected void configure() {
				TestedInterceptor testedInterceptor = new TestedInterceptor();
				super.requestInjection(testedInterceptor);
				bindInterceptor(Matchers.subclassesOf(Target.class), Matchers.any(), testedInterceptor);
			}
		};
		injector = Guice.createInjector(setTargetMethod, addInterceptor);
	}

	@Test
	public void testInterceptor() {
		Target target = injector.getInstance(Target.class);
		System.out.println(target.targetedMethod());
	}

	@Test
	public void testPref() {
		{
			Target target = injector.getInstance(Target.class);
			long t = System.currentTimeMillis();
			for (int i = 0; i < 50000; i++) {
				target.targetedMethod();
			}
			System.out.println(System.currentTimeMillis() - t);
		}
		
		{
			Target target = new Target();
			TargetWrapper tw = new TargetWrapper(target);
			long t = System.currentTimeMillis();
			for (int i = 0; i < 50000; i++) {
				tw.targetedMethod();
			}
			System.out.println(System.currentTimeMillis() - t);
		}
	}
}

class TargetWrapper extends Target {
	private Target target;
	
	public TargetWrapper(Target t) {
		this.target = t;
	}
	
	public String targetMethod() {
		return target.targetedMethod();
	}
	
}