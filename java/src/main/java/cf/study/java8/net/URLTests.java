package cf.study.java8.net;

import java.net.URL;

import org.junit.Test;

public class URLTests {

	@Test
	public void testURL() throws Exception {
		URL url = new URL("http://localhost:8080/what?params=a");
		System.out.println(url);
		System.out.println(url.toExternalForm());
		System.out.println(url.toURI());
		System.out.println(url.getHost());
		System.out.println(url.getPort());
		System.out.println(url.getProtocol());
		System.out.println(url.getPath());
		System.out.println(url.getQuery());
	}
}
