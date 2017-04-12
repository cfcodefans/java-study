package cf.study.oo.cdi.guice.interceptor;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;

import com.google.inject.name.Named;

public class TestedInterceptor implements MethodInterceptor {

	public static final String TARGET_METHOD = "targetedMethod";
	@Inject
	@Named(TARGET_METHOD)
	private String targetMethodName;
	
	public Object invoke(MethodInvocation method) throws Throwable {
		if (!StringUtils.equals(targetMethodName, method.getMethod().getName())) {
			return method.proceed();
		}
		
		System.out.println(String.format("Intercepted: %s.%s(%s)",
				method.getThis().getClass().toString(),
				method.getMethod().getName(),
				StringUtils.join(method.getArguments())));
		return method.proceed();
	}

}
