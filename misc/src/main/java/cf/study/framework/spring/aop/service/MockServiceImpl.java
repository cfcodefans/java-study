package cf.study.framework.spring.aop.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import misc.MiscUtils;

@Component
public class MockServiceImpl implements IMockService {

	public final static Logger log = LogManager.getLogger(MockServiceImpl.class);

	@Override
	public void foo() {
		log.info(MiscUtils.invocationInfo());
	}
}
