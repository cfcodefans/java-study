package cf.study.java8.javax.crypto;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.junit.Test;

public class DESEncryptionTest {

	private static final String DES = "DES";

	public static byte[] encode(byte[] src, byte[] password) throws Exception {
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new DESKeySpec(password);
		
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey secureKey = keyFactory.generateSecret(desKey);
		
		Cipher cipher = Cipher.getInstance(DES);
		
		cipher.init(Cipher.ENCRYPT_MODE, secureKey, random);
		
		return cipher.doFinal(src);
	}
	
	public static byte[] decode(byte[] src, byte[] password) throws Exception {
		SecureRandom random = new SecureRandom();
		
		DESKeySpec desKey = new DESKeySpec(password);
		
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		
		SecretKey secuKey = keyFactory.generateSecret(desKey);
		
		Cipher cipher = Cipher.getInstance(DES);
		
		cipher.init(Cipher.DECRYPT_MODE, secuKey, random);
		
		return cipher.doFinal(src);
	}
	
	@Test
	public void test() throws Exception {
		final String src = "whatthen";
		final String password = "12345678";
		
		System.out.println(src);
		final byte[] encoded = encode(src.getBytes(), password.getBytes());
		System.out.println(new String(encoded));
		final byte[] decoded = decode(encoded, password.getBytes());
		System.out.println(new String(decoded));
		
		try {
			final byte[] decoded1 = decode(encoded, "01234567".getBytes());
			System.out.println(new String(decoded1));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			final byte[] decoded2 = decode(encoded, "12345678".getBytes());
			System.out.println(new String(decoded2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			final byte[] decoded2 = decode(encoded, "23456789".getBytes());
			System.out.println(new String(decoded2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
