package cf.study.java8.javax.cdi.weld.beans.annotated;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class ReqScopedBean {

	@Inject 
	protected long id;
}
