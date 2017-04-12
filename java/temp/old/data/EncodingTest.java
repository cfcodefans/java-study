package cf.study.data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
//import org.apache.commons.lang.StringUtils;


public class EncodingTest {
	
	final static byte[] BASE64 = new byte[64];
	final static byte[] REVERSE_BASE64 = new byte[256];
	
	@BeforeClass
	public static void initBase64() {
		int i = 0;
		for (char c = 'A'; c <= 'Z'; c++, i++) {
			BASE64[i] = (byte)c;
		}
		
		for (char c = 'a'; c <= 'z'; c++, i++) {
			BASE64[i] = (byte)c;
		}
		
		for (char c = '0'; c <= '9'; c++, i++) {
			BASE64[i] = (byte)c;
		}
		
		BASE64[i++] = (byte)'+';
		BASE64[i++] = (byte)'/';	
		
		for (byte j = 0; j < BASE64.length; j++) {
			REVERSE_BASE64[BASE64[j]] = j;
		}
	}
	
	public byte[] base64ToBytes(byte[] bytes) {
		byte[] re = null;
		if (bytes[bytes.length - 1] == '=') {
			if ((bytes[bytes.length - 2] == '=')) {
				re = new byte[bytes.length / 4 * 3 - 2];
			} else {
				re = new byte[bytes.length / 4 * 3 - 1];
			}
		} else {
			re = new byte[bytes.length / 4 * 3];
		}
		
		int i = 0;
		int i1 = 0;
		for (int j = bytes.length - 4; i < j; i+=4, i1 += 3) {
			byte v1 = REVERSE_BASE64[bytes[i]];
			byte v2 = REVERSE_BASE64[bytes[i + 1]];
			byte v3 = REVERSE_BASE64[bytes[i + 2]];
			byte v4 = REVERSE_BASE64[bytes[i + 3]];
			
			re[i1] = (byte)((v1 << 2) | ((v2 >> 4) & 0x3));
			re[i1 + 1] = (byte)(((v2 & 0xF) << 4) | ((v3 >> 2) & 0xF));
			re[i1 + 2] = (byte)(((v3 & 0x3) << 6) | v4);
		}
		
		{
			byte v1 = REVERSE_BASE64[bytes[i]];
			byte v2 = REVERSE_BASE64[bytes[i + 1]];
			byte v3 = REVERSE_BASE64[bytes[i + 2]];
			byte v4 = REVERSE_BASE64[bytes[i + 3]];
			
			re[i1] = (byte)((v1 << 2) | ((v2 >> 4) & 0x3));
			if (bytes[i + 2] == '=') {
				return re;
			}
			re[i1 + 1] = (byte)(((v2 & 0xF) << 4) | ((v3 >> 2) & 0xF));
			if (bytes[i + 3] == '=') {
				return re;
			}
			re[i1 + 2] = (byte)(((v3 & 0x3) << 6) | v4);
		}
		
		return re;
	}
	
	public byte[] bytesToBase64(byte[] bytes) {
		byte[] re = new byte[bytes.length / 3 * 4 + (bytes.length % 3 != 0 ? 4 : 0)];
		
		int i = 0;
		int i1 = 0;
		for (int j = bytes.length - bytes.length % 3; i < j; i+=3, i1+=4) {
			re[i1] = BASE64[(byte)(bytes[i] >> 2)];
			re[i1 + 1] = BASE64[(byte)(((bytes[i] & 3) << 4) | (bytes[i + 1] >> 4))];
			re[i1 + 2] = BASE64[(byte)(((bytes[i + 1] & 0xF) << 2) | (bytes[i + 2] >> 6))];
			re[i1 + 3] = BASE64[(byte)(bytes[i + 2] & 0x3F)];
		}

		if (bytes.length - i == 1) {
			re[i+1] = BASE64[(byte)(bytes[i] >> 2)];
			re[i + 2] = BASE64[(byte)((bytes[i] & 3) << 4)];
			re[i + 3] = (byte)'=';
			re[i + 4] = (byte)'=';
			
		} else if (bytes.length - i == 2) {
			re[i + 1] = BASE64[(byte)(bytes[i] >> 2)];
			re[i + 2] = BASE64[(byte)(((bytes[i] & 3) << 4) | (bytes[i + 1] >> 4))];
			re[i + 3] = BASE64[(byte)((bytes[i + 1] & 0xF) << 2)];
			re[i + 4] = (byte)'=';
		}
		
		return re;
	}
	
