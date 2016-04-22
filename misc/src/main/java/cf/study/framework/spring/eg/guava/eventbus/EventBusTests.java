package cf.study.framework.spring.eg.guava.eventbus;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;

import cf.study.misc.guava.EventBusTests.IEventListener;

@ContextConfiguration("application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Configuration
@EnableAspectJAutoProxy
public class EventBusTests {
	static final Logger log = Logger.getLogger(EventBusTests.class);
	
	@Inject
	EventBus eb;
	
	IEventListener<DeadEvent> deadEventHandler = (DeadEvent de)->{
		log.error(String.format("detected dead event: %s", de));
		return de;
	};

	@Test
	public void verifyCfgs() {
		Assert.assertNotNull(eb);
		log.info(eb);
	}

	
}
