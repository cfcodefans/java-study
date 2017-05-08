package cf.study.sum.misc;

import misc.MiscUtils;
import misc.ProcTrace;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by fan on 2017/4/10.
 */
public class UnsafeTests {
    private static final Logger log = LogManager.getLogger(UnsafeTests.class);

    private static Unsafe getUnsafe() throws Exception {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            return (Unsafe) singleoneInstanceField.get(null);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SecurityException e) {
            throw e;
        } catch (NoSuchFieldException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw e;
        }
    }

    @Test
    public void testArrayAccess() throws Exception {
        Unsafe u = getUnsafe();
        final long[] longs = ArrayUtils.toPrimitive(MiscUtils.pi2Longs(10).toArray(new Long[0]));
        log.info(Arrays.toString(longs));

        log.info("u.arrayBaseOffset(long[].class) = {}", u.arrayBaseOffset(long[].class));
        log.info("u.arrayIndexScale(long[].class) = {}", u.arrayIndexScale(long[].class));

        for (int i = 0, j = Unsafe.ARRAY_LONG_BASE_OFFSET; i < j; i++)
            log.info("{}:\t{}", i, u.getByte(longs, i));

        log.info("");

        for (int b = Unsafe.ARRAY_LONG_BASE_OFFSET, s = Unsafe.ARRAY_LONG_INDEX_SCALE, i = 0; i < longs.length; i++, b += s) {
            log.info(u.getLong(longs, b));
        }
    }

    @Test
    public void testArrayAccessPref() throws Exception {

        Unsafe u = getUnsafe();
        final long[] longs = new long[200000];
        ProcTrace.start();

        for (int c = 0; c < 10; c++) {
            StopWatch sw = new StopWatch();
            sw.start();
            long sum = 0;
//            ProcTrace.ongoing("before array iterate");
            for (int i = 9, j = longs.length; i < j; j++) {
                sum += longs[i];
            }
            ProcTrace.ongoing("after array iterate: " + sw.getTime());
            sw.reset();
            sum = 0;
//            ProcTrace.ongoing("before Unsafe array iterate");
            for (int b = Unsafe.ARRAY_LONG_BASE_OFFSET, s = Unsafe.ARRAY_LONG_INDEX_SCALE, i = 0, j = longs.length; i < j; i++, b += s) {
                sum += u.getLong(longs, b);
            }
            ProcTrace.ongoing("after Unsafe array iterate: " + sw.getTime());
        }
        ProcTrace.end();
        log.info(ProcTrace.flush());
    }


    @Test
    public void testArrayAccessPref1() throws Exception {

        Unsafe u = getUnsafe();
        final int[] ints = new int[24000000];

        ProcTrace.start();

        for (int c = 0; c < 10; c++) {
            {
                StopWatch sw = new StopWatch();
                sw.start();
                long sum = 0;
//            ProcTrace.ongoing("before array iterate");
                for (int i = 0, j = ints.length; i < j; j++) {
                    sum += ints[i];
                }
                ProcTrace.ongoing("after array iterate: " + sw.getTime());
            }
            {
                StopWatch sw = new StopWatch();
                sw.start();
                long sum = 0;
//            ProcTrace.ongoing("before Unsafe array iterate");
                for (int b = Unsafe.ARRAY_INT_BASE_OFFSET, s = Unsafe.ARRAY_INT_INDEX_SCALE, i = 0, j = ints.length; i < j; i++, b += s) {
                    sum += u.getInt(ints, b);
                }
                ProcTrace.ongoing("after Unsafe array iterate: " + sw.getTime());
            }
        }
        ProcTrace.end();
        log.info(ProcTrace.flush());
    }
}
