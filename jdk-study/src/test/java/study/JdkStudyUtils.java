package study;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class JdkStudyUtils {
    static final Logger log = LoggerFactory.getLogger(JdkStudyUtils.class);

    public static String toBinStr(final byte _b) {
        final StringBuilder sb = new StringBuilder(Byte.SIZE);
        for (byte i = Byte.SIZE - 1; i >= 0; i--) {
            sb.append((_b & (1 << i)) != 0 ? '1' : '0');
        }
        return sb.toString();
    }

    public static String toBinStr(final int _i) {
        final StringBuilder sb = new StringBuilder(Integer.SIZE);
        sb.append(_i < 0 ? '1' : '0');
        for (byte i = Integer.SIZE - 2; i >= 0; i--) {
            sb.append((_i & (1 << i)) != 0 ? '1' : '0');
        }
        return sb.toString();
    }

    public static String toBinStr(final short _s) {
        final StringBuilder sb = new StringBuilder(Short.SIZE);
        for (byte i = Short.SIZE - 1; i >= 0; i--) {
            sb.append((_s & (1 << i)) != 0 ? '1' : '0');
        }
        return sb.toString();
    }

    public static String toBinStr(long _l) {
        final StringBuilder sb = new StringBuilder(Long.SIZE);
        for (byte i = Long.SIZE; i >= 0; i--) {
            sb.append((_l & (1 << i)) != 0 ? '1' : '0');
        }
        return sb.toString();
    }

    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            log.error("failed to get Unsafe", e);
            return null;
        }
    }

    public static void easySleep(long i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
        }
    }
}
