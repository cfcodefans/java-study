package cf.study.oo.cdi.guice.sample.annotation;

import org.junit.Test;

import com.google.inject.Guice;

public class BindAnnotationTest {
	@Test
	public void test() {
		Guice.createInjector(new BindAnnotationModule()).getInstance(Order.class).finishOrder();
	}
}
