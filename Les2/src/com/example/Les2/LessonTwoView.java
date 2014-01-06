package com.example.Les2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class LessonTwoView extends View implements View.OnClickListener {
	public static final double K = 1.73;
	float s;
	boolean flag = true;
	int[] sourceImg, qualityImg, fastImg;
	int sourceHeight, sourceWidth, newWidth, newHeight;


	public LessonTwoView(Context context, int displayHeight, int displayWidth) {
		super(context);

		Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.source);
		sourceWidth = source.getWidth();
		sourceHeight = source.getHeight();
		newWidth = (int) ((double) sourceWidth / K);
		newHeight = (int) ((double) sourceHeight / K);

		sourceImg = new int[sourceWidth * sourceHeight];
		qualityImg = new int[newWidth * newHeight];
		fastImg = new int[newWidth * newHeight];
		source.getPixels(sourceImg, 0, sourceWidth, 0, 0, sourceWidth, sourceHeight);

		rotation();
		brightness();
		fastImage();
		qualityImage();

		s = (newWidth > displayWidth || newHeight > displayHeight) ?
				((newWidth > displayWidth) ?
						((float) displayWidth) / newWidth
						: ((float) displayHeight) / newHeight)
				: 1;

		this.setOnClickListener(this);
	}


	private void brightness() {
		int curRed, curGreen, curBlue;
		for (int t = 0; t < sourceWidth * sourceHeight; ++t) {

			curRed = sourceImg[t] & 0x00FF0000;
			curGreen = sourceImg[t] & 0x0000FF00;
			curBlue = sourceImg[t] & 0x000000FF;

			curRed = curRed >> 16;
			curGreen = curGreen >> 8;

			curRed = (curRed * 1.5 <= 255) ? (int) (curRed * 1.5) : 255;
			curGreen = (curGreen * 1.5 <= 255) ? (int) (curGreen * 1.5) : 255;
			curBlue = (curBlue * 1.5 <= 255) ? (int) (curBlue * 1.5) : 255;

			sourceImg[t] = 0xFF000000 | curRed << 16 | curGreen << 8 | curBlue;
		}
	}


	private void rotation() {
		int[] t = new int[sourceHeight * sourceWidth];
		for (int i = 0; i < sourceHeight; ++i) {
			for (int j = 0; j < sourceWidth; ++j) {
				t[j * sourceHeight + sourceHeight - i - 1] = sourceImg[i * sourceWidth + j];
			}
		}

		sourceImg = t;

		int tmp = sourceHeight;
		sourceHeight = sourceWidth;
		sourceWidth = tmp;

		tmp = newHeight;
		newHeight = newWidth;
		newWidth = tmp;
	}


	private void fastImage() {
		for (int i = 0; i < newHeight; ++i) {
			for (int j = 0; j < newWidth; ++j) {
				fastImg[i * newWidth + j] = sourceImg[(int) (i * K) * sourceWidth + (int) (j * K)];
			}
		}
	}

	private void qualityImage() {
		int x, y;
		int [] point = new int[4];
		float [] d = new float[4];
		float dx, dy;
		float red, green, blue;
		int t = 0;
		for (int i = 0; i < newHeight; i++) {
			for (int j = 0; j < newWidth; j++, t++) {
				x = (int) (K * j);
				y = (int) (K * i);
				int pos = y * sourceWidth + x;
				point[0] = sourceImg[pos];
				point[1] = sourceImg[pos + 1];
				point[2] = sourceImg[pos + sourceWidth];
				point[3] = sourceImg[pos + sourceWidth + 1];

				dx = (float) (K * j) - x;
				dy = (float) (K * i) - y;
				d[0] = (1 - dx) * (1 - dy);
				d[1] = dx * (1 - dy);
				d[2] = dy * (1 - dx);
				d[3] = dx * dy;
				
				red = ((point[0] >> 16) & 255) * d[0] + ((point[1] >> 16) & 255) * d[1] + ((point[2] >> 16) & 255) * d[2] + ((point[3] >> 16) & 255) * d[3];
				green = ((point[0] >> 8) & 255) * d[0] + ((point[1] >> 8) & 255) * d[1] + ((point[2] >> 8) & 255) * d[2] + ((point[3] >> 8) & 255) * d[3];
				blue = (point[0] & 255) * d[0] + (point[1] & 255) * d[1] + (point[2] & 255) * d[2] + (point[3] & 255) * d[3];

				qualityImg[t] = 0xff000000 | ((((int) red) & 255) << 16) | ((((int) green) & 255) << 8) | (((int) blue) & 255);

			}
		}
	}


	@Override
	public void onDraw(Canvas canvas) {
		canvas.scale(s, s);
		if (flag)
			canvas.drawBitmap(fastImg, 0, newWidth, 0, 0, newWidth, newHeight, false, null);
		else
			canvas.drawBitmap(qualityImg, 0, newWidth, 0, 0, newWidth, newHeight, false, null);
		flag = !flag;
	}

	@Override
	public void onClick(View v) {
		v.invalidate();
	}

}
