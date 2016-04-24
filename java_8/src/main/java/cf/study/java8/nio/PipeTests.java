package cf.study.java8.nio;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

public class PipeTests {

	private static final Logger log = Logger.getLogger(PipeTests.class);

	@Test
	public void testPipe() throws Exception {
		Pipe p = Pipe.open();

		Runnable consumer = () -> {
			try (SourceChannel sc = p.source(); Selector readSel = Selector.open()) {
				sc.configureBlocking(false);
				sc.register(readSel, SelectionKey.OP_READ);
				// sc.configureBlocking(true);
				while (!Thread.interrupted()) {
					if (readSel.select() == 0) {
						log.info("still waiting...");
						// Thread.sleep(100);
						continue;
					}

					Set<SelectionKey> _sks = readSel.selectedKeys();
					Set<SelectionKey> skSet = new HashSet(_sks);
					_sks.clear();
					SelectionKey readKey = skSet.stream().filter(SelectionKey::isReadable).findAny().get();
					StringBuilder sb = new StringBuilder();
					if (readKey != null) {
						log.info("selected:\t" + readKey);
						ReadableByteChannel rbc = (ReadableByteChannel) readKey.channel();
						for (ByteBuffer buf = ByteBuffer.allocate(128); rbc.read(buf) > 0;) {
							buf.flip();
							sb.append(new String(buf.array()));
						}
						log.info("received:\t" + sb.toString());
					}
					;
				}
				log.info("interrupted:\t" + Thread.interrupted());
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		Thread consumerThread = new Thread(consumer);
		consumerThread.start();

		try (SinkChannel sc = p.sink()) {
			log.info("going to send");
			Thread.sleep(1300);
			sc.write(ByteBuffer.wrap("New String to write to file...".getBytes()));
			Thread.sleep(1300);
			sc.write(ByteBuffer.wrap("Another String to write to file...".getBytes()));
			Thread.sleep(1300);
			consumerThread.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
