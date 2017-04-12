package cf.study.oo.cdi.guice.sample.basic;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class SampleTest {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector();
		Order order = injector.getInstance(Order.class);
		order.finishOrder();
	}
}