	@Test
	public void testMyBase64() {
		{
		String str = "ABCDEF";
		String str64 = new String(Base64.encodeBase64(str.getBytes()));
		System.out.println(String.format("%s -> Base 64 -> %s", str, str64));
		
		String myStr64 = new String(bytesToBase64(str.getBytes()));
		System.out.println(String.format("'%s' -> Base 64 -> '%s'", str, myStr64));
		str = new String( base64ToBytes(myStr64.getBytes()));
		System.out.println(String.format("'%s' <- Base 64 <- '%s'", str, myStr64));
		}
		
		{
		String str = "ABCDE";
		String str64 = new String(Base64.encodeBase64(str.getBytes()));
		System.out.println(String.format("%s -> Base 64 -> %s", str, str64));
		
		String myStr64 = new String(bytesToBase64(str.getBytes()));
		System.out.println(String.format("'%s' -> Base 64 -> '%s'", str, myStr64));
		str = new String( base64ToBytes(myStr64.getBytes()));
		System.out.println(String.format("'%s' <- Base 64 <- '%s'", str, myStr64));
		}
		
		{
		String str = "ABCD";
		String str64 = new String(Base64.encodeBase64(str.getBytes()));
		System.out.println(String.format("%s -> Base 64 -> %s", str, str64));
		
		String myStr64 = new String(bytesToBase64(str.getBytes()));
		System.out.println(String.format("'%s' -> Base 64 -> '%s'", str, myStr64));
		str = new String( base64ToBytes(myStr64.getBytes()));
		System.out.println(String.format("'%s' <- Base 64 <- '%s'", str, myStr64));
		}
		
		{
		String str = "ABC";
		String str64 = new String(Base64.encodeBase64(str.getBytes()));
		System.out.println(String.format("%s -> Base 64 -> %s", str, str64));
		
		String myStr64 = new String(bytesToBase64(str.getBytes()));
		System.out.println(String.format("'%s' -> Base 64 -> '%s'", str, myStr64));
		str = new String( base64ToBytes(myStr64.getBytes()));
		System.out.println(String.format("'%s' <- Base 64 <- '%s'", str, myStr64));
		}
	}
	
	@Test
	public void testBase64() {
		String str = "ABC";
		String str64 = new String(Base64.encodeBase64(str.getBytes()));
		System.out.println(String.format("%s -> Base 64 -> %s", str, str64));
		System.out.println(String.format("%s <- Base 64 <- %s", str64, new String(Base64.decodeBase64(str64.getBytes()))));
		
		Long lv = 1234567890l;
		BitSet longBits = BitSet.valueOf(new long[] {lv});
		String longStr64 = new String(Base64.encodeBase64(longBits.toByteArray()));
		System.out.println(String.format("%s -> Base 64 - >%s", Long.toBinaryString(lv), longStr64));
	}
	
	@Test
	public void testWebEncoding() throws Exception {
		String desert = "ɳĮ";
		System.out.println(ArrayUtils.toString(desert.getBytes()));
		System.out.println(ArrayUtils.toString(StringUtils.getBytesIso8859_1(desert)));
		System.out.println(StringUtils.newStringUsAscii(StringUtils.getBytesIso8859_1(desert)));
		System.out.println(StringUtils.newStringUtf8((StringUtils.getBytesIso8859_1(desert))));
		System.out.println(StringUtils.newStringUtf8((StringUtils.getBytesUtf8(desert))));
		System.out.println(StringUtils.newStringUtf8((desert.getBytes("GB2312"))));
		
		System.out.println(URLEncoder.encode(desert, "UTF-8"));
		
		String string = "%99%20";
		System.out.println(URLDecoder.decode(string, "UTF-8"));
		System.out.println(URLDecoder.decode(string, "GB2312"));
		
	}
	
	private String processURL(String _imgPath) throws UnsupportedEncodingException {
		if (!(_imgPath.contains("%"))) {
			return _imgPath;
		}
		String[] chars = (_imgPath.split("%"));
		ByteBuffer sb = ByteBuffer.allocate(chars.length * 2);
		for (String c : chars) {
			sb.putShort(Short.decode(c));
		}
		
		return new String(sb.array(), "UTF-8");
	}
	
	@Test
	public void testWebEncoding1() {
		byte[] bs = {-66, -43, -69, -88, 46, 106, 112, 103};
		String s1 = new String(bs);
		System.out.println(s1);
		
		StringBuilder sb = new StringBuilder();
		sb.append("abc/").append(s1);
		
		System.out.println(sb.toString());
	}
	
	@Test
	public void testStringFormat() {
		System.out.println(String.format("%s", "abc"));
	}
}
