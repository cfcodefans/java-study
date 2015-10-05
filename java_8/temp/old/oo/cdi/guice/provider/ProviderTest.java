package cf.study.oo.cdi.guice.provider;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import cf.study.oo.cdi.guice.interceptor.Target;
import cf.study.oo.cdi.guice.interceptor.TestedInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

public class ProviderTest {

	private Injector injector;
	private Module setTargetMethod;
	private AbstractModule addInterceptor;

	@Before
	public void init() {
		setTargetMethod = new AbstractModule() {
			@Override
			protected void configure() {
				bind(String.class).annotatedWith(Names.named(TestedInterceptor.TARGET_METHOD)).toInstance("targetedMethod");
				install(new TargetFactory());
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
		
		injector = Guice.createInjector(setTargetMethod);
	}
	
	@Test
	public void testProvider() {
		Component c = injector.getInstance(Component.class);
		c.procTarget();
	}

}

class TargetFactory extends AbstractModule implements Provider<Target> {
	
	public Target get() {
		Target t = new Target();
		super.requestInjection(t);
		return t;
	}

	protected void configure() {
		TestedInterceptor testedInterceptor = new TestedInterceptor();
		super.requestInjection(testedInterceptor);
		bindInterceptor(Matchers.subclassesOf(Target.class), Matchers.any(), testedInterceptor);
	}
}

class Component {
	@Inject
	private Provider<Target> targetFactory;
	
	public void procTarget() {
		Target t = targetFactory.get();
		t.targetedMethod();
	}
}