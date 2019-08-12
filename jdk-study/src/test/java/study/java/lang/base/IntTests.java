package study.java.lang.base;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static study.Utils.toBinStr;

public class IntTests {
    static final Logger log = LoggerFactory.getLogger(IntTests.class);

    int b;

    @Test
    public void testIntDef() {
        int a = 0;
        assertEquals(a, 0);
        a = 1;
        assertEquals(a, 1);
//        int b, c = 5; //compile error, b is not initialized
        assertEquals(b, 0);
//        int d=e=4; //e is not defined
        int d = 0, e = 0;
        d = e = 3;
        assertEquals(d, e);
    }

    @Test
    public void testValueRange() {
        assertEquals(32, Integer.SIZE);
        assertEquals(4, Integer.BYTES);

        log.info("Integer.MAX_VALUE ={}\t{}", Integer.MAX_VALUE, toBinStr(Integer.MAX_VALUE));
        log.info("Integer.MIN_VALUE = {}\t{}", Integer.MIN_VALUE, toBinStr(Integer.MIN_VALUE));

        {
            int i = Integer.MAX_VALUE + 1;
            log.info("Integer.MAX_VALUE + 1 = {}\t{}", i, toBinStr(i));
            i = Integer.MAX_VALUE + 2;
            log.info("Integer.MAX_VALUE + 2 = {}\t{}", i, toBinStr(i));
        }

        {
            int i = Integer.MIN_VALUE - 1;
            log.info("Integer.MIN_VALUE - 1 = {}\t{}", i, toBinStr(i));
            i = Integer.MIN_VALUE - 2;
            log.info("Integer.MIN_VALUE - 2 = {}\t{}", i, toBinStr(i));
        }

        {
            int i = Integer.MAX_VALUE + Integer.MAX_VALUE;
            log.info("Integer.MAX_VALUE + Integer.MAX_VALUE = {}\t{}", i, toBinStr(i));
            i = Integer.MAX_VALUE + Integer.MIN_VALUE;
            log.info("Integer.MAX_VALUE + Integer.MIN_VALUE = {}\t{}", i, toBinStr(i));
            i = Integer.MIN_VALUE + Integer.MIN_VALUE;
            log.info("Integer.MIN_VALUE + Integer.MIN_VALUE = {}\t{}", i, toBinStr(i));
        }

        {
            int i = Integer.MAX_VALUE - Integer.MAX_VALUE;
            log.info("Integer.MAX_VALUE - Integer.MAX_VALUE = {}\t{}", i, toBinStr(i));
            i = Integer.MAX_VALUE - Integer.MIN_VALUE;
            log.info("Integer.MAX_VALUE - Integer.MIN_VALUE = {}\t{}", i, toBinStr(i));
            i = Integer.MIN_VALUE - Integer.MIN_VALUE;
            log.info("Integer.MIN_VALUE - Integer.MIN_VALUE = {}\t{}", i, toBinStr(i));
        }
    }

    @Test
    public void testCasting() {
        int i = Integer.MAX_VALUE;
        {
            short s = (short) i;
            log.info("int {}|{} to short {}|{}", i, toBinStr(i), s, toBinStr(s));
            int j = s;
            log.info("short {}|{} to int {}|{}", s, toBinStr(s), j, toBinStr(j));
        }
        {
            byte b = (byte) i;
            log.info("int {}|{} to byte {}|{}", i, toBinStr(i), b, toBinStr(b));
            int j = b;
            log.info("byte {}|{} to int {}|{}", b, toBinStr(b), j, toBinStr(j));
        }
    }

    @Test
    public void testComplement() {
        log.info("{}\t{}", 4, toBinStr(4));
        log.info("{}\t{}", ~4, toBinStr(~4));
        log.info("{}\t{}", -4, toBinStr(-4));
    }
}
