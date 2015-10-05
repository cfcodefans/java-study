package cf.study.old.data.encryption;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class SHAEncryptionTest {

	public final static String PWD = "test123";
	
	@Test
	public void matchPwd() throws Exception {
		String encoded1 = encodePassword(PWD);
		String encoded2 = encodePassword(PWD);
		
		System.out.println(encoded1);
		System.out.println(encoded2);
		
		System.out.println(verifySHA(encoded1, encoded2));
		System.out.println(verifySHA(encoded1, PWD));
	}

	private static boolean verifySHA(String ldappw, String inputpw) throws NoSuchAlgorithmException {
	
		// MessageDigest provides message hash algorithm, such as MD5 and SHA, here SHA-1 is used 
		MessageDigest md = MessageDigest.getInstance("SHA-1");
	
		// get encoded string
		if (ldappw.startsWith("{SSHA}")) {
			ldappw = ldappw.substring(6);
		} else if (ldappw.startsWith("{SHA}")) {
			ldappw = ldappw.substring(5);
		}
	
		// decode BASE64
		byte[] ldappwbyte = Base64.decodeBase64(ldappw.getBytes());
		byte[] shacode;
		byte[] salt;
	
		// first 20 byte is SHA-1 encrypted section. after that, it is encrypted random code
		if (ldappwbyte.length <= 20) {
			shacode = ldappwbyte;
			salt = new byte[0];
		} else {
			shacode = new byte[20];
			salt = new byte[ldappwbyte.length - 20];
			System.arraycopy(ldappwbyte, 0, shacode, 0, 20);
			System.arraycopy(ldappwbyte, 20, salt, 0, salt.length);
		}
	
		// add user password into hash
		md.update(inputpw.getBytes());
		
		// add random code into hash computation
		md.update(salt);
	
		// ��SSHA�ѵ�ǰ�û�������м���
		// calculate current user password
		byte[] inputpwbyte = md.digest();
	
		return MessageDigest.isEqual(shacode, inputpwbyte);
	}

	public static String encryptPassword(String password) {
		String sha1 = "";
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(password.getBytes("UTF-8"));
			sha1 = new String( Base64.encodeBase64(crypt.digest()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	
		if (!(sha1.startsWith("{SSHA}"))) {
			sha1 = "{SSHA}" + sha1;
		}
	
		return sha1;
	}

	public static String encodePassword(String plaintext) throws Exception {
		byte[] plainBytes = plaintext.getBytes();
		byte[] saltBytes = new byte[8];
		byte[] plainPlusSalt = new byte[plainBytes.length + 8];
	
		System.arraycopy(plainBytes, 0, plainPlusSalt, 0, plainBytes.length);
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		byte[] digestBytes = null;
	
			try {
				// Generate the salt and put in the plain+salt array.
				(new Random()).nextBytes(saltBytes);
				System.arraycopy(saltBytes, 0, plainPlusSalt, plainBytes.length, 8);
	
				// Create the hash from the concatenated value.
				digestBytes = messageDigest.digest(plainPlusSalt);
			} catch (Exception e) {
				e.printStackTrace();
			}
	
		// Append the salt to the hashed value and base64-the whole thing.
		byte[] hashPlusSalt = new byte[digestBytes.length + 8];
	
		System.arraycopy(digestBytes, 0, hashPlusSalt, 0, digestBytes.length);
		System.arraycopy(saltBytes, 0, hashPlusSalt, digestBytes.length, 8);
	
		return new String( Base64.encodeBase64(hashPlusSalt));
	}
	
	
}
