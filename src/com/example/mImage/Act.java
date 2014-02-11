package com.example.mImage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class Act extends Activity {
    Bitmap bitmap;
    int bitmapHeight, bitmapWidth, mBitmapHeight, mBitmapWidth, displayHeight, displayWidth, x, y;
    int[] bitmapPixels, rotatePixels, mPixels;
    Display display;
    boolean quality;
    final static int FF = 0xff000000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Image(this));
    }

    class Image extends View implements View.OnClickListener {

        public Image(Context context) {
            super(context);
            this.setOnClickListener(this);
        }

        @Override
        public void onDraw(Canvas canvas) {

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            bitmapHeight = bitmap.getHeight();
            bitmapWidth = bitmap.getWidth();
            bitmapPixels = new int[bitmapHeight * bitmapWidth];
            bitmap.getPixels(bitmapPixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

            rotatePixels = new int[bitmapHeight * bitmapWidth];
            for (int i = 0; i < bitmapHeight; i++) {
                for (int j = 0; j < bitmapWidth; j++) {
                    rotatePixels[bitmapHeight * (j + 1) - (i + 1)] = bitmapPixels[bitmapWidth * i + j];
                }
            }
            int oldWidth = bitmapWidth;
            bitmapWidth = bitmapHeight;
            bitmapHeight = oldWidth;

            display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            displayHeight = display.getHeight();
            displayWidth = display.getWidth();

            int r, g, b;
            for(int i = 0; i < bitmapHeight * bitmapWidth; i++) {
                r = ((rotatePixels[i] >> 16) & 255) * 2;
                g = ((rotatePixels[i] >> 8) & 255) * 2;
                b = (rotatePixels[i] & 255) * 2;

                if (r > 255) {
                    r = 255;
                }
                if (g > 255) {
                    g = 255;
                }
                if (b > 255) {
                    b = 255;
                }
                rotatePixels[i] = (r << 16) | (g << 8) | b;
            }

            mBitmapHeight = (int) (bitmapHeight / 1.73);
            mBitmapWidth = (int) (bitmapWidth / 1.73);
            mPixels = new int[mBitmapHeight * mBitmapWidth];

            if (quality) {
                int a, step = 0;
                int[] colors;
                double dx, dy;
                for(int i = 0; i < mBitmapHeight; i++) {
                    for(int j = 0; j < mBitmapWidth; j++) {
                        dx = (j * 1.73) - (int)(j * 1.73);
                        dy = (i * 1.73) - (int)(i * 1.73);
                        a = (int) (i * 1.73) * bitmapWidth + (int)(j * 1.73);
                        colors = getColors(dx, dy, a);
                        mPixels[step] = FF | colors[0] | colors[1] | colors[2];
                        step++;
                    }
                }
                canvas.drawBitmap(mPixels, 0, mBitmapWidth, 0, 0,  mBitmapWidth, mBitmapHeight, false, null);
            } else {
                for(int i = 0; i < mBitmapHeight; ++i) {
                    for(int j = 0; j < mBitmapWidth; ++j) {
                        x = ((j * ((bitmapWidth << 16) / mBitmapWidth + 1)) >> 16);
                        y = ((i * ((bitmapHeight << 16) / mBitmapHeight + 1)) >> 16);
                        mPixels[i * mBitmapWidth + j] = rotatePixels[y * bitmapWidth + x];
                    }
                }
                canvas.drawBitmap(mPixels, 0, mBitmapWidth, 0, 0,  mBitmapWidth, mBitmapHeight, false, null);
            }
        }

        public int[] getColors(double dx, double dy, int a0) {
            int[] colors = new int[3];
            int a1 = rotatePixels[a0], a2 = rotatePixels[a0 + 1], a3 = rotatePixels[a0 + bitmapWidth], a4 = rotatePixels[a0 + bitmapWidth + 1];
            int red = (int) (((a1 >> 16) & 255) * (1 - dx) * (1 - dy) + ((a2 >> 16) & 255) * dx * (1 - dy) + ((a3 >> 16) & 255) * (1 - dx) * dy + ((a4 >> 16) & 255) * dx * dy);
            int green = (int) (((a1 >> 8) & 255) * (1 - dx) * (1 - dy) + ((a2 >> 8) & 255) * dx * (1 - dy) + ((a3 >> 8) & 255) * (1 - dx) * dy + ((a4 >> 8) & 255) * dx * dy);
            int blue = (int) ((a1 & 255) * (1 - dx) * (1 - dy) + (a2 & 255) * dx * (1 - dy) + (a3 & 255) * (1 - dx) * dy + (a4 & 255) * dx * dy);
            colors[0] = (red & 255) << 16;
            colors[1] = (green & 255) << 8;
            colors[2] = blue & 255;
            return colors;
        }

        @Override
        public void onClick(View view) {
            quality = !quality;
            view.invalidate();
        }
    }
}
