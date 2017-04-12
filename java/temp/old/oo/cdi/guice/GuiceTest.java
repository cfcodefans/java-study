package cf.study.oo.cdi.guice;

import javax.inject.Inject;

import org.junit.Assert;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

public class GuiceTest {
	@Test
	public void testInject() {
		System.out.println("test");
		
		Injector inj = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {//be careful, super overkill!
				super.bind(TypeLiteral.get(String.class)).toInstance("injected value");
			}
		});
		
		Cls_1 cls_1_instance = inj.getInstance(Cls_1.class);
		Assert.assertNotNull(cls_1_instance.getCls_2_instance());
		Assert.assertEquals(cls_1_instance.getCls_2_instance().getValue(), "injected value");
		
		String strVal = inj.getInstance(String.class);
		System.out.println(strVal);
	}
	
	public static class Cls_1 {
		@Inject
		private Cls_2 cls_2_instance;
		
		public Cls_2 getCls_2_instance() {
			return cls_2_instance;
		}
	}
	
	public static class Cls_2 {
		@Inject
		private String value;
		
		public String getValue() {
			return value;
		}
	}
}

