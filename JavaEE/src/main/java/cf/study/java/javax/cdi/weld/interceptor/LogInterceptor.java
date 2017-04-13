package cf.study.java.javax.cdi.weld.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Logged
@Interceptor
public class LogInterceptor {
	
	private static Logger log = LoggerFactory.getLogger(LogInterceptor.class);

	@AroundInvoke
	public Object log(InvocationContext ctx) throws Throwable {
		log.info(String.format("%s.%s(%s)", ctx.getTarget().getClass().getSimpleName(), 
				ctx.getMethod().getName(), 
				StringUtils.join(ctx.getParameters(), ',')));
		Object returnValue = ctx.proceed();
		return returnValue;
	}
}
