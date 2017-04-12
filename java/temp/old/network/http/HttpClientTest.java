package cf.study.network.http;

//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class HttpClientTest {

	// final static String IPS_URL =
	// "http://10.20.1.50:8080/media/rest/image/process";
	//
	// public static void main(String[] args) throws Exception {
	//
	// List<String> jobStrs = FileUtils.readLines(new
	// File("test.data/wrong_jobs.txt"));
	// String[] jobStrA = new String[jobStrs.size()];
	//
	// jobStrA = jobStrs.toArray(jobStrA);
	//
	//
	// for (int i = 0, j = jobStrA.length; i < j; i ++) {
	// final String bodyStr = jobStrA[i].replace("\\", "");
	// PostMethod post = null;
	// try {
	// final HttpClient hc = new HttpClient();
	// hc.getHttpConnectionManager().getParams().setConnectionTimeout(300000);
	// hc.getHttpConnectionManager().getParams().setSoTimeout(300000);
	//
	// post = new PostMethod(IPS_URL);
	// post.setRequestHeader("Connection", "close");
	// post.setRequestEntity(new ByteArrayRequestEntity(bodyStr.getBytes()));
	// if (hc.executeMethod(post) != 200) {
	// System.out.println(String.format("\n%s\n%s\n", bodyStr, new
	// String(post.getResponseBody())));
	// continue;
	// } else {
	// System.out.println(String.format("%d is finished!", i));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// post.releaseConnection();
	// }
	// }
	// }

	@Test
	public void testThreadSafeClientConnManager() throws Exception {
		
		final String urlStr = "http://www.poppen.de/wio2/wan/isonline"; //?SHOW_IMAGE=0&NICKNAME=vipxiaoyu";
		final HttpGet hg = new HttpGet(urlStr);
		
		final ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
		cm.setMaxTotal(128);
		cm.setDefaultMaxPerRoute(32);
		
		final DefaultHttpClient hc = new DefaultHttpClient(cm);
		final HttpResponse resp = hc.execute(hg);
		System.out.println(EntityUtils.toString(resp.getEntity()));
		
		hc.getConnectionManager().shutdown();
	}
}
