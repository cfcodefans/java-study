package cf.study.jdk7.aio.egs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class AIOClientWithFuture {
	private final AsynchronousSocketChannel client;

	public AIOClientWithFuture() throws IOException {
		client = AsynchronousSocketChannel.open();
	}

	public void sendMsg() throws InterruptedException, ExecutionException, IOException {
		client.connect(new InetSocketAddress("127.0.0.1", 8888));
		client.write(ByteBuffer.wrap("quit".getBytes())).get();
		client.close();
	}

	public static void main(String[] args) throws Exception {
		AIOClientWithFuture client = new AIOClientWithFuture();
		client.sendMsg();
	}

}
