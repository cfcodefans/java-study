package cf.study.java8.javax.cdi.weld.beans.annotated;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import misc.MiscUtils;


import cf.study.java8.javax.cdi.weld.beans.AppBean;
import cf.study.java8.javax.cdi.weld.beans.BasicBean;

@ApplicationScoped
public class ModuleBean extends BasicBean {
	private static Logger log = LoggerFactory.getLogger(ModuleBean.class);
	
	public void onStart(@Observes @Initialized(ApplicationScoped.class) AppBean app) {
		log.info(MiscUtils.invocationInfo());
	}
	
	public void onDestroy(@Observes @Destroyed(ApplicationScoped.class) AppBean app) {
		log.info(MiscUtils.invocationInfo());
	}
	
	public static void onAppStart(@Observes @Initialized(ApplicationScoped.class) AppBean app) {
		log.info(MiscUtils.invocationInfo());
	}
}
