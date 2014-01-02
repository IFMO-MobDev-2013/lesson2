package com.ifmomd.lesson2;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageHandler {
	private static int[] image;
	private static int[] fastProcessingImage;
	private static int[] qualityProcessingImage;

	private final int IMAGE_WIDTH;
	private final int IMAGE_HEIGHT;
	public static final int NEW_WIDTH = 405;
	public static final int NEW_HEIGHT = 434;

	public static int[] getFastProcessingImage() {
		return fastProcessingImage;
	}

	public static int[] getQualityProcessingImage() {
		return qualityProcessingImage;
	}

	public ImageHandler (Bitmap source) {
		IMAGE_HEIGHT = source.getHeight();
		IMAGE_WIDTH = source.getWidth();
		image = new int[IMAGE_WIDTH*IMAGE_HEIGHT];
		source.getPixels(image, 0, IMAGE_WIDTH, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		fastProcessing();
		qualityProcessing();
	}

	private int[] rotate(int[] image) {
		int[] result = new int[IMAGE_HEIGHT*IMAGE_WIDTH];

		for (int x = 0; x < IMAGE_HEIGHT; x++) {
			for (int y = 0; y < IMAGE_WIDTH; y++) {
				result[y*IMAGE_WIDTH + (IMAGE_HEIGHT-x-1)] = image[x*IMAGE_WIDTH + y];
			}
		}

		return result;
	}

	private int[] increaseBrightness(int[] image) {
		int[] result = new int[IMAGE_HEIGHT*IMAGE_WIDTH];

		for (int i = 0; i < image.length; i++) {
			int pixel = image[i];
			result[i] = Color.argb(
					Math.min(255, (int)((double)Color.alpha(pixel) * 1.5f)),
					Math.min(255, (int)((double)Color.red(pixel)   * 1.5f)),
					Math.min(255, (int)((double)Color.green(pixel) * 1.5f)),
					Math.min(255, (int)((double)Color.blue(pixel)  * 1.5f)));
		}

		return result;
	}

	private int[] fastScale(int[] image, int newWidth, int newHeight) {
		int[] result = new int[newWidth*newHeight];

		int xRatio = IMAGE_WIDTH / newWidth;
		int yRatio = IMAGE_HEIGHT / newHeight;
		for (int i = 0; i < newHeight; i++) {
			for (int j = 0; j < newWidth; j++) {
				result[(i * newWidth) + j] = image[(i*yRatio * IMAGE_WIDTH) + (j*xRatio)];
			}
		}

		return result;
	}

	private int[] qualityScale(int[] image, int newWidth, int newHeight) {
		int[] result = new int[newWidth*newHeight];

		int p1, p2, p3, p4, x, y, index, h, w;
		int xRatio = (IMAGE_WIDTH-1) / newWidth;
		int yRatio = (IMAGE_HEIGHT-1) / newHeight;
		float blue, red, green;
		float t, u, d1, d2, d3, d4;

		int offset = 0;
		for (int i = 0; i < newHeight; i++) {
			h = (int)Math.floor(i / (newHeight-1) * (IMAGE_HEIGHT-1));
			h = (h < 0) ? 0 : h;
			h = (h >= IMAGE_HEIGHT-1) ? IMAGE_HEIGHT-2 : h;
			u = (i / (newHeight-1) * (IMAGE_HEIGHT-1)) - h;

			for (int j = 0; j < newWidth; j++) {
				w = (int)Math.floor((i) / (newWidth - 1) * (IMAGE_WIDTH - 1));
				w = (w < 0) ? 0 : w;
				w = (w >= IMAGE_WIDTH-1) ? IMAGE_WIDTH-2 : w;
				t = ((i) / (newWidth-1) * (IMAGE_WIDTH-1)) - w;

				x = (xRatio * j);
				y = (yRatio * i);
				index = (y*IMAGE_WIDTH + x);

				//coefficients
				d1 = (1 - t) * (1 - u);
				d2 = t * (1 - u);
				d3 = t * u;
				d4 = (1 - t) * u;

				//surrounding pixels
				p1 = image[index];
				p2 = image[index + 1];
				p3 = image[index + IMAGE_WIDTH];
				p4 = image[index + IMAGE_WIDTH + 1];

				blue = p1 *d1 + p2 *d2 + p3 *d3 + p4 *d4;
				green = (p1 >> 8) * d1 + (p2 >> 8) * d2 + (p3 >> 8) * d3 + (p4 >> 8) * d4;
				red = (p1 >> 16) * d1 + (p2 >> 16) * d2 + (p3 >> 16) * d3 + (p4 >> 16) * d4;

				result[offset++] =
						0xff000000                       | // alpha
								((((int) red) << 16) & 0xff0000) |
								((((int) green) << 8) & 0xff00)  |
								((int) blue);
			}
		}

		return result;
	}

	private void fastProcessing() {
		fastProcessingImage = fastScale(increaseBrightness(rotate(image)), NEW_WIDTH, NEW_HEIGHT);
	}

	private void qualityProcessing() {
		qualityProcessingImage = qualityScale(increaseBrightness(rotate(image)), NEW_WIDTH, NEW_HEIGHT);
	}
}
