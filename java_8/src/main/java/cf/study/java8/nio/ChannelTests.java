package cf.study.java8.nio;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import sun.nio.ch.ChannelInputStream;

@SuppressWarnings("restriction")
public class ChannelTests {

	Path srcPath = Paths.get("./src/main/java/cf/study/java8/nio/ChannelTests.java");
	private static final Logger log = Logger.getLogger(ChannelTests.class);

	@Test
	public void testReadFromChannel() throws Exception {
		try (RandomAccessFile raf = new RandomAccessFile(srcPath.toFile(), "r");
				FileChannel inChannel = raf.getChannel();
				ChannelInputStream cis = new ChannelInputStream(inChannel)) {

			log.info(IOUtils.toString(cis));
		}
	}
}
