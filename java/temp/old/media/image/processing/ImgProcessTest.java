package cf.study.media.image.processing;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class ImgProcessTest {
	
	PImage pImg = null;
	PApplet applet = new PApplet();
	private BufferedImage read;
	
	@Before
	public void setup() {
		try {
			read = ImageIO.read(new File("Tulips.jpg"));
			pImg = PImageBufferedImageConverter.toPImage(read, "jpg");
//			pImg = applet.loadImage("sample.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ImgProcessTest t = new ImgProcessTest();
		t.setup();
	}
	
	@Test
	public void testRotationWithGraphy2D() throws Exception {
		 Graphics2D g = (Graphics2D) read.getGraphics();
		 
		 g.rotate((float) (Math.PI) / 6);
		 g.drawImage(read, 0, 0, read.getWidth(), read.getHeight(), null);
		 g.drawString("Test Line, why doesn't it rotate?", 100, 100);
		 Font font = Font.decode("TimesRoman");
		 g.setFont(font);
		 g.setColor(Color.black);
		 read.flush();
		 ImageIO.write(read, "png", new File("sample_rotate_30.png"));
	}
	
	@Test
	public void testRotation() throws Exception {
		PGraphics r = applet.createGraphics(pImg.width, pImg.height, PApplet.JAVA2D);
		
		r.beginDraw();
		
//		r.pushMatrix();
		
		r.translate(pImg.width / 2, pImg.height / 2);
		r.rotate((float) (Math.PI) *(2f/3f));
		r.translate(-pImg.width / 2, -pImg.height / 2);
		r.image(pImg, 0, 0);
//		renderer.background(pImg);
//		renderer.translate(pImg.width / 2, pImg.height / 2);
		
		
		
//		renderer.updatePixels();
//		renderer.textFont(new PFont(Font.decode("TimesRoman"), false), 30);
		r.textSize(30);
		r.text("write some test text on it", 100, 100);
		
//		r.popMatrix();
		r.endDraw();
		
//		r.flush();
//		renderer.get().save("sample_rotate_30.png");
		
		
		BufferedImage result = PImageBufferedImageConverter.toBufferedImage(r, "png");
		ImageIO.write(result, "png", new File("sample_rotate_-30.png"));
	}
	
	@Test
	public void testRotationAndZoomIn() throws Exception {
		float angle = (float) (Math.PI / 3f * 2f) ;
		
		int newWidth = (int) (pImg.width * abs(cos(angle)) + pImg.height * abs(sin(angle)));
		int newHeight = (int) (pImg.height * abs(cos(angle)) + pImg.width * abs(sin(angle)));
		
		PGraphics r = applet.createGraphics(newWidth, newHeight, PApplet.JAVA2D);
		
		r.beginDraw();
		
		r.backgroundColor = Color.gray.hashCode();
		r.fill(Color.gray.hashCode());
		
		r.translate(newWidth / 2, newHeight / 2);
		
		
		r.rotate(angle);
		r.translate(-newWidth / 2, -newHeight / 2);
		r.image(pImg, ( newWidth - pImg.width) / 2, ( newHeight - pImg.height) / 2);
		
		
		r.textSize(30);
		r.text("write some test text on it", 100, 100);
		
		r.endDraw();
		
		BufferedImage result = PImageBufferedImageConverter.toBufferedImage(r, "jpg");
		ImageIO.write(result, "jpg", new File("sample_rotate_resize_30.jpg"));
	}
	
	@Test
	public void testRotationAndZoomIn_() throws Exception {
		double angle = +(Math.PI) * (2d/3d);
		
		int newWidth = pImg.width;
		int newHeight = pImg.height;
		
		int originalWidth = pImg.width;
		int originalHeight = pImg.height;
		
		double originalAngle = Math.atan2(originalHeight, originalWidth);
		
		
		double newAngle = angle;
		double PI_2 = PI / 2d;
		System.out.println(newAngle);
		newAngle = newAngle > PI_2 ? PI - newAngle : newAngle;
		System.out.println(newAngle > PI_2);
		System.out.println(newAngle);
		newAngle += originalAngle;
		
		pImg.resize((int)(pImg.height * (double)(cos(originalAngle) / sin(newAngle))), 
				(int)(pImg.height *  (double)(sin(originalAngle) / sin(newAngle))));
		
		System.out.println(String.format("image[width: %d, height: %d]. rotated_image[width: %d, height: %d]", originalWidth, originalHeight, pImg.width, pImg.height ));
		
		PGraphics r = applet.createGraphics(newWidth, newHeight, PApplet.JAVA2D);
		
		r.beginDraw();
		
		r.backgroundColor = Color.gray.hashCode();
		r.fill(Color.cyan.hashCode());
		r.rect(0, 0, originalWidth, originalHeight);
		
		r.translate(originalWidth / 2, originalHeight / 2);
		r.rotate((float) angle);
		r.translate(-originalWidth / 2, -originalHeight / 2);
		r.image(pImg, ( originalWidth - pImg.width) / 2, ( originalHeight - pImg.height) / 2);
//		r.image(pImg, 0, 0);
		
		r.textSize(30);
		r.text("write some test text on it", 100, 100);
		
		r.endDraw();
		
		BufferedImage result = PImageBufferedImageConverter.toBufferedImage(r, "jpg");
		ImageIO.write(result, "jpg", new File("sample_rotate_resize_30.jpg"));
	}
}
