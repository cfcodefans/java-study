package cf.study.java8.javax.cdi.weld.beans;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

//@SessionScoped
@RequestScoped
@SuppressWarnings("rawtypes")
public class BizBean extends BasicBean {

	@Inject
	@Named("infProc")
	protected IBizProcessor<Object, List<Class>> infProc;
	
	private static Logger log = Logger.getLogger(BizBean.class);

	public IBizProcessor<Object, List<Class>> getInfProc() {
		return infProc;
	}
	
	@Inject 
	@Any
	protected Event<BizRequest> serializableReqEvent;
	
	public Object process(BizRequest req) {
		Object param = req.getParam();

		if (param instanceof Serializable) {
			serializableReqEvent.fire(req);
		}
		
//		log.info(req);
		
		return getInfProc().proc(req);
	}
	
	public void cacheSerializableParam(@Observes BizRequest req) {
		log.info(String.format("\n\t %s \n\t", req));
	}
}
