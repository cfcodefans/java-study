package study.java.lang.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArithmeticTests {
    static final Logger log = LoggerFactory.getLogger(ArithmeticTests.class);

    @Test
    public void testAdd() {
        {
            log.info("add different types of numbers to integer");
            int _i = Integer.MAX_VALUE;
            log.info("1 + {} = {}", _i, 1 + _i);
            long _l = Long.MAX_VALUE;
            log.info("1 + {} = {}", _l, 1 + _l);
            short _s = Short.MAX_VALUE;
            log.info("1 + {} = {}", _s, 1 + _s);
            byte _b = Byte.MAX_VALUE;
            log.info("1 + {} = {}", _b, 1 + _b);
            double _f = Float.MAX_VALUE;
            log.info("1 + {} = {}", _f, 1 + _f);
            double _d = Double.MAX_VALUE;
            log.info("1 + {} = {}", _d, 1 + _d);
        }
    }

    @Test
    public void testMod() {
        {
            log.info("-3 % 2 = {}", -3 % 2);
            log.info("3 % -2 = {}", 3 % -2);

            int _i = Integer.MAX_VALUE;
            long _l = Long.MAX_VALUE;
            log.info("{} % {} = {}", _l, (_l - _i), _l % (_l - _i));
        }

        {
            log.info("5 % 4 = {}", 5 % 4);
            log.info("5 % 2 + 2 = {}", 5 % 2 + 2);
            log.info("5 % 2 * 2 = {}", 5 % 2 * 2);
            log.info("2 + 5 % 2 = {}", 2 + 5 % 2);
            log.info("2 * 5 % 2 = {}", 2 * 5 % 2);
        }

        {
            log.info("0.75 % 0.5 = {}", 0.75 % 0.5);
            log.info("0.75 % 1.5 = {}", 0.75 % 1.5);
            log.info("(2/3) % (5/7) = {}", (2 / 3.0) % (5 / 7.0));
            log.info("(5/3) % (5/7) = {} \t 5 / 21.0 = {}", (5 / 3.0) % (5 / 7.0), 5 / 21.0);
            log.info("(4/3) % (5/7) = {} \t 5 / 21.0 = {}", (4 / 3.0) % (5 / 7.0), 13 / 21.0);
        }

        try {
            log.info("3 % 0 = {}", 3 % 0);
            Assertions.fail("should be ArithmeticException");
        } catch (Exception e) {
            log.error("can not mod by 0, {}", e.getMessage());
        }
    }
}
