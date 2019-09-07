package study.java.lang.base;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.JdkStudyUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static study.JdkStudyUtils.toBinStr;

public class IntTests {
    static final Logger log = LoggerFactory.getLogger(IntTests.class);
    static final Unsafe us = JdkStudyUtils.getUnsafe();

    int b;
    int intValue = 0xAA << 24 | 0xCC << 16 | 0xEE << 8 | 0xFF;

    @Test
    public void testUnsafeOper() throws Exception {

        Field f = IntTests.class.getDeclaredField("intValue");
        long offset = us.objectFieldOffset(f);
        byte b1 = us.getByte(this, offset);
        byte b2 = us.getByte(this, offset + 1);
        byte b3 = us.getByte(this, offset + 2);
        byte b4 = us.getByte(this, offset + 3);

        log.info("{}\t{}", intValue, toBinStr(intValue));
        log.info("{}\t{}\t{}\t{}", toBinStr(b1), toBinStr(b2), toBinStr(b3), toBinStr(b4));
        log.info(ByteOrder.nativeOrder().toString());
    }

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
