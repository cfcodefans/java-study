package cf.study.java.net;

	/*
        final AsynchronousServerSocketChannel listener =
        AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));

    listener.accept(null, new CompletionHandler<AsynchronousSocketChannel,Void>() {
        public void completed(AsynchronousSocketChannel ch, Void att) {
            // accept the next connection
            listener.accept(null, this);

            // handle this connection
            handle(ch);
        }
        public void failed(Throwable exc, Void att) {
            ...
        }
    });
	 */

import misc.Lambdas;
import misc.MiscUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AioChannelTests {

    private static final Logger log = LoggerFactory.getLogger(AioChannelTests.class);

    public AioChannelTests() throws IOException {
    }

    interface ISimpleCompletionHandler<V, A> extends CompletionHandler<V, A> {
        void _completed(V result, A attachment) throws Exception;

        default void completed(V result, A attachment) {
            try {
                _completed(result, attachment);
            } catch (Exception e) {
                failed(e, attachment);
            }
        }

        default void failed(Throwable ex, A att) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static <V, A> CompletionHandler<V, A> _wrapper(ISimpleCompletionHandler<V, A> handler) {
        return handler;
    }

    @Test
    public void testAioClient() throws Exception {
        try (AsynchronousSocketChannel client = AsynchronousSocketChannel.open()) {
            CompletableFuture.supplyAsync(
                Lambdas.ws(client.connect(new InetSocketAddress("127.0.0.1", 3456))::get)
            ).thenApplyAsync(Lambdas.wf((Void _void) -> {
                return client.write(ByteBuffer.wrap("shutdown".getBytes())).get();
            })).thenApply(Lambdas.wf((Integer writeCnt) -> {
                log.info(String.format("hello is written: %s", writeCnt));
                return client.write(ByteBuffer.wrap("shutdown".getBytes())).get();
            })).thenApply(Lambdas.wf((Integer writeCnt) -> {
                log.info(String.format("shutdown is written: %s", writeCnt));
                return client.write(ByteBuffer.wrap("shutdown".getBytes())).get();
            })).get();
        }
    }

    private void printHandler(AsynchronousSocketChannel ch, AsynchronousServerSocketChannel srv) {
        srv.accept(srv, AioChannelTests._wrapper(this::printHandler));
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        ch.read(readBuffer, null, _wrapper((Integer readCount, Void attr) -> {
            log.info(String.format("received %s bytes from %s", readCount, ch.getRemoteAddress()));
            byte[] bytes = new byte[readCount];
            readBuffer.flip();
            readBuffer.get(bytes);
            readBuffer.rewind();
            String req = new String(bytes);
            log.info("request:\t" + req);
            if ("stop".equalsIgnoreCase(req)) {
                closeChannel(ch);
            }
            if ("shutdown".equalsIgnoreCase(req)) {
                synchronized (asyncChannelGroup) {
                    asyncChannelGroup.notifyAll();
                }
            }
        }));
    }

    ExecutorService executor = Executors.newFixedThreadPool(10, MiscUtils.namedThreadFactory("aio-thread"));
    AsynchronousChannelGroup asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);

    @Test
    public void testAioServer() throws Exception {
        try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(3456));) {
            server.accept(server, AioChannelTests._wrapper(this::printHandler));
            log.info("server accepted");
            synchronized (asyncChannelGroup) {
                asyncChannelGroup.wait();
                asyncChannelGroup.shutdownNow();
            }
            log.info("server is going to close");
        }
    }

    private static void closeChannel(AsynchronousSocketChannel ch) {
        try {
            ch.close();
        } catch (IOException e) {
            try {
                log.error(String.format("can't close between %s -> %s", ch.getRemoteAddress(), ch.getLocalAddress()), e);
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            }
        }
    }
}
