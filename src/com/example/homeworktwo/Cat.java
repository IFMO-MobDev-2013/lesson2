package com.example.homeworktwo;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Cat extends Activity implements OnTouchListener {
	ImageView canvas;
	Bitmap source;

	double[] GAMMA = new double[256];
	double S_RGB_GAMMA = 0.45;

	boolean flag = true;
	Image brute, average;

	public void calculateGamma() {
		for (int i = 0; i < 256; ++i) {
			GAMMA[i] = Math.pow(i / 255., 1 / S_RGB_GAMMA);
		}
	}

	private int invGammaCorrection(double color, double brightness) {
		return (int) Math.round(255. * Math.min(1,
				brightness * Math.pow(color, S_RGB_GAMMA)));
	}

	private int gammaCorrection(double newDoubleField, int cntNewField,
			double brightness) {
		return (int) Math.round(255. * Math.min(
				1,
				brightness
						* Math.pow(newDoubleField / cntNewField, S_RGB_GAMMA)));
	}

	private int makeBrightly(int color) {
		double r = GAMMA[Color.red(color)];
		double g = GAMMA[Color.green(color)];
		double b = GAMMA[Color.blue(color)];
		return Color.argb(0xFF, invGammaCorrection(r, 2.),
				invGammaCorrection(g, 2.), invGammaCorrection(b, 2.));
	}

	public Image resizeBitmapBrute(Image src, double scale) {
		long start = System.nanoTime();
		src.rotateImageClockwise();
		Log.i("Brute1: ", "" + ((System.nanoTime() - start) / 1000000)); 
		Image result = new Image((int) (src.getWidth() / scale),
				(int) (src.getHeight() / scale));
		for (int y = 0; y < result.getHeight(); y++) {
			for (int x = 0; x < result.getWidth(); x++) {
				int newX = (int) (x * scale);
				int newY = (int) (y * scale);
				result.setPixel(x, y, makeBrightly(src.getPixel(newX, newY)));
			}
		}
		return result;
	}

	public Image resizeBitmapAverage(Image src, double scale) {
		src.rotateImageClockwise();

		int height = src.getHeight();
		int width = src.getWidth();
		Image result = new Image((int) Math.round(width / scale),
				(int) Math.round(height / scale));
		int size = result.getWidth() * result.getHeight();
		float[] newDoubleFieldR = new float[size];
		float[] newDoubleFieldG = new float[size];
		float[] newDoubleFieldB = new float[size];
		short[] cntNewField = new short[size];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				int newX = (int) (Math.round(x / scale));
				int newY = (int) (Math.round(y / scale));
				int c = newX + newY * result.getWidth();
				int color = src.getPixel(x, y);
				c = Math.min(c, result.getWidth() * result.getHeight() - 1);
				newDoubleFieldR[c] += GAMMA[Color.red(color)];
				newDoubleFieldG[c] += GAMMA[Color.green(color)];
				newDoubleFieldB[c] += GAMMA[Color.blue(color)];
				cntNewField[c]++;
			}
		}
		for (int i = 0; i < result.getWidth() * result.getHeight(); i++) {
			int r = gammaCorrection(newDoubleFieldR[i], cntNewField[i], 2.);
			int g = gammaCorrection(newDoubleFieldG[i], cntNewField[i], 2.);
			int b = gammaCorrection(newDoubleFieldB[i], cntNewField[i], 2.);
			result.field[i] = Color.argb(0xFF, r, g, b);
		}
		return result;
	}

	private void run() {
		canvas = (ImageView) findViewById(R.id.imageView);
		source = BitmapFactory
				.decodeResource(getResources(), R.drawable.source);
		calculateGamma();
		flag = true;
		draw();
		canvas.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					draw();
				}
				return true;
			}
		});
	}

	private void draw() {
		if (flag) {
			long start = System.nanoTime();
			brute = resizeBitmapBrute(new Image(source), 2.);
			canvas.setImageBitmap(brute.toBitmap());
			Log.i("Brute: ", "" + ((System.nanoTime() - start) / 1000000)); 
		} else {
			long start = System.nanoTime();
			average = resizeBitmapAverage(new Image(source), 2.);
			canvas.setImageBitmap(average.toBitmap());
			Log.i("Average: ", "" + ((System.nanoTime() - start) / 1000000));
		}
		flag = !flag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cat);
		run();

	}

	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			draw();
		}
		return true;
	}
}
