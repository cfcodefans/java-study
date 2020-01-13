package cf.study.javax.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Created by fan on 2017/5/8.
 */
public class DESTests {
    private final static Logger log = LogManager.getLogger(DESTests.class);

    private final static String UNICODE_FORMAT = "UTF8";

    private final static String DESEDE_ENCRYPTION_SCHEME = "DESede";

    private KeySpec ks;
    private SecretKeyFactory skf;
    private byte[] bytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    private SecretKey key;

    public DESTests() {
        System.out.print("Hello world".substring(1));
        int array[] = {1,2};
        myEncryptionKey = "012345678901234567890123456789";
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        try {
            bytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            ks = new DESedeKeySpec(bytes);
            skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            key = skf.generateSecret(ks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String src) {
        try {
            Cipher cipher = Cipher.getInstance(myEncryptionScheme);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] srcBytes = src.getBytes(UNICODE_FORMAT);
            return Base64.getEncoder().encodeToString(cipher.doFinal(srcBytes));
        } catch (Exception e) {
            log.error("failed to encrypt", e);
            return null;
        }
    }

    public String decrypt(String base64Encoded) {
        try {
            Cipher cipher = Cipher.getInstance(myEncryptionScheme);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(base64Encoded)));
        } catch (Exception e) {
            log.error("failed to decrypt", e);
            return null;
        }
    }

    @Test
    public void test() {
        log.info("src:\t{}", "whoami");
        final String encoded = encrypt("whoami");
        log.info("encoded:\t{}", encoded);
        log.info("decoded:\t{}", decrypt(encoded));
    }
}
