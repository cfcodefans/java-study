package cf.study.java.javax.cdi.weld.beans.annotated;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import misc.MiscUtils;


public class BlankBean {

	@Inject 
	public long id;
	private static final Logger log = LoggerFactory.getLogger(BlankBean.class);

	public BlankBean() {
		log.info(MiscUtils.invocationInfo());
	}
}
