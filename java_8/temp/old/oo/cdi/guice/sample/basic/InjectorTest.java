package cf.study.oo.cdi.guice.sample.basic;

import javax.inject.Inject;

import org.junit.Assert;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class InjectorTest {
	public static class Target {
		@Inject
		@Named("firstName")
		public String firstName;
		@Inject
		@Named("lastName")
		public String lastName;
	}
	
	@Test
	public void testInjector() {
		Target target = new Target();
		Injector createInjector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(String.class).annotatedWith(Names.named("firstName")).toInstance("Chen");
				bind(String.class).annotatedWith(Names.named("lastName")).toInstance("Fan");
			}
		});
		
		{
			long t = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				createInjector.injectMembers(target);
			}
			System.out.println(System.currentTimeMillis() - t);
		}
		
		{
			long t = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				target.firstName = "Chen";
				target.lastName = "Fan";
			}
			System.out.println(System.currentTimeMillis() - t);
		}
		
		Assert.assertEquals("Chen", target.firstName);
	}
}
