package cf.study.framework.org.quartz;

import misc.MiscUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzTests {
    static Logger log = LogManager.getLogger(QuartzTests.class);

    public static class HelloJob implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            log.info("Hello");
            log.info(jobExecutionContext);
        }
    }

    @Test
    public void study1() throws Exception {
        SchedulerFactory schedFact = new StdSchedulerFactory();

        Scheduler sched = schedFact.getScheduler();

        sched.start();

        //define a job and tie it to our job class
        JobDetail jd = JobBuilder.newJob(HelloJob.class).withIdentity("myTrigger").build();

        //Trigger the job to run now, and then every 3 seconds
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger3sec")
            .startNow()
            .withSchedule(SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(3)
                .withRepeatCount(5))
            .build();

        sched.scheduleJob(jd, trigger);

        while (trigger.getNextFireTime() != null) {
            MiscUtils.easySleep(1000);
        }
        log.info("done");
    }
}
