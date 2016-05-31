package cf.study.java8.net;

import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class InetAddrTests {

	private static final Logger log = LogManager.getLogger(InetAddress.class);

	@Test
	public void testHostname() throws Exception {
		log.info(InetAddress.getLocalHost().getHostName());
	}
}
