package cf.study.oo.cdi.guice.sample.subclass;

import cf.study.oo.cdi.guice.sample.basic.PaymentCardImpl;

public class PaymentVisaCard extends PaymentCardImpl {
	public void pay() {
		System.out.println("I'll pay with a card from Visa");
	}
}
