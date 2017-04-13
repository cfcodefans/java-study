package cf.study.java.javax.imageio;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageIOTests {

	static final Logger log = LoggerFactory.getLogger(ImageIOTests.class);

	//http://bugs.java.com/bugdatabase/view_bug.do?bug_id=7064516
	@Test
	public void testReadJpeg() throws Exception {
		final BufferedImage bufImg = ImageIO.read(ImageIOTests.class.getResourceAsStream("test.jpg"));
		
		log.info(bufImg.toString());
	}
}
