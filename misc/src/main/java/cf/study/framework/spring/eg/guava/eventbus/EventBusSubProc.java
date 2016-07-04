package cf.study.framework.spring.eg.guava.eventbus;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

import com.google.common.eventbus.EventBus;


import misc.MiscUtils;

@Component
public class EventBusSubProc extends CommonAnnotationBeanPostProcessor {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(EventBusSubProc.class);
	@Inject
	public EventBus eb;

	public EventBusSubProc() {
		log.info(MiscUtils.invocationInfo());
	}


	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		log.info(String.format("register bean %s named %s as listener", bean.getClass(), beanName));
		eb.register(bean);
		return bean;
	}
}