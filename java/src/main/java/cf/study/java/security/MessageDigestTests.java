package cf.study.java.security;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by fan on 2017/5/8.
 */
public class MessageDigestTests {
    private final static Logger log = LogManager.getLogger(MessageDigestTests.class);

    private static char[] HEX_CODE = "01234567890abcdef".toCharArray();

    public static String toHexString(byte[] data) {
        if (ArrayUtils.isEmpty(data)) return null;
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(HEX_CODE[(b >> 4) & 0xF]);
            sb.append(HEX_CODE[(b & 0xF)]);
        }
        return sb.toString();
    }

    public static byte[] charsToBytes(char[] chars) {
        byte[] bytes = new byte[chars.length];
        for (int i = 0, j = chars.length; i < j; i++, bytes[i] = (byte) chars[i]) ;
        return bytes;
    }

    private static MessageDigest MD5 = null;

    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("failed to create md5", e);
        }
    }

    @Test
    public void testMD5() {
        String src = "testMD5";
        byte[] srcBytes = src.getBytes();

        MD5.update(srcBytes);
        String encoded = toHexString(srcBytes);
        log.info("before encoding:\t{}", src);
        log.info("after encoding:\t{}", encoded);
    }

    private static MessageDigest SHA = null;

    static {
        try {
            SHA = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            log.error("failed to create SHA-1", e);
        }
    }

    public static String encodeWithSHA(String src, String salt) {
        byte[] srcBytes = src.getBytes();
        byte[] saltBytes = salt.getBytes();
        byte[] srcAndSalt = new byte[src.length() + salt.length()];

        System.arraycopy(srcBytes, 0, srcAndSalt, 0, srcBytes.length);
        System.arraycopy(saltBytes, 0, srcAndSalt, srcBytes.length, saltBytes.length);
        byte[] digestBytes = SHA.digest(srcAndSalt);

        return Base64.getEncoder().encodeToString(digestBytes);
    }
}
