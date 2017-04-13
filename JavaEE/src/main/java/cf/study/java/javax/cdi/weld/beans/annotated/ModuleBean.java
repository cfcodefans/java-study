package cf.study.java.javax.cdi.weld.beans.annotated;

import java.beans.PropertyChangeEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cf.study.java.javax.cdi.weld.beans.AppBean;
import cf.study.java.javax.cdi.weld.beans.BasicBean;
import misc.MiscUtils;

@ApplicationScoped
public class ModuleBean extends BasicBean {
	private static Logger log = LogManager.getLogger(ModuleBean.class);
	
	public void onStart(@Observes @Initialized(ApplicationScoped.class) AppBean app) {
		log.info(MiscUtils.invocationInfo());
	}
	
	public void onDestroy(@Observes @Destroyed(ApplicationScoped.class) AppBean app) {
		log.info(MiscUtils.invocationInfo());
	}
	
	public static void onAppStart(@Observes @Initialized(ApplicationScoped.class) AppBean app) {
		log.info(MiscUtils.invocationInfo());
	}
	
	public static void onInterested(@Observes PropertyChangeEvent pce) {
		log.info(MiscUtils.invocationInfo() + "\n\t" + pce);
	}
}
