package cf.study.java8.javax.cdi.weld.beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import misc.MiscUtils;

import org.apache.log4j.Logger;

@ApplicationScoped
public class AppBean extends BasicBean {

	private static Logger log = Logger.getLogger(AppBean.class);
	
	@PostConstruct
	public void postConstruct() {
		log.info(MiscUtils.invocationInfo() + "\n\t" + this);
	}
	
	@PreDestroy
	public void preDestroy() {
		log.info(MiscUtils.invocationInfo() + "\n\t" + this);
	}
	
	
}
