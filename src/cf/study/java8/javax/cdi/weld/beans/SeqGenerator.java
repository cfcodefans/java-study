package cf.study.java8.javax.cdi.weld.beans;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import misc.MiscUtils;

import org.apache.log4j.Logger;

@Singleton
public class SeqGenerator {
	@Inject
	private static Logger log;
	
	private AtomicLong seq;
	
	@Produces
	public Long getSeq() {
		final long newId = seq.incrementAndGet();
		log.info(String.format("%s \t-> \t{newId: %d}", MiscUtils.invocationInfo(), newId));
		return newId;
	}
	
	@PostConstruct
	public void postConstruct() {
		log.info(MiscUtils.invocationInfo());
		seq = new AtomicLong();
	}
	
	@PreDestroy
	public void preDestroy() {
		log.info(MiscUtils.invocationInfo());
		log.info(String.format("last id: %d", seq.get()));
	}
}
