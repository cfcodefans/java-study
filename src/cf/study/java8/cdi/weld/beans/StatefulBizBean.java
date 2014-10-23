package cf.study.java8.cdi.weld.beans;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;

@SessionScoped
public class StatefulBizBean extends BasicBean {

	@Inject
	private static Logger log;
	
	private Object state;

	public Object getState() {
		return state;
	}

	public void setState(Object state) {
		this.state = state;
	}
}
