package cf.study.java8.javax.cdi.weld.beans;

import javax.enterprise.context.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cf.study.java8.javax.cdi.weld.interceptor.Logged;
import misc.MiscUtils;

@RequestScoped
public class BizRequest extends BasicBean {

	private static Logger log = LoggerFactory.getLogger(BizRequest.class);
	
	private Object param;

	@Logged
	public Object getParam() {
		return param;
	}

	@Logged
	public void setParam(Object param) {
		this.param = param;
	}
	
	public BizRequest() {
		log.info(MiscUtils.invocationInfo());
	}

	@Override
	public String toString() {
		return "BizRequest [getParam()=" + getParam() + ", getCreatedTime()=" + getCreatedTime() + ", getId()=" + getId() + ", getName()=" + getName() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + ", getClass()=" + getClass() + "]";
	}
}
