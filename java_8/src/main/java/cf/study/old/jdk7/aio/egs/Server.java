package cf.study.jdk7.aio.egs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cucumber.annotation.en_pirate.Aye;

import cf.study.utils.Trace;


public class Server {

	private AsynchronousServerSocketChannel server;
	
	private ExecutorService executor = Executors.newFixedThreadPool(20);
	private static AsynchronousChannelGroup asyncChannelGroup;	
	
	public Server() throws IOException {
		asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);
		server = AsynchronousServerSocketChannel.open(asyncChannelGroup).bind(new InetSocketAddress("127.0.0.1", 8888));
	}

	public void start() throws Exception {
		Trace.start();
		
		Trace.ongoing("before server.accept();");
		Future<AsynchronousSocketChannel> future = server.accept();
		
		Trace.ongoing("before future.get();");
		AsynchronousSocketChannel socketChannel = future.get();
		
		Trace.ongoing("before socketChannel.read(readBuffer).get();");
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		socketChannel.read(readBuffer).get();
		
		Trace.ongoing("after socketChannel.read(readBuffer).get();");
		System.out.printf("Receiver: %s", new String(readBuffer.array()));
		Trace.end();
	}
	
	public void startWithCompletionHandler() throws Exception {
//		while (true) {
			server.accept(System.currentTimeMillis(), completionHandler1);
//		}
			synchronized (asyncChannelGroup) {
				asyncChannelGroup.wait();
				asyncChannelGroup.shutdownNow();
			}
			System.out.println("asyncChannelGroup.shutdownNow();");
	}
	
	public static void main(String[] args) throws Exception {
		Trace.start();
		
		Trace.ongoing("new Server().start();");
		new Server().startWithCompletionHandler();
		
		Trace.ongoing("after Server().start();");
		Trace.end();
		System.out.println(Trace.flush());
	}

	private static CompletionHandler<AsynchronousSocketChannel, Long> completionHandler1 = new CompletionHandler<AsynchronousSocketChannel, Long>() {
		@Override
		public void completed(AsynchronousSocketChannel channel, Long attachment) {
			ByteBuffer readBuffer = ByteBuffer.allocate(1024);
			try {
				channel.read(readBuffer).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			final String req = new String(readBuffer.array());
			System.out.println(String.format("%d: %s", attachment, req));
			if ("quit".equalsIgnoreCase(req)) {
				synchronized (asyncChannelGroup) {
					asyncChannelGroup.notifyAll();
				}
			}
		}

		@Override
		public void failed(Throwable exc, Long attachment) {
			System.out.println(attachment);
			exc.printStackTrace();
		}
	};
}
