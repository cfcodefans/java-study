package cf.study.oo.cdi.guice;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ConstructorInjectionTest {

	@Test
	public void testConstructorInjection() {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				this.bind(String.class).toInstance("String");
			}
		});
		
		BaseDao bd = injector.getInstance(BaseDao.class);
		Assert.assertEquals("String", bd.getEm());
	}
	
}

class BaseDao {
	
	private String em;
	
	@Inject
	public BaseDao(String em) {
		super();
		this.em = em;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public Object getEm() {
		return em;
	}
}
