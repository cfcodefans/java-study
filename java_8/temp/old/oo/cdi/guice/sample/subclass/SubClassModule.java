package cf.study.oo.cdi.guice.sample.subclass;

import cf.study.oo.cdi.guice.sample.basic.Payment;
import cf.study.oo.cdi.guice.sample.basic.PaymentCardImpl;

import com.google.inject.AbstractModule;

public class SubClassModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Payment.class).to(PaymentCardImpl.class);
        bind(PaymentCardImpl.class).to(PaymentVisaCard.class);
    }
}
