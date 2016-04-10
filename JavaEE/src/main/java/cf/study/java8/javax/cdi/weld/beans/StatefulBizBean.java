package cf.study.java8.javax.cdi.weld.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import misc.MiscUtils;

@SessionScoped
public class StatefulBizBean extends BasicBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(StatefulBizBean.class);
	
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
