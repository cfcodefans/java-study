package cf.study.oo.cdi;

import javax.inject.Inject;

import org.junit.Test;


public class InjectionTest {
	
	@Inject
	IHelloWorld hw;
	
	@Test
	public void testHelloWorld() {
		hw.helloWorld();
	}
}
