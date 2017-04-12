package cf.study.oo.cdi.guice.sample.simple;

public class PaymentCardImpl implements Payment {

	@Override
	public void pay() {
		System.out.println("I'll pay with a credit card");
	}

}
