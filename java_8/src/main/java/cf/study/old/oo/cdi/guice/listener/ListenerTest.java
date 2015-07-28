package cf.study.oo.cdi.guice.listener;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ListenerTest {
	@Test
	public void testListener() {
		Injector inj = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
			}
		});
	}
}
