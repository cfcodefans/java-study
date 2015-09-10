package cf.study.java8.javax.cdi.weld.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

import misc.MiscUtils;

import org.apache.log4j.Logger;

@SessionScoped
public class StatefulBizBean extends BasicBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(StatefulBizBean.class);
	
	private Object state;

	public Object getState() {
		return state;
	}

	public void setState(Object state) {
		this.state = state;
	}
	
	public StatefulBizBean() {
		log.info(MiscUtils.invocationInfo());
	}
}
