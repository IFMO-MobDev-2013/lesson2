package com.example.lesson2;

import java.util.Random;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {

	int newheight;
	int newwidth;
	int[][] reduce;
	Bitmap srcBitmapLocal;
	int[][] image;
	int[][] imageRotation;
	int[] h;
	int[] w;
	Bitmap a;
	int lop;
	int[] b;
	int height, width, height1, width1;
	ImageView targetImage;
	int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		targetImage = (ImageView) findViewById(R.id.ImageContainer);
		srcBitmapLocal = BitmapFactory.decodeResource(getApplicationContext()
				.getResources(), R.drawable.source);

		height1 = srcBitmapLocal.getHeight();
		width1 = srcBitmapLocal.getWidth();// 750 700
		image = new int[height1][width1];
		read();
		imageRotation = new int[width1][height1];
		height = width1;
		width = height1;
		newheight = (int) (height / 1.73);
		newwidth = (int) (width / 1.73);
		h = new int[newheight];
		w = new int[newwidth];
		b = new int[newwidth * newheight];
		reduce = new int[newheight][newwidth];
		set();
		rotation();
		fast();

		targetImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (count == 0) {
					count = 1;
					longtime();
				} else {
					count = 0;
					fast();
				}

			}
		});

	}

	private void read() {
		for (int i = 0; i < height1; i++)
			for (int j = 0; j < width1; j++) {
				image[i][j] = srcBitmapLocal.getPixel(j, i);
			}
	}

	private void set() {
		for (int i = 0; i < newheight; i++) {
			h[i] = (int) (i * 1.73);
		}
		for (int i = 0; i < newwidth; i++) {
			w[i] = (int) (i * 1.73);
		}
	}

	private void rotation() {
		for (int i = height - 1; i >= 0; i--)
			for (int j = 0; j < width; j++) {
				imageRotation[i][width - 1 - j] = image[j][i];
			}

	}

	private void fast() {
		for (int i = 0; i < newheight; i++) {
			for (int j = 0; j < newwidth; j++) {
				reduce[i][j] = imageRotation[h[i]][w[j]];
			}
		}
		bright();
		for (int i = 0; i < newwidth * newheight; i++) {
			b[i] = reduce[i / newwidth][i % newwidth];
		}
		a = Bitmap.createBitmap(newwidth, newheight, Config.ARGB_8888);
		a.setPixels(b, 0, newwidth, 0, 0, newwidth, newheight);
		targetImage.setImageBitmap(a);

	}

	private void longtime() {
		Random t = new Random();
		for (int i = 0; i < newheight - 1; ++i) {
			for (int j = 0; j < newwidth - 1; ++j) {
				reduce[i][j] = imageRotation[h[i] + t.nextInt(h[i + 1] - h[i])][w[j]
						+ t.nextInt(w[j + 1] - w[j])];

			}
		}
		bright();
		for (int i = 0; i < newwidth * newheight; i++) {
			b[i] = reduce[i / newwidth][i % newwidth];
		}
		a = Bitmap.createBitmap(newwidth, newheight, Config.ARGB_8888);
		a.setPixels(b, 0, newwidth, 0, 0, newwidth, newheight);
		targetImage.setImageBitmap(a);

	}

	private void bright() {
		int a, r, g, b;
		for (int i = 0; i < newheight; i++) {
			for (int j = 0; j < newwidth; j++) {
				a = Color.alpha(reduce[i][j]);
				a = a + (int) (0.3 * (255 - a));
				r = Color.red(reduce[i][j]);
				r = r + (int) (0.3 * (255 - r));
				g = Color.green(reduce[i][j]);
				g = g + (int) (0.3 * (255 - g));
				b = Color.blue(reduce[i][j]);
				b = b + (int) (0.3 * (255 - b));
				reduce[i][j] = (a << 24) | (r << 16) | (g << 8) | (b);

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
