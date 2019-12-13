package study.junit;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith({RandomParamExt.class, TimingExt.class})
public class ParallelTests {
    static final Logger log = LoggerFactory.getLogger(ParallelTests.class);

    static {
        // not working
//        String enableKey = "junit.jupiter.execution.parallel.enabled";
//        log.info(System.getProperty(enableKey));
//        System.setProperty(enableKey, Boolean.TRUE.toString());
//        System.setProperty("junit.jupiter.execution.parallel.mode.default", "concurrent");
    }

    @Test
    @RepeatedTest(10)
    @Execution(ExecutionMode.CONCURRENT)
    public void testConcurrentMode(@RandomParamExt.RandParam long ms) throws InterruptedException {
        Thread.sleep(ms % 1000);
        log.info("finished");
    }
}
