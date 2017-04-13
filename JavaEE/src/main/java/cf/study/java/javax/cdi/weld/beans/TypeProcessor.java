package cf.study.java.javax.cdi.weld.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@SuppressWarnings("rawtypes")
@ApplicationScoped
@Named("typeProc")
public class TypeProcessor implements IBizProcessor<Object, List<Class>> {

	@Override
	public List<Class> proc(final Object param) {
		if (param == null) {
			return Collections.emptyList();
		}
		
		final List<Class> reList = new ArrayList<Class>();
		for (Class cls = param.getClass(); !cls.getSuperclass().equals(Object.class); cls = cls.getSuperclass()) {
			reList.add(cls);
		}
		reList.add(Object.class);
		return reList;
	}

}
