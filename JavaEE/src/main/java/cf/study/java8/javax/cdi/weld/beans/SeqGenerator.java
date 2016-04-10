package cf.study.java8.javax.cdi.weld.beans;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import misc.MiscUtils;

//@Singleton
@ApplicationScoped
public class SeqGenerator {
	private static Logger log = LoggerFactory.getLogger(SeqGenerator.class);
	
	private AtomicLong seq;
	
	public SeqGenerator() {}
	
	@Produces
	public Long getSeq() {
		final long newId = seq.incrementAndGet();
		log.info(String.format("%s \t-> \t{newId: %d}", MiscUtils.invocationInfo(), newId));
		return newId;
	}
	
	@Produces
	public Date getTimeStamp() {
		return Calendar.getInstance().getTime();
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
