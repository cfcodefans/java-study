package cf.study.java8.utils;

import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

public class HashTests {
	private static final String SHA1_ALGORITHM = "SHA1";
	@Test
	public void testSha1() throws Exception {
		final MessageDigest md = MessageDigest.getInstance(SHA1_ALGORITHM);
		md.update("abc".getBytes());
		System.out.println(DatatypeConverter.printHexBinary(md.digest()));
	}
}
