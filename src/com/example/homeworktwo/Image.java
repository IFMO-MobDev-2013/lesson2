package com.example.homeworktwo;

import android.graphics.Bitmap;

class Image {
	public int[] field;
	private int width, height;

	public Image(Bitmap bitmap) {
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		field = new int[width * height];
		bitmap.getPixels(field, 0, width, 0, 0, width, height);
	}

	public Bitmap toBitmap() {
		Bitmap res = Bitmap.createBitmap(field, width, height,
				Bitmap.Config.ARGB_8888);
		return res;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Image(int Width, int Height) {
		width = Width;
		height = Height;
		field = new int[width * height];
	}

	public void setPixel(int x, int y, int color) {
		int c = y * width + x;
		c = Math.min(c, width * height - 1);
		field[c] = color;
	}

	public int getPixel(int x, int y) {
		int c = y * width + x;
		c = Math.min(c, width * height - 1);
		return (int) field[c];
	}

	public void rotateImageClockwise() {
		int[] newField = new int[height * width];
		int iter = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				newField[x * height + (height - y - 1)] = field[iter];
				iter++;
			}
		}
		field = newField;
		int temp = width;
		width = height;
		height = temp;
	}
}