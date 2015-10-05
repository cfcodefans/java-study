package cf.study.oo.cdi.guice.sample.subclass;

import org.junit.Test;

import cf.study.oo.cdi.guice.sample.basic.Payment;

import com.google.inject.Guice;

public class SubClassTest {
	@Test
	public void testSubClassBind() {
		Guice.createInjector(new SubClassModule()).getInstance(Payment.class).pay();
	}
}
