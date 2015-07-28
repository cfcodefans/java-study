package cf.study.media.image.processing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import processing.core.PImage;

public final class PImageBufferedImageConverter {
	private static final Logger LOG = LoggerFactory
			.getLogger(PImageBufferedImageConverter.class);

	private PImageBufferedImageConverter() {
	}

	public static PImage toPImage(final BufferedImage image,
			final String extension) {
		if (image == null) {
			throw new IllegalArgumentException(
					"coverting buffered image to PImage howerver te buffered image is empty");
		}
		final long start = Calendar.getInstance().getTimeInMillis();
		final PImage pImage = new PImage(image.getWidth(), image.getHeight(),
				getColorModel(extension));

		final DataBuffer dataBuffer = image.getRaster().getDataBuffer();

		if (dataBuffer instanceof DataBufferByte) {
			pImage.pixels = convertTo2DWithoutUsingGetRGB(image);
		} else {
			image.getRGB(0, 0, pImage.width, pImage.height, pImage.pixels, 0,
					pImage.width);
		}
		pImage.updatePixels();
		final long end = Calendar.getInstance().getTimeInMillis();
		LOG.debug("elapsed time to convert to pimage {}", (end - start));
		pImage.parent = new PApplet();
		return pImage;
	}

	private static int[] convertTo2DWithoutUsingGetRGB(final BufferedImage image) {
		final byte[] pixels = ((DataBufferByte) image.getRaster()
				.getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[] result = new int[height * width];
		int index = 0;
		if (hasAlphaChannel) {
			final int pixelLength = 4;
			for (int pixel = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
				argb += ((int) pixels[pixel + 1] & 0xff); // blue
				argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
				result[index++] = argb;
				col++;
				if (col == width) {
					col = 0;
				}
			}
		} else {
			final int pixelLength = 3;
			for (int pixel = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += -16777216; // 255 alpha
				argb += ((int) pixels[pixel] & 0xff); // blue
				argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
				result[index++] = argb;
				col++;
				if (col == width) {
					col = 0;
				}
			}
		}

		return result;
	}

	public static BufferedImage toBufferedImage(final PImage pImage,
			final String extension) {

		// pImage.loadPixels();
		// DataBuffer buffer=new DataBufferInt(pImage.pixels,
		// pImage.width*pImage.height);
		// WritableRaster raster=Raster.createPackedRaster(
		// buffer,
		// pImage.width,
		// pImage.height,
		// pImage.width,
		// RGB_MASKS,
		// null);
		// BufferedImage bimage=new BufferedImage(RGB_OPAQUE, raster, false,
		// null);

		final BufferedImage bimage = new BufferedImage(pImage.width,
				pImage.height, getColorModel(extension));
		final WritableRaster wr = bimage.getRaster();
		wr.setDataElements(0, 0, pImage.width, pImage.height, pImage.pixels);
		return bimage;

	}

	private static int getColorModel(final String extension) {
		if ("png".equalsIgnoreCase(extension)) {
			return 2;
		}
		return 1;
	}

}
