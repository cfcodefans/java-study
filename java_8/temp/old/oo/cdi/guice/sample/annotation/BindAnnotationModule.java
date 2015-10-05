package cf.study.oo.cdi.guice.sample.annotation;

import cf.study.oo.cdi.guice.sample.basic.Payment;
import cf.study.oo.cdi.guice.sample.basic.PaymentCardImpl;
import cf.study.oo.cdi.guice.sample.basic.PaymentCashImpl;

import com.google.inject.AbstractModule;

public class BindAnnotationModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Payment.class).annotatedWith(Cash.class).to(PaymentCashImpl.class);
		bind(Payment.class).annotatedWith(Card.class).to(PaymentCardImpl.class);
	}

}
