package cf.study.network.http.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import cf.study.utils.Trace;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HttpServerTest {

	private static final int TIME_OUT = 40;

	private static byte[] imgBuf = null;
	
	private static final String BASE_PATH = "test/img/";

	private static HttpHandler pathHandler = new HttpHandler() {
		@Override
		public void handle(HttpExchange he) throws IOException {
			String pathStr = he.getRequestURI().getQuery();
			
			System.out.println(pathStr);
			String[] strs = StringUtils.split(pathStr, "=");
			if (ArrayUtils.isEmpty(strs)) {
				return;
			}
			
			pathStr = strs[1];
			byte[] buf = FileUtils.readFileToByteArray(new File(pathStr)); 
			
			he.getResponseHeaders().set("Content-Type", "image/jpeg");
			he.sendResponseHeaders(200, buf.length);
			OutputStream respOS = he.getResponseBody();
			respOS.write(buf);
			respOS.close();
		}
	};
	
	private static HttpHandler normal = new HttpHandler() {
		@Override
		public void handle(HttpExchange he) throws IOException {
//			System.out.println(Paths.get(he.getRequestURI()).getFileName()); 
			
			he.getResponseHeaders().set("Content-Type", "image/jpeg");
			he.sendResponseHeaders(200, imgBuf.length);
			OutputStream respOS = he.getResponseBody();
			respOS.write(imgBuf);
			respOS.close();
		}
	};

	private static HttpHandler timeout = new HttpHandler() {
		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println(Calendar.getInstance().getTime());
			try {
				TimeUnit.SECONDS.sleep(TIME_OUT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Calendar.getInstance().getTime());
			he.getResponseHeaders().set("Content-Type", "image/jpeg");
			he.sendResponseHeaders(200, imgBuf.length);
			OutputStream respOS = he.getResponseBody();
			respOS.write(imgBuf);
			respOS.close();
		}
	};

	private static HttpHandler ioTimeout = new HttpHandler() {
		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println(Calendar.getInstance().getTime());
			
			he.getResponseHeaders().set("Content-Type", "image/jpeg");
			he.sendResponseHeaders(200, imgBuf.length);
			OutputStream respOS = he.getResponseBody();
			
			System.out.println(Calendar.getInstance().getTime());
			respOS.write(ArrayUtils.subarray(imgBuf, 0, imgBuf.length / 2));
			try {
				TimeUnit.SECONDS.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Calendar.getInstance().getTime());
			respOS.write(ArrayUtils.subarray(imgBuf, imgBuf.length / 2, imgBuf.length));
			respOS.close();
		}
	};
	
	private static HttpHandler trickyIoTimeout = new HttpHandler() {
		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println(Calendar.getInstance().getTime());

			he.getResponseHeaders().set("Content-Type", "image/jpeg");
			he.sendResponseHeaders(200, imgBuf.length);
			OutputStream respOS = he.getResponseBody();

			System.out.println(Calendar.getInstance().getTime());
			for (int i = 0, size = imgBuf.length / 10; i < 10; i++) {
				respOS.write(ArrayUtils.subarray(imgBuf, size * i, size * i + size));
				try {
					TimeUnit.SECONDS.sleep(4);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(String.format("%d, from %d to %d in %d", i, size*i, size * i + size, imgBuf.length));
			}				
			System.out.println(Calendar.getInstance().getTime());
			respOS.close();
		}
	};
	
	

	public static void main(String[] args) throws Exception {
		URL url = new URL("http://10.20.1.40:8880/message-temp/3597967-9904c6d27375efcaa512cbb8a7e9dd1f.jpeg");
//		imgBuf = IOUtils.toByteArray(url.openStream());
		imgBuf = IOUtils.toByteArray(FileUtils.openInputStream(new File(BASE_PATH + "MB1.JPG")));
		if (imgBuf == null) {
			System.out.println("can't read the img");
			return;
		}

		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 10);
		server.createContext("/jpg/normal.jpg", normal);
		server.createContext("/jpg/timeout.jpg", timeout);
		server.createContext("/jpg/io_timeout.jpg", ioTimeout);
		server.createContext("/jpg/tricky_io_timeout.jpg", trickyIoTimeout);
		server.createContext("/path", pathHandler);
		server.setExecutor(null); // creates a default executor
		server.start();
	}
	
	@Test
	public void testConn() throws Exception {
		URL url = new URL("http://localhost:8000/jpg/tricky_io_timeout.jpg");
		InputStream is = null;
		try {
			System.out.println(Calendar.getInstance().getTime());
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(1000);
			conn.setReadTimeout(1000);
			is = conn.getInputStream();
			byte[] imgBuf = IOUtils.toByteArray(is);
			System.out.println(imgBuf.length);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				is.close();
		}
		System.out.println(Calendar.getInstance().getTime());
	}
	
	@Test
	public void testPref() throws Exception {
		for (int i = 0; i < 20; i++) {
			Trace.start("start:");
			byte[] buffer = new byte[1024 * 64];
			URL url = new URL("http://develop.poppen.lab/uploads/DSCF5125.JPG");
			final URLConnection httpConn = url.openConnection();
			final int timeout = (int) 1000;
			httpConn.setConnectTimeout(timeout);
			httpConn.setReadTimeout(timeout);
			httpConn.connect();
			InputStream is = httpConn.getInputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream(1024 * 512);

			int n = 0;
			while (-1 != (n = is.read(buffer))) {
				output.write(buffer, 0, n);
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			buffer = null;
			Trace.end();
			System.out.println(Trace.flush());
		}
	}
	
	@Test
	public void testHttpClientTimeout() throws Exception {
		String url = "http://localhost:8000/jpg/tricky_io_timeout.jpg";
		
		HttpParams params = new BasicHttpParams();
		{
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			String version = "0.0";
			HttpProtocolParams.setUserAgent(params, "test/" + version);
			// Only selectively enable this for PUT but not all entity enclosing methods
			HttpProtocolParams.setUseExpectContinue(params, false);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);

			HttpConnectionParams.setTcpNoDelay(params, true);
			HttpConnectionParams.setSocketBufferSize(params, 8192);
		}
		
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		
//		ClientConnectionManager cm = new ThreadSafeClientConnManager(registry);
		AbstractHttpClient client = new DefaultHttpClient(params);
		HttpContext context = new BasicHttpContext();
		
		HttpGet get = new HttpGet(url);
		System.out.println("before sending: " + Calendar.getInstance().getTime()); 
		HttpResponse resp = client.execute(get);
		InputStream content = resp.getEntity().getContent();
		
		System.out.println("before reading: " + Calendar.getInstance().getTime()); 
		byte[] imgBuf = IOUtils.toByteArray(content);
		System.out.println(imgBuf.length);
		System.out.println("finish reading: " + Calendar.getInstance().getTime()); 
	}
}
