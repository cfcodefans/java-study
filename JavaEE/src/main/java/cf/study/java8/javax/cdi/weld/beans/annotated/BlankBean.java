package cf.study.java8.javax.cdi.weld.beans.annotated;

import javax.inject.Inject;

import misc.MiscUtils;

import org.apache.log4j.Logger;

public class BlankBean {

	@Inject 
	public long id;
	private static final Logger log = Logger.getLogger(BlankBean.class);

	public BlankBean() {
		log.info(MiscUtils.invocationInfo());
	}
}
