package study.javax.cdi.weld.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.commons.MiscUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class SeqGenerator {
    private static Logger log = LoggerFactory.getLogger(SeqGenerator.class);
    private AtomicLong seq;

    @Produces
    public Long getSeq() {
        final long newId = seq.incrementAndGet();
        log.info("{} \t-> \t{newId: {}}", MiscUtils.invocationInfo(), newId);
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
        log.info("last id: {}", seq.get());
    }
}
