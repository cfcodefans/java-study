package cf.study.java.Math;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTests {
    @Test
    public void testRounding() {
        System.out.println("ROUND_HALF_DOWN to 2:\t" + new BigDecimal(123.4567).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        System.out.println("ROUND_HALF_UP to 2:\t" + new BigDecimal(123.4567).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        System.out.println("ROUND_DOWN to 2:\t" + new BigDecimal(123.4567).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        System.out.println("ROUND_UP to 2:\t" + new BigDecimal(123.4567).setScale(2, BigDecimal.ROUND_UP).doubleValue());
    }
}
