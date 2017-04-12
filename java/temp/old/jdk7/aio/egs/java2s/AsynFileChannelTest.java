package cf.study.jdk7.aio.egs.java2s;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

import org.junit.Test;

public class AsynFileChannelTest {

	@Test
	public void testAsynFileChannel() throws Exception {
		Path file = Paths.get("pom.xml");
		AsynchronousFileChannel afc = AsynchronousFileChannel.open(file, StandardOpenOption.READ);
				
		ByteBuffer bb = ByteBuffer.allocate(100000);
		Future<Integer> ref = afc.read(bb, 0);

		final long t = System.currentTimeMillis();
		int readCnt = ref.get();
		System.out.println(String.format("it took %d seconds to read %d byte", System.currentTimeMillis() - t, readCnt));
		
		System.out.println(new String(bb.array()));
		
		afc.close();		
	}
	
	@Test
	public void testAsynFileChannelLock() throws Exception {
		Path file = Paths.get("pom.xml");
		{
			AsynchronousFileChannel afc = AsynchronousFileChannel.open(file, StandardOpenOption.READ);
			ByteBuffer bb = ByteBuffer.allocate(100000);
			
			afc.lock();
		}
		
		{
			AsynchronousFileChannel afc = AsynchronousFileChannel.open(file, StandardOpenOption.READ);
			ByteBuffer bb = ByteBuffer.allocate(100000);
			
			afc.lock("String", new CompletionHandler<FileLock, Object>() {
				@Override
				public void completed(FileLock result, Object attachment) {
					
				}

				@Override
				public void failed(Throwable exc, Object attachment) {
					
				}
			});
		}
	}
}
