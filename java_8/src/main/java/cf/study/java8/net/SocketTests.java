package cf.study.java8.net;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import misc.MiscUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class SocketTests {
	
	private static final String SERVER_NAME = "localhost";
	private static final int SERVER_PORT = 12900;

	public static String readLine(final InputStream is) throws Exception {
		if (is == null || is.available() == 0) {
			return StringUtils.EMPTY;
		}

		final StringBuilder sb = new StringBuilder();
		for (int c = is.read(); c != '\n' && c != -1; c = (char)is.read()) {
			sb.append((char)c);
		}
		
		return sb.toString();
	}
	
	@Test
	public void testServerSocket() throws Exception {
		// Create an unbound server socket
		final ServerSocket ss = new ServerSocket();
		
		// Create a socket address object
		final InetSocketAddress addr = new InetSocketAddress(SERVER_NAME, SERVER_PORT); 
		
		// Set the wait queue size to 100
		final int waitQueueSize = 100;
		
		// Bind the server socket to addr
		// with a wait queue size of 100
		ss.bind(addr, waitQueueSize);
		
		ExecutorService es = Executors.newSingleThreadExecutor();
		
		ExecutorService _es = Executors.newCachedThreadPool();
		
		es.submit((Runnable)()->{
			try {
				for (Socket s = ss.accept(); s != null; s = ss.accept()) {
					final Socket _s = s;
					System.out.println("inbound: \t" + _s);
					_es.submit((Runnable)()->{
						try {
							System.out.println("reading from: \t" + _s);
							while (!_s.isClosed()) {
								System.out.println(readLine(_s.getInputStream()));
								IOUtils.write(MiscUtils.now()  + "\t" + ss + "\n", 
										_s.getOutputStream());
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								_s.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		final Socket s = new Socket();
		s.connect(addr);
		try {
			System.out.println("connected to server: \t" + addr);
			while (!s.isClosed()) {
				System.out.println("sending to server: \t" + addr);
				IOUtils.write(MiscUtils.now() + "\t" + s + "\n", s.getOutputStream());
				System.out.println(readLine(s.getInputStream()));
				break;
			}
			s.close();
			System.out.println("disconnected to server: \t" + addr);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		MiscUtils.easySleep(1000);
		_es.awaitTermination(3, TimeUnit.SECONDS);
		es.awaitTermination(3, TimeUnit.SECONDS);
	}
}
