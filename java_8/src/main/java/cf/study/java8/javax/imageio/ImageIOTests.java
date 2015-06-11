package cf.study.java8.javax.imageio;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ImageIOTests {

	static final Logger log = Logger.getLogger(ImageIOTests.class);

	//http://bugs.java.com/bugdatabase/view_bug.do?bug_id=7064516
	@Test
	public void testReadJpeg() throws Exception {
		final BufferedImage bufImg = ImageIO.read(ImageIOTests.class.getResourceAsStream("test.jpg"));
		
		log.info(bufImg);
	}
}
