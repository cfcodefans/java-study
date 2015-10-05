package cf.study.oo.cdi.guice.sample.named;

import cf.study.oo.cdi.guice.sample.basic.Payment;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Order {
	private Payment paymentCash;
	private Payment paymentCard;

	@Inject
	public void setPaymentCash(@Named("Cash") Payment payment) {
		this.paymentCash = payment;
	}

	public Payment getPaymentCash() {
		return paymentCash;
	}

	@Inject
	public void setPaymentCard(@Named("Card") Payment payment) {
		this.paymentCard = payment;
	}

	public Payment getPaymentCard() {
		return paymentCard;
	}
}
