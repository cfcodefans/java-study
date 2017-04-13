package cf.study.java.net.socket.wire.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Framer {
	public static byte[] nextToken(InputStream in, byte[] delimiter) throws IOException {
		int nextByte;
		
		//If the stream has already ended, return null
		if ((nextByte = in.read()) == -1) {
			return null;
		}
		
		ByteArrayOutputStream tokenBuffer = new ByteArrayOutputStream();
		do {
			tokenBuffer.write(nextByte);
			byte[] currentToken = tokenBuffer.toByteArray();
			
			if (endsWith(currentToken, delimiter)) {
				int tokenLength = currentToken.length - delimiter.length;
				byte[] token = new byte[tokenLength];
				System.arraycopy(currentToken, 0, token, 0, tokenLength);
				return token;
			}
		} while ((nextByte = in.read()) != -1);
		return tokenBuffer.toByteArray();
	}
	
	private static boolean endsWith(byte[] value, byte[] suffix) {
		if (value.length < suffix.length) {
			return false;
		}
		
		for (int offset = 1; offset <= suffix.length; offset++) {
			if (value[value.length - offset] != suffix[suffix.length - offset]) {
				return false;
			}
		}
		return true;
	}
}
