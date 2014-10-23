package cf.study.java8.cdi.weld.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;

@RequestScoped
public class BizRequest extends BasicBean {

	@Inject
	private static Logger log;
	
	private Object param;

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}
}
