package cf.study.java8.javax.cdi.weld.beans;

import javax.enterprise.context.RequestScoped;

import org.apache.log4j.Logger;

@RequestScoped
public class BizRequest extends BasicBean {

	private static Logger log = Logger.getLogger(BizRequest.class);
	
	private Object param;

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}
}
