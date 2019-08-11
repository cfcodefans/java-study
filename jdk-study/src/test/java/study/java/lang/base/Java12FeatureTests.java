package study.java.lang.base;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;

public class Java12FeatureTests {

    static final Logger log = LoggerFactory.getLogger(Java12FeatureTests.class);

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"MONDAY", "SUNDAY"})
    public void switchExp(DayOfWeek d) {
        log.info("{} is {}",
            d,
            switch (d) {
                case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "weekday";
                case SATURDAY, SUNDAY -> "weekend";
                default -> throw new IllegalArgumentException("what is " + d);
            });
    }



}
