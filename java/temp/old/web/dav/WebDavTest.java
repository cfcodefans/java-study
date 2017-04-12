package cf.study.web.dav;

import java.io.IOException;
import java.net.ProxySelector;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.googlecode.sardine.impl.SardineException;
import com.googlecode.sardine.impl.SardineImpl;

public class WebDavTest {

	private static class IpsSardineImpl extends SardineImpl {
		public IpsSardineImpl() {
			super(null, null);
		}
	
		public IpsSardineImpl(String username, String password) {
			super(username, password, null);
		}
	
		public IpsSardineImpl(String username, String password, ProxySelector selector) {
			super(username, password, selector);
		}
	
		public IpsSardineImpl(AbstractHttpClient http) {
			super(http, null, null);
		}
	
		public IpsSardineImpl(AbstractHttpClient http, String username, String password) {
			super(http, username, password);
		}
		
		protected ClientConnectionManager createDefaultConnectionManager(SchemeRegistry schemeRegistry) {
			return new ThreadSafeClientConnManager(schemeRegistry, 1000, TimeUnit.MILLISECONDS);
		}
		
		protected HttpParams createDefaultHttpParams() {
			HttpParams hps = super.createDefaultHttpParams();
			HttpConnectionParams.setStaleCheckingEnabled(hps, true);
			HttpConnectionParams.setConnectionTimeout(hps, 3000);
			HttpConnectionParams.setSoTimeout(hps, 3000);
			return hps;
		}
	}

	private Sardine sardine;

	@Before
	public void init() throws SardineException {
		//sardine = SardineFactory.begin("fan.chen", "cF_811017");
		sardine = SardineFactory.begin("ips", "ips");
	}

	@Test
	public void testWebDavList() {
		try {
			System.out.println(sardine.getResources("http://localhost:8081/webdav/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testWebDavPut() {
		try {
			sardine.put("http://localhost:8081/webdav/" + UUID.randomUUID(), UUID.randomUUID().toString().getBytes());
			System.out.println(sardine.getResources("http://localhost:8081/webdav/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTimeout() {
		long t = System.currentTimeMillis();
		IpsSardineImpl is = new IpsSardineImpl();
		try {
			is.list("http://edgy.thenetcircle.lab:12222/");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println(System.currentTimeMillis() - t);
		}
	}
}
