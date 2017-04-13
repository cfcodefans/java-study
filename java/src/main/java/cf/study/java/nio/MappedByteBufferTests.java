package cf.study.java.nio;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MappedByteBufferTests {

	static Path	testPath	= Paths.get(".\\test-output\\bigfile");
	
	static int SIZE_MB = 1024 * 1024;
	static long SIZE_100MB = 1024 * 1024 * 500;
	static byte[] DATA = new byte[SIZE_MB];

	@BeforeClass
	public static void setUpClass() throws IOException {
		if (Files.exists(testPath))
			 return;
		Files.createFile(testPath);
	}

	@AfterClass
	public static void tearDownClass() throws IOException {
//		Files.deleteIfExists(testPath);
	}

	StopWatch sw = null;
	
	@Before
	public void setUp() {
		sw = new StopWatch();
		sw.start();
	}
	
	@After
	public void tearDown() {
		sw.stop();
		System.out.println(String.format("%d ms", sw.getTime()));
	}
	
	@Test
	public void testMappedByteBufWrite() throws IOException {
		try (FileChannel fc = FileChannel.open(testPath, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			MappedByteBuffer mappedBuf = fc.map(MapMode.PRIVATE, 0, SIZE_100MB);
			for (int i = 0; i < 500; i++) {
				Arrays.fill(DATA, (byte)(i % 26 + 95));
				DATA[DATA.length - 1] = (byte)('\n');
				mappedBuf.put(DATA);
			}
			mappedBuf.force();
//			fc.force(true);
		}
		
		System.out.println(testPath.toFile().length());
	}
	
	@Test
	public void testIO() throws IOException {
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(testPath.toFile()))) {
			for (int i = 0; i < 500; i++) {
				Arrays.fill(DATA, (byte)(i % 26 + 95));
				DATA[DATA.length - 1] = (byte)('\n');
				os.write(DATA);
			}
			os.flush();
		}
	}
	
	@Test
	public void testMappedByteBufRead() throws IOException {
		try (FileChannel fc = FileChannel.open(testPath, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
			MappedByteBuffer mappedBuf = fc.map(MapMode.READ_WRITE, 0, SIZE_100MB);
			IntStream.range(0, 100).forEach(i -> {
				mappedBuf.get(DATA);
				System.out.println(DATA[3]);
			});
			
//			mappedBuf.force();
//			fc.force(true);
		}
		
		System.out.println(testPath.toFile().length());
	}
}
