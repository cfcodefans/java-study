package cf.study.oo.cdi.guice.sample.annotation;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;

public class InterfaceAnnotationTest {

	@Test
	public void testImplementedBy() {
		Injector inj = Guice.createInjector();
		IHelloWorld hw = inj.getInstance(IHelloWorld.class);
		System.out.println(hw.say());
	}
}

@ImplementedBy(ChineseHelloWorld.class)
interface IHelloWorld {
	String say();
}

class ChineseHelloWorld implements IHelloWorld {

	public String say() {
		return "Ni Hao";
	}

}