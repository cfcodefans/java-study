package cf.study.oo.cdi.guice.sample.simple;

public class Order {

    private Payment payment;

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void finishOrder(){
        this.payment.pay();
    }
}
