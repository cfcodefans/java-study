package cf.study.java8.javax.cdi.weld.beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import misc.MiscUtils;


@ApplicationScoped
public class AppBean extends BasicBean {

	private static Logger log = LoggerFactory.getLogger(AppBean.class);
	
	@Inject
	@Initialized(ApplicationScoped.class)
	protected Event<AppBean> startupEvent;
	

	@Inject
	@Destroyed(ApplicationScoped.class)
	protected Event<AppBean> destoryEvent;
	
	@PostConstruct
	public void postConstruct() {
		log.info(MiscUtils.invocationInfo() + "\n\t" + this);
		startupEvent.fire(this);
	}
	
	@PreDestroy
	public void preDestroy() {
		log.info(MiscUtils.invocationInfo() + "\n\t" + this);
		destoryEvent.fire(this);
	}
}
