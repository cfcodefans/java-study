package cf.study.java.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import org.apache.commons.io.IOUtils;

public class NIOSocketTests {

	
	// @Test
	
	static class Worker implements Runnable {
		public final Queue<String> inputs = new ConcurrentLinkedQueue<>();
		public final Queue<String> outputs = new ConcurrentLinkedQueue<>();
		@Override
		public void run() {
			
		}
	}
	

	static class NIOSocketServer {
		
		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
		
		void onAccepted(ServerSocketChannel sc, SelectionKey sk) {
			SocketChannel accepted;
			try {
				accepted = sc.accept();
				accepted.configureBlocking(false).register(sk.selector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT).attach(new Worker());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		void onRead(ServerSocketChannel sc, SelectionKey sk) {
			try {
				SocketChannel cc = (SocketChannel) sk.channel();
				String req = new String(IOUtils.toByteArray(cc.socket().getInputStream()));
				
				Worker worker = (Worker) sk.attachment();
				worker.inputs.offer(req);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		void onClose(ServerSocketChannel sc, SelectionKey sk) {
			
		}
		
		void onWrite(ServerSocketChannel sc, SelectionKey sk) {
			
		}

		public BiConsumer<ServerSocketChannel, SelectionKey> onAcceptedHandler = this::onAccepted;
		public BiConsumer<ServerSocketChannel, SelectionKey> onReadHandler = this::onRead;
		public BiConsumer<ServerSocketChannel, SelectionKey> onWriteHandler = this::onWrite;
		public BiConsumer<ServerSocketChannel, SelectionKey> onCloseHandler = this::onClose;

		private AtomicBoolean toggle = new AtomicBoolean(true);
		
		public void testSocketByNIO() throws IOException {

			try (ServerSocketChannel sc = ServerSocketChannel.open().bind(new InetSocketAddress(4520))) {
				try (Selector sel = Selector.open()) {
					SelectionKey serverKey = sc.register(sel, SelectionKey.OP_ACCEPT);
					sc.configureBlocking(false);

					while (toggle.get() && !Thread.interrupted()) {
						if (sel.select() == 0)
							continue;

						Set<SelectionKey> selectedKeys = sel.selectedKeys();
						Set<SelectionKey> processKeys = new HashSet<>(selectedKeys);
						selectedKeys.clear();

						processKeys.stream().filter(sk -> sk.isValid()).forEach(sk -> onCloseHandler.accept(sc, sk));
						processKeys.stream().filter(sk -> sk.isWritable()).forEach(sk -> onWriteHandler.accept(sc, sk));
						processKeys.stream().filter(sk -> sk == serverKey && sk.isAcceptable()).forEach(sk -> onAcceptedHandler.accept(sc, sk));
						processKeys.stream().filter(sk -> sk.isReadable()).forEach(sk -> onReadHandler.accept(sc, sk));
					}
				}
			}
		}
	}
}
