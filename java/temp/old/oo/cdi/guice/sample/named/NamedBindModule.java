package cf.study.oo.cdi.guice.sample.named;

import org.junit.Test;

import cf.study.oo.cdi.guice.sample.basic.Payment;
import cf.study.oo.cdi.guice.sample.basic.PaymentCardImpl;
import cf.study.oo.cdi.guice.sample.basic.PaymentCashImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class NamedBindModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Payment.class).annotatedWith(Names.named("Cash")).to(PaymentCashImpl.class);
        bind(Payment.class).annotatedWith(Names.named("Card")).to(PaymentCardImpl.class);
    }

	@Test
	public void test() {
		Injector inj = Guice.createInjector(new NamedBindModule());
        Order order = inj.getInstance(Order.class);
        order.getPaymentCard().pay();

        Guice.createInjector(new NamedBindModule()).getInstance(Order.class).getPaymentCash().pay();
	}
}
