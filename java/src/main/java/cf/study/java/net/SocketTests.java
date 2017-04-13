package cf.study.java.net;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import misc.MiscUtils;
import misc.Lambdas.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketTests {
	
	private static final Logger log = LoggerFactory.getLogger(SocketTests.class);

	private static final String SERVER_NAME = "localhost";
	private static final int SERVER_PORT = 12900;

	public static String readLine(final InputStream is) throws Exception {
		if (is == null || is.available() == 0) {
			return StringUtils.EMPTY;
		}

		final StringBuilder sb = new StringBuilder();
		for (int c = is.read(); c != '\n' && c != -1; c = (char) is.read()) {
			sb.append((char) c);
		}

		return sb.toString();
	}

	@SuppressWarnings("resource")
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

		serverThread.submit((Runnable) () -> {
			try {
				for (Socket s = ss.accept(); s != null; s = ss.accept()) {
					final Socket _s = s;
					log.info("inbound: \t" + _s);
					workerThreads.submit((Runnable) () -> {
						try {
							log.info("reading from: \t" + _s);
							while (!_s.isClosed()) {
								log.info(readLine(_s.getInputStream()));
								IOUtils.write(MiscUtils.now() + "\t" + ss + "\n", _s.getOutputStream());
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
			log.info("connected to server: \t" + addr);
			while (!s.isClosed()) {
				log.info("sending to server: \t" + addr);
				IOUtils.write(MiscUtils.now() + "\t" + s + "\n", s.getOutputStream());
				log.info(readLine(s.getInputStream()));
				break;
			}
			s.close();
			log.info("disconnected to server: \t" + addr);
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		
	}

	static final Charset charset = Charset.forName("ISO-8859-1");
	static final CharsetEncoder encoder = charset.newEncoder();
	static final CharsetDecoder decoder = charset.newDecoder();
	ExecutorService serverThread = null; 
	ExecutorService workerThreads = null; 
	ScheduledExecutorService scheduler = null;
	
	@Before
	public void setupTest() {
		scheduler = Executors.newScheduledThreadPool(1);
		serverThread = Executors.newSingleThreadExecutor(MiscUtils.namedThreadFactory("server"));
		workerThreads = Executors.newFixedThreadPool(MiscUtils.AVAILABLE_PROCESSORS, MiscUtils.namedThreadFactory("worker"));
	}
	
	@After
	public void tearDownTest() {
		MiscUtils.easySleep(1000);
		try {
			scheduler.awaitTermination(3, TimeUnit.SECONDS);
			workerThreads.awaitTermination(3, TimeUnit.SECONDS);
			serverThread.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	final InetSocketAddress addr = new InetSocketAddress(SERVER_NAME, SERVER_PORT);
	
	/**
	 * Why do we need to use selector model?
	 * In accept model, the server has to allocate a new thread for a new accepted client socket.
	 * and the new accepted client socket will attach/occupy the thread until it disconnects.
	 * 
	 * with select model, server can create a runnable with the ready channel, and throw 
	 * the runnable into a thread pool so that threads can be reused. 
	 * Because once runnable is executed, the thread pool can handle next read channel.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNioSelectSocket() throws Exception {
		final ServerSocketChannel sch = ServerSocketChannel.open();
		
		// Create an unbound server socket
		final ServerSocket ss = sch.socket();

		// Create a socket address object

		// Set the wait queue size to 100
		final int waitQueueSize = 100;

		// Bind the server socket to addr
		// with a wait queue size of 100
		ss.bind(addr, waitQueueSize);
		sch.configureBlocking(false);
		
		final Selector sel = Selector.open();
		
		final SelectionKey acceptSelKey = sch.register(sel, SelectionKey.OP_ACCEPT);
//		final SelectionKey readSelKey = sch.register(sel, SelectionKey.OP_READ);
		

		
		serverThread.submit((Runnable)()->{
			try {
				while (true) {
					/*
					 * Selects a set of keys whose corresponding channels are ready for I/O operations. 
This method performs a blocking selection operation. It returns only after at least one channel is selected, this selector's wakeup method is invoked, or the current thread is interrupted, whichever comes first. 
Returns:
The number of keys, possibly zero, whose ready-operation sets were updated*/
					final int selCnt = sel.select();
					log.info(String.format("%d selected", selCnt));
					
					final Set<SelectionKey> selKeys = sel.selectedKeys();
					
					for (Iterator<SelectionKey> it = selKeys.iterator(); it.hasNext();) {
						final SelectionKey sk = it.next();
						it.remove();
						
						if (sk == acceptSelKey && sk.isAcceptable()) {
							final SocketChannel clientCh = sch.accept();
							clientCh.configureBlocking(false);
							final SelectionKey clientKey = clientCh.register(sel, SelectionKey.OP_READ);
							final SocketAddress remoteAddr = clientCh.getRemoteAddress();
							clientKey.attach(remoteAddr);
							log.info("accepting..." + remoteAddr);
							continue;
						}
						
						final SocketChannel clientCh = (SocketChannel) sk.channel();
						if (!sk.isReadable()) {
							continue;
						}
						
						final SocketAddress remoteAddr = clientCh.getRemoteAddress();
						log.info("reading..." + remoteAddr);
						
						final ByteBuffer buf = ByteBuffer.allocate(512);
						final int readCnt = clientCh.read(buf);
						
						log.info("read:\t " + readCnt);
						
						if (readCnt == -1) {
							sk.cancel();
							clientCh.close();
							continue;
						}
						
						buf.flip();
						final String reqStr = decoder.decode(buf).toString();
						buf.clear();
						log.info(String.format("%s\t%s\tread:\t%s", MiscUtils.now(), clientCh.getRemoteAddress(), reqStr));
						
						if ("shutdown".equals(reqStr)) {
							sch.close();
							return;
						}
						
						if ("quit".equals(reqStr)) {
							sk.cancel();
							clientCh.close();
						} 
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		final Socket s = new Socket();
		s.connect(addr);
		try {
			log.info("connected to server: \t" + addr);
			while (!s.isClosed()) {
				log.info("sending to server: \t" + addr);
				IOUtils.write(MiscUtils.now() + "\t" + s + "\n", s.getOutputStream());
				log.info(readLine(s.getInputStream()));
				break;
			}
			s.close();
			log.info("disconnected to server: \t" + addr);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	public static final Runnable client(final InetSocketAddress addr, final ExConsumer<Socket> c) {
		return () -> {
			final Socket s = new Socket();
			try {
				s.connect(addr);
				c.accept(s);
				s.close();
				log.info("disconnected to server: \t" + addr);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		};
	}
	
	@SuppressWarnings("unused")
	@Test 
	public void testNioSelectSocketV2() throws Exception {
		final ServerSocketChannel ssch = ServerSocketChannel.open();
		ssch.socket().bind(addr);
		ssch.configureBlocking(false);
		final Selector sel = Selector.open();
		
		final SelectionKey acceptKey = ssch.register(sel, SelectionKey.OP_ACCEPT);
		
		final Function<SelectionKey, String> keyToStr = (sk) -> sk.attachment() + " is " + (sk.isAcceptable() ? "acceptable " : "") + (sk.isReadable() ? "readable " : "")  + (sk.isWritable() ? "writable " : "");
		final Map<SocketAddress, String> inboundAddrs = new ConcurrentHashMap<SocketAddress, String>();

		final Consumer<SocketChannel> onReq = (sc) -> {
			final SelectionKey sk = sc.keyFor(sel);
			if (!sk.isReadable()) return;
			
			final ByteBuffer buf = ByteBuffer.allocate(512);
			try {
				final SocketAddress remoteAddr = sc.getRemoteAddress();
				final int readCnt = sc.read(buf);
				if (readCnt == -1) {
					sk.cancel();
					sc.close();
					return;
				}
				
//				log.info("read:\t " + readCnt + " from " + remoteAddr);
				buf.flip();
				final String reqStr = decoder.decode(buf).toString();
				buf.clear();
				inboundAddrs.put(remoteAddr, reqStr);
				log.info(String.format("%s\t%s\tread:\t%s", MiscUtils.now(), remoteAddr, reqStr));
				
				if ("shutdown".equals(reqStr)) {
					ssch.close();
					return;
				}
				
				if ("quit".equals(reqStr)) {
					sk.cancel();
					sc.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		};
		
		ExecutorService _workerThreads = Executors.newFixedThreadPool(3, MiscUtils.namedThreadFactory("\n\t_server_worker"));
		final Runnable runServer = () -> {
			try {
				while (true) {
					final int selCnt = sel.select();
//					log.info(String.format("%d selected", selCnt));
					
					final Set<SelectionKey> selKeys = sel.selectedKeys();
					
//					log.info("selected keys: \t" + selKeys);
					
					for (Iterator<SelectionKey> it = selKeys.iterator(); it.hasNext();) {
						final SelectionKey sk = it.next();
						it.remove();
						
//						final SocketChannel sc = (SocketChannel) sk.channel();
//						log.info("get selected channel from:\t" + sc.getRemoteAddress());
//						log.info(keyToStr.apply(sk));
						if (sk == acceptKey && sk.isAcceptable()) {
							final SocketChannel csc = ssch.accept();
							csc.configureBlocking(false);
							final SocketAddress remoteAddr = csc.getRemoteAddress();
//							log.info("accept selected channel from:\t" + remoteAddr);
							csc.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE, remoteAddr);
							continue;
						}
						
						final SocketChannel sc = (SocketChannel) sk.channel();
						_workerThreads.submit((Runnable)()->onReq.accept(sc));
					}
				}
			} catch (Exception e) {
				log.error("error", e);
			}
		};
		serverThread.submit(runServer);
		
		IntStream.range(0, 125).forEach(i -> workerThreads.submit(client(addr, (s) -> {
			if (!s.isClosed()) {
				log.info(i + "\tsending from: \t" + s.getLocalSocketAddress() + "\t to server: \t" + addr);
				IOUtils.write(MiscUtils.now() + "\t" + s + "\n", s.getOutputStream());
//				log.info(readLine(s.getInputStream()));
//				log.info(i + "\tsent");
			}
		})));
		
//		_workerThreads.shutdown();
		if (!_workerThreads.isTerminated()) {
			_workerThreads.awaitTermination(1, TimeUnit.SECONDS);
		}
		
		inboundAddrs.entrySet().stream().map(e->e.toString()).forEach(log::info);
		log.info("inbound: \t" + inboundAddrs.size());
	}
	
	
	@Test
	public void testAioSocket() throws Exception {
		final AsynchronousServerSocketChannel asch = AsynchronousServerSocketChannel.open();
		
		asch.bind(addr);
		
		
	}
}
