package cf.study.java8.javax.cdi.weld.beans;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("rawtypes")
public class MethodProcessor implements IBizProcessor<Object, List<Method>> {

	@Override
	public List<Method> proc(final Object param) {
		if (param == null) {
			return Collections.emptyList();
		}
		
		final Class cls = param.getClass();
		
		return Arrays.asList(cls.getMethods());
	}
}
