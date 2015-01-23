package cf.study.java8.javax.cdi.weld.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@Logged
@Interceptor
public class LogInterceptor {
	
	private static final Logger log = Logger.getLogger(LogInterceptor.class);

	@AroundInvoke
	public Object withTransaction(InvocationContext ctx) throws Throwable {
		log.info(String.format("%s.%s(%s)", ctx.getTarget().getClass().getSimpleName(), 
				ctx.getMethod().getName(), 
				StringUtils.join(ctx.getParameters(), ',')));
		Object returnValue = ctx.proceed();
		return returnValue;
	}
}
