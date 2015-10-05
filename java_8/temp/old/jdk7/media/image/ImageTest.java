package cf.study.jdk7.media.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cf.study.utils.Trace;

public class ImageTest {
	@Test
	public void testImageRead() throws Exception {
		List<BufferedImage> bis = new ArrayList<BufferedImage>();
		for (int i = 0; i < 200; i++) {
			Trace.start("start reading from file");
			byte[] data = FileUtils.readFileToByteArray(new File("MB1.JPG"));
			Trace.ongoing(String.format("%d byte is read", data.length));

			BufferedImage bi = ImageIO.read(new ByteArrayInputStream(data));
			bis.add(bi);
			Trace.ongoing(String.format("%d byte is read from buffer and make a image with: %d byte", data.length, bi.getData().getDataBuffer().getSize()));

			Trace.ongoing("done");
			Trace.end();
			System.out.println(Trace.flush());
		}
	}
}
