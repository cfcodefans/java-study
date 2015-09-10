package cf.study.oo.cdi.guice.sample.simple;

public class PaymentCashImpl implements Payment {

	@Override
	public void pay() {
		System.out.println("I'll pay just plain cash");
	}

}
