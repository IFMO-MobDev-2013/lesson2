package com.mobdev.rotate;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
	ImageView image;
	Bitmap b1, b2, b3, b4, bMap, bMap2;
	int k = 0;
	final int w2 = 405;
	final int h2 = 434;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		go();
	}

	public void go() {
		image = (ImageView) findViewById(R.id.imageView1);
		bMap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
		bMap = bMap.copy(Bitmap.Config.RGB_565, true);

		int width = bMap.getWidth();
		int height = bMap.getHeight();

		int[] intArray = new int[width * height + 1];

		bMap.getPixels(intArray, 0, width, 0, 0, width, height);

		int[] arr = intArray;
		int[] arr2 = intArray;

		arr = resizeBilinear(arr, width, height, w2, h2);
		arr2 = resizePixels(arr2, width, height, w2, h2);

		int[] arrp = new int[arr.length];
		int[] arr2p = new int[arr.length];

		rotate(arr, arrp, w2, h2);
		rotate(arr2, arr2p, w2, h2);

		b1 = Bitmap.createBitmap(arrp, h2, w2, Bitmap.Config.RGB_565);
		b2 = Bitmap.createBitmap(arr2p, h2, w2, Bitmap.Config.RGB_565);

		b1 = b1.copy(Bitmap.Config.RGB_565, true);
		b2 = b2.copy(Bitmap.Config.RGB_565, true);

		upBright(b1, h2, w2);
		upBright(b2, h2, w2);

		image.setImageBitmap(bMap);
		image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				screen();
			}
		});

	}

	public int[] resizeBilinear(int[] pixels, int w, int h, int w2, int h2) {
		int[] temp = new int[w2 * h2 + 1];
		int a, b, c, d, x, y, index;
		float x_ratio = ((float) (w - 1)) / w2;
		float y_ratio = ((float) (h - 1)) / h2;
		float x_diff, y_diff, blue, red, green;
		int offset = 0;
		for (int i = 0; i < h2; i++) {
			for (int j = 0; j < w2; j++) {
				x = (int) (x_ratio * j);
				y = (int) (y_ratio * i);
				x_diff = (x_ratio * j) - x;
				y_diff = (y_ratio * i) - y;
				index = (y * w + x);
				a = pixels[index];
				b = pixels[index + 1];
				c = pixels[index + w];
				d = pixels[index + w + 1];

				blue = (a & 0xff) * (1 - x_diff) * (1 - y_diff) + (b & 0xff)
						* (x_diff) * (1 - y_diff) + (c & 0xff) * (y_diff)
						* (1 - x_diff) + (d & 0xff) * (x_diff * y_diff);

				green = ((a >> 8) & 0xff) * (1 - x_diff) * (1 - y_diff)
						+ ((b >> 8) & 0xff) * (x_diff) * (1 - y_diff)
						+ ((c >> 8) & 0xff) * (y_diff) * (1 - x_diff)
						+ ((d >> 8) & 0xff) * (x_diff * y_diff);

				red = ((a >> 16) & 0xff) * (1 - x_diff) * (1 - y_diff)
						+ ((b >> 16) & 0xff) * (x_diff) * (1 - y_diff)
						+ ((c >> 16) & 0xff) * (y_diff) * (1 - x_diff)
						+ ((d >> 16) & 0xff) * (x_diff * y_diff);

				temp[offset++] = 0xff000000 | ((((int) red) << 16) & 0xff0000)
						| ((((int) green) << 8) & 0xff00) | ((int) blue);
			}
		}
		return temp;
	}

	public int[] resizePixels(int[] pixels, int w1, int h1, int w2, int h2) {
		int[] temp = new int[w2 * h2 + 1];
		int x_ratio = (int) ((w1 << 16) / w2) + 1;
		int y_ratio = (int) ((h1 << 16) / h2) + 1;
		int x2, y2;
		for (int i = 0; i < h2; i++) {
			for (int j = 0; j < w2; j++) {
				x2 = ((j * x_ratio) >> 16);
				y2 = ((i * y_ratio) >> 16);
				temp[(i * w2) + j] = pixels[(y2 * w1) + x2];
			}
		}
		return temp;
	}

	public void rotate(int[] first, int[] second, int width, int height) {
		for (int i = 0; i < first.length; i++) {
			second[i] = first[(height - (i % height) - 1) * width + i / height];
		}
	}

	public static void upBright(Bitmap src, int w, int h) {
		int R, G, B, pixel;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				pixel = src.getPixel(i, j);
				B = pixel & 0x000000FF;
				G = (pixel & 0x0000FF00) >> 8;
				R = (pixel & 0x00FF0000) >> 16;
				if (B + B > 255)
					B = 255;
				else
					B = B + B;
				if (G + G > 255)
					G = 255;
				else
					G = G + G;
				if (R + R > 255)
					R = 255;
				else
					R = R + R;
				pixel = pixel & 0xFF000000;
				pixel = pixel | (R << 16) | (G << 8) | B;
				src.setPixel(i, j, pixel);
			}
		}
	}

	public void screen() {
		if (k == 0) {
			image.setImageBitmap(bMap);
			k++;
		} else if (k == 1) {
			image.setImageBitmap(b1);
			k++;
		} else if (k == 2) {
			image.setImageBitmap(b2);
			k++;
		} else {
			image.setImageBitmap(bMap);
			k = 1;
		}
	}

}
