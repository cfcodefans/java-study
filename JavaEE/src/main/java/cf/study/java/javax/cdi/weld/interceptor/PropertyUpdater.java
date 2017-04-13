package cf.study.java.javax.cdi.weld.interceptor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Interested
@Interceptor
public class PropertyUpdater {

	private static Logger log = LogManager.getLogger(PropertyUpdater.class);

	private static ConcurrentMap<Class<?>, ConcurrentMap<Method, PropertyDescriptor>> cache = new ConcurrentHashMap<>();

	public static ConcurrentMap<Method, PropertyDescriptor> compute(Class<?> cls) {
		if (cls == null) {
			return null;
		}

		Map<Method, PropertyDescriptor> collect = new HashMap();
		Stream.of(PropertyUtils.getPropertyDescriptors(cls)).filter(pd -> pd.getWriteMethod() != null).forEach(pd -> collect.put(pd.getWriteMethod(), pd));
		ConcurrentMap<Method, PropertyDescriptor> methodAndPropertyDescriptors = new ConcurrentHashMap<Method, PropertyDescriptor>(collect);
		return methodAndPropertyDescriptors;
	}

	@Inject
	@Initialized(ApplicationScoped.class)
	protected Event<PropertyChangeEvent> ev;

	@AroundInvoke
	public Object update(InvocationContext ctx) throws Throwable {
		Method md = ctx.getMethod();

		ConcurrentMap<Method, PropertyDescriptor> mps = cache.computeIfAbsent(md.getDeclaringClass(), PropertyUpdater::compute);
		PropertyDescriptor pd = mps.get(md);

		Object oldValue = null;
		Object target = ctx.getTarget();
		if (pd == null) {
			log.error("can't handle this method:\t%s", md);
		} else {
			oldValue = pd.getReadMethod().invoke(target);
		}

		ev.fire(new PropertyChangeEvent(target, pd.getDisplayName(), oldValue, ctx.getParameters()[0]));

		return ctx.proceed();
	}
}
