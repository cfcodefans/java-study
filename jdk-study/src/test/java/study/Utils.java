package study;

public class Utils {
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
}
