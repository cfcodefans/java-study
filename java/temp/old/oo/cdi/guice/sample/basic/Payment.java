package cf.study.oo.cdi.guice.sample.basic;

import com.google.inject.ImplementedBy;

@ImplementedBy(PaymentCardImpl.class)
public interface Payment {

	public void pay();

}
