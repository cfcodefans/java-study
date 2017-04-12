package cf.study.oo.dynamic.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class CgLibTest {

	static Enhancer enhancer = new Enhancer();

	@Test
	public void testGeneratedClass() {
		configEnhancer(LdapUserEntry.class);

		{
			long t = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				IEntity en = (IEntity) enhancer.create();
				en.getProperty("sn");
			}
			System.out.println(System.currentTimeMillis() - t);
		}

		{
			long t = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				LdapUserEntry en = new LdapUserEntry();
				en.getAttr("sn");
			}
			System.out.println(System.currentTimeMillis() - t);
		}
	}

	public static <T> void configEnhancer(Class<T> cls) {
		Callback[] callbacks = new Callback[] { new MethodInterceptorImpl(cls),
				NoOp.INSTANCE };
		enhancer.setSuperclass(cls);
		enhancer.setInterfaces(new Class[] { IEntity.class });
		enhancer.setCallbacks(callbacks);
		enhancer.setCallbackFilter(new CallbackFilterImpl());
	}

	private static class MethodInterceptorImpl implements MethodInterceptor {

		Class cls = null;

		public MethodInterceptorImpl(Class cls) {
			super();
			this.cls = cls;
		}

		public Object intercept(Object obj, Method method, Object[] aobj,
				MethodProxy methodproxy) throws Throwable {
			String propertyName = aobj[0].toString();
			String methodName = "get" + StringUtils.capitalize(propertyName);
			MethodProxy targetMethodProxy = MethodProxy.create(cls, cls, "",
					methodName, methodName);
			System.out.println(targetMethodProxy.getSignature());
			return targetMethodProxy.invoke(obj, new Object[0]);
		}
	}

	private static class CallbackFilterImpl implements CallbackFilter {
		public int accept(Method method) {
			return "getProperty".equals(method.getName()) ? 0 : 1;
		}
	}
}

class LdapDao implements IDao {

	EntityMetadata userMetadata = new EntityMetadata("cn", "cn", "sn",
			"userPassword");

	public LdapDao(Class entityClass) {
	}

	public String getId(Object entity) {

		return null;
	}

	public String getProperty(String propertyName, Object entity) {
		return null;
	}

}