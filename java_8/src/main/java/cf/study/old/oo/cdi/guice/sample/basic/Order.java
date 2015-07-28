package cf.study.oo.cdi.guice.sample.basic;

import com.google.inject.Inject;

public class Order {

    private Payment payment;

    public Payment getPayment() {
        return payment;
    }

    @Inject
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void finishOrder(){
        this.payment.pay();
    }
}
