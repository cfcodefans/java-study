package cf.study.network.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;


public class URLTest {
	
	private static final Logger _logger =Logger.getLogger(URLTest.class.getSimpleName());
	
	public static Map<String, String> parseRefUrl(final String refUrl) {
		final Map<String, String> result = new HashMap<String, String>();
		if (refUrl == null) {
			return result;
		}
		String url = null;
		// http://dev.poppen.lab/video/suche
		if (refUrl.startsWith("http")) {
			url = refUrl.substring(7); // dev.poppen.lab/video/suche
			url = cutOffLongUrl(url);
		}
		// if (url == null || url.length() == 0) {
		if (StringUtils.isEmpty(url)) {
			_logger.severe(" the referred url " + refUrl + "is not able to be recoginized ");
			return result;
		}
		final int idx = url.indexOf('/');
		final String domainName = url.substring(0, idx + 1);
		final String partialUrl = url.substring(idx + 1, url.length());
		result.put("domainName", domainName);
		result.put("partialUrl", partialUrl);

		return result;
	}

	public static String cutOffLongUrl(final String url) {
		return StringUtils.substring(url, 0, 250);
	}
	
	@Test
	public void test() throws Exception {
		System.out.println(parseRefUrl("http://dev.poppen.lab"));
		System.out.println(parseRefUrl("http://dev.poppen.lab/video/suche"));
		System.out.println(parseRefUrl("http://cn.bing.com/search?q=cmb&FORM=SEENCN"));
		System.out.println(parseRefUrl("https://cn.bing.com/search?q=cmb&FORM=SEENCN"));
		
		String urlStr = "http://dev.poppen.lab";  //video/suche";//"https://cn.bing.com/search?q=cmb&FORM=SEENCN";
		System.out.println(Arrays.asList(new String[] { URIUtil.getName(urlStr), URIUtil.getPathQuery(urlStr), URIUtil.getFromPath(urlStr) }));
	}
	
	@Test
	public void testConn() throws Exception {
		URL url = new URL("http://localhost:8000/jpg/timeout.jpg");
		try {
			System.out.println(Calendar.getInstance().getTime());
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);
			conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(Calendar.getInstance().getTime());
	}
	
	public static void main(String[] args) {
		 try {
			URL url = new URL("https://api.clickandbuy-s1.com/webservices/pay_1_1_0.wsdl");
			System.out.println(url.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
