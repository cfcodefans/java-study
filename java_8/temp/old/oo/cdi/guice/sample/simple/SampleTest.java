package cf.study.oo.cdi.guice.sample.simple;

public class SampleTest {
	   public static void main(String[] args) {
	        Order order = new Order();
	        Payment payment = new PaymentCardImpl();
	        order.setPayment(payment);
	        order.finishOrder();
	    }
}
