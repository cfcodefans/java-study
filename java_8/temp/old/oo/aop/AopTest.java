package cf.study.oo.aop;

import java.math.BigInteger;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

import cern.colt.Arrays;
import cf.study.misc.algo.sorting.ArraySortBasic;
import cf.study.utils.Trace;
import cf.study.utils.Trace.TraceEntry;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;



public class AopTest implements MethodInterceptor {

	public static class BigIntProvider extends AbstractModule  implements Provider<BigInteger> {
		@Override
		public BigInteger get() {
			return new BigInteger(String.valueOf(Integer.MAX_VALUE), 10);
		}

		@Override
		protected void configure() {
			
		}
	}

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		final String mdName = mi.getMethod().getName();
		TraceEntry te = Trace.start(String.format("%s.%s(%s)", mi.getThis().toString(), mdName, Arrays.toString(mi.getArguments())));

		Object ret = null;
		try {
			ret = mi.proceed();
			Trace.end();
		} catch (Throwable t) {
			Trace.end(te, t);
			throw t;
		}
		return ret;
	}

	@Test
	public void testWeaving() {
		final AopTest at = new AopTest();
		Injector inj = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				this.install(new BigIntProvider());
				this.requestInjection(at);
				this.requestStaticInjection(AopTest.class);
				this.bindInterceptor(Matchers.inPackage(ArraySortBasic.class.getPackage()), Matchers.returns(Matchers.any()), at);
			}
		});
		
//		Date d = inj.getInstance(Date.class);
//		System.out.println(d);
		
		ArraySortBasic sort = inj.getInstance(ArraySortBasic.class);
		sort.testSort();
		System.out.println(Trace.flush());
	}

}
