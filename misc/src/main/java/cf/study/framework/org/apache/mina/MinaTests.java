package cf.study.framework.org.apache.mina;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import misc.MiscUtils;
//import misc.MiscUtils.BiConsumer;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.Test;

public class MinaTests {

	public static class HandlerHolder extends IoHandlerAdapter {
		
		public void sessionCreated(IoSession session) {
			session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
			if (onCreate != null) onCreate.accept(session);
		}
		
		public void sessionClosed(IoSession session) throws Exception {
			log.info(MiscUtils.invocationInfo());
			if (onClose != null)  onClose.accept(session);
		}
		
		public void sessionOpened(IoSession session) throws Exception {
			log.info(MiscUtils.invocationInfo());
			if (onOpen != null)  onOpen.accept(session);
		}
		
		public void sessionIdle(IoSession session, IdleStatus status) {
			log.info(String.format("*** IDLE #%d ***", session.getIdleCount(status)));
			if (onIdle != null)  onIdle.accept(session, status);
		}
		
		public void exceptionCaught(IoSession session, Throwable cause) {
			session.close(true);
			if (onErr != null) onErr.accept(session, cause);
		}
		
		public void messageReceived(IoSession session, Object message) throws Exception {
			log.info(MiscUtils.invocationInfo() + ":\t" + message);
			if (onMsg != null)  onMsg.accept(session, message);
		}
		
	    public void messageSent(IoSession session, Object message) throws Exception {
	    	if (onMsg != null)  onMsgSent.accept(session, message);
	    }
		
	    public BiConsumer<IoSession, Object> onMsg = (session, message) -> log.info(message);
	    public BiConsumer<IoSession, Object> onMsgSent = (session, message) -> log.info(message);
	    public BiConsumer<IoSession, Throwable> onErr = (session, e)->log.info(e);
	    public BiConsumer<IoSession, IdleStatus> onIdle = (session, status)->log.info(status);
	    public Consumer<IoSession> onOpen = log::info;
	    public Consumer<IoSession> onClose = log::info;
	    public Consumer<IoSession> onCreate = log::info;
	}
	
	public static class TcpClient extends HandlerHolder {
		public IoConnector connector;
		public IoSession session;
		public TcpClient() {
			connector = new NioSocketConnector();
			connector.setHandler(this);
			ConnectFuture connFuture = connector.connect(new InetSocketAddress("localhost", PORT));
			connFuture.awaitUninterruptibly();
			session = connFuture.getSession();
		}
	}

	static final Logger log = Logger.getLogger(MinaTests.class);
	
	private static final int PORT = 8080;
	
	public static final ExecutorService serverThead = Executors.newSingleThreadExecutor();
	public static final ExecutorService clientTheads = Executors.newCachedThreadPool();
	
	@Test
	public void testEchoServer() throws Exception {
		serverThead.submit((Runnable)()->{
			SocketAcceptor acceptor = new NioSocketAcceptor();
			acceptor.setReuseAddress(true);
			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
			
			//Bind
			HandlerHolder _handler = new HandlerHolder();
			_handler.onMsg = (session, message) -> session.write(((IoBuffer) message).duplicate());
			acceptor.setHandler(_handler);
			try {
				acceptor.bind(new InetSocketAddress(PORT));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			log.info("Server started");
			while(true) MiscUtils.easySleep(1000);
		});
		
		MiscUtils.easySleep(10);
		
		clientTheads.submit((Runnable) () -> {
			TcpClient tc = new TcpClient();
			
			IntStream.range(0, 3).forEach(i->{
				IoBuffer buf = IoBuffer.allocate(4);
				buf.putInt(i);
				buf.flip();
				tc.session.write(buf);
			});
			
			tc.connector.dispose(true);
		});
		
		MiscUtils.easySleep(100);
		
		serverThead.awaitTermination(1, TimeUnit.SECONDS);
		clientTheads.awaitTermination(1, TimeUnit.SECONDS);
	}
}
