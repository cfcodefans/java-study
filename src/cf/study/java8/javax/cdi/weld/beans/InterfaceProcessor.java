package cf.study.java8.javax.cdi.weld.beans;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("rawtypes")
public class InterfaceProcessor implements IBizProcessor<Object, List<Class>> {

	@Override
	public List<Class> proc(final Object param) {
		if (param == null) {
			return Collections.emptyList();
		}
		
		return Arrays.asList(param.getClass().getInterfaces());
	}
}
