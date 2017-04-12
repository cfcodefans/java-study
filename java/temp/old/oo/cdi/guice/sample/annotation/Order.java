package cf.study.oo.cdi.guice.sample.annotation;

import cf.study.oo.cdi.guice.sample.basic.Payment;

import com.google.inject.Inject;

public class Order {
	@Inject
	private @Card
	Payment payment;

	// public Payment getPayment() {
	// return payment;
	// }
	//
	// public void setPayment(Payment payment) {
	// this.payment = payment;
	// }

	public void finishOrder() {
		this.payment.pay();
	}
}
