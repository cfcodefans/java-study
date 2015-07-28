package cf.study.network.socket;

import java.net.InetAddress;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class HostNameTest {
	@Test
	public void printHostname() throws Exception {
		String hostname = IOUtils.toString(Runtime.getRuntime().exec("hostname").getInputStream());
		System.out.println(hostname.trim());
		hostname = InetAddress.getLocalHost().getHostName();
		System.out.println(hostname);
		hostname = InetAddress.getLoopbackAddress().getHostAddress();
		System.out.println(hostname);
		hostname = InetAddress.getLocalHost().getHostAddress();
		System.out.println(hostname);
	}
	
	
}
