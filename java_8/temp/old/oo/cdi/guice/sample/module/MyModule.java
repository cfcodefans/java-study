package cf.study.oo.cdi.guice.sample.module;

import cf.study.oo.cdi.guice.sample.basic.Order;
import cf.study.oo.cdi.guice.sample.basic.Payment;
import cf.study.oo.cdi.guice.sample.basic.PaymentCashImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class MyModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Payment.class).to(PaymentCashImpl.class);
	}

	public static void main(String[] args) {
		MyModule module = new MyModule();
		Injector injector = Guice.createInjector(module);

		long curTime = System.currentTimeMillis();
		{
			Order order = injector.getInstance(Order.class);
			for (int i = 0; i < 100000; i++) {
				order.finishOrder();
			}
			System.out.println(System.currentTimeMillis() - curTime);
		}

		{
			Order order = new Order();
			order.setPayment(new PaymentCashImpl());
			curTime = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				order.finishOrder();
			}
			System.out.println(System.currentTimeMillis() - curTime);
		}

		{
			curTime = System.currentTimeMillis();
			try {
				Order order = (Order) Order.class.newInstance();
				order.setPayment(new PaymentCashImpl());
				for (int i = 0; i < 100000; i++) {
					order.finishOrder();
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			System.out.println(System.currentTimeMillis() - curTime);
		}
	}

}
