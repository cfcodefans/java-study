package cf.study.java8.javax.cdi.weld.beans.annotated;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AppScopedBean {

	@Inject 
	protected long id;
}
