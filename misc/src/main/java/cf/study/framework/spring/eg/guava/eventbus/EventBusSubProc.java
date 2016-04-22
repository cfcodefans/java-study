package cf.study.framework.spring.eg.guava.eventbus;

import javax.inject.Inject;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;

import misc.MiscUtils;

@Component
public class EventBusSubProc implements BeanPostProcessor {
	@Inject
	public EventBus eb;
	
	public EventBusSubProc() {
		EventBusTests.log.info(MiscUtils.invocationInfo());
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return null;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		EventBusTests.log.info(String.format("register bean %s named %s as listener", bean.getClass(), beanName));
		eb.register(bean);
		return bean;
	}
}