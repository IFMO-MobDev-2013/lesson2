package com.example.Task2;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;

public class MyActivity extends Activity {

    class ImgTransform extends View implements View.OnClickListener {
        Bitmap bitmap;
        int[] pixels;
        int[] newPixels;
        boolean fast = false;
        int height, width, nwidth, nheight;

        public ImgTransform(Context contex) {
            super(contex);
            this.setOnClickListener(this);
        }

        public void getPict() {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            pixels = new int [width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        }

        public void rotate() {
            int[] temp = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    temp[j * height + height - 1 - i] = pixels[i * width + j];
                }
            }
            pixels = temp;
            int x = width;
            width = height;
            height = x;
        }

        public void addContrast(int correction) {
            int avContrast = 0;
            int R = 0, G = 0, B = 0;
            for (int i = 0; i < width * height; i++) {
                R = (pixels[i] >> 16) & 255;
                G = (pixels[i] >> 8) & 255;
                B = pixels[i] & 255;
                avContrast += (int)(R * 0.299 + G * 0.587 + B * 0.114);
            }
            avContrast /= (width * height);  //average brightness
            double k = 1.0 + correction / 100.0;
            int[] newColors = new int[256];
            for (int i = 0; i < 256; i++) {
                int delta = i - avContrast;
                int temp = (int)(avContrast + k * delta);
                if (temp < 0) temp = 0;
                if (temp > 255) temp = 255;
                newColors[i] = temp;
            }
            int ind;
            for (int i = 0; i < width * height; i++) {
                ind = pixels[i];
                pixels[i] =  (newColors[(ind >> 16) & 255] << 16) + (newColors[(ind >> 8) & 255] << 8) + newColors[ind & 255];
            }
        }

        public void addBrightness(int correction) {
            int R, G, B;
            double k = 1.0 + (double)correction / 100;
            for (int i = 0; i < width * height; i++) {
                R = (pixels[i] >> 16) & 255;
                G = (pixels[i] >> 8) & 255;
                B = pixels[i] & 255;

                R = (int)(R * k);
                G = (int)(G * k);
                B = (int)(B * k);

                if (R > 255) R = 255;
                if (G > 255) G = 255;
                if (B > 255) B = 255;

                pixels[i] = (R << 16) | (G << 8) | B;
            }
        }

        public int[] fastScale(int w1, int h1, int w2, int h2) {
            int[] temp = new int[w2 * h2];
            int x_ratio = (int) ((w1 << 16) / w2) + 1;
            int y_ratio = (int) ((h1 << 16) / h2) + 1;
            int x2, y2;
            for (int i = 0; i < h2; i++) {
                for (int j = 0; j < w2; j++) {
                    x2 = ((j * x_ratio) >> 16);
                    y2 = ((i * y_ratio) >> 16);
                    temp[i * w2 + j] = pixels[y2 * w1 + x2];
                }
            }
            return temp;
        }

        public int[] goodScale(int w1, int h1, int w2, int h2) {
            int[] temp = new int [w2 * h2];
            int a, b, c, d, x, y, ind;
            float x_ratio = ((float)(w1 - 1)) / w2;
            float y_ratio = ((float)(h1 - 1)) / h2;
            float dx, dy, blue, red ,green;
            int offset = 0;
            for (int i = 0; i < h2; i++) {
                for (int j = 0; j < w2; j++) {
                    x = (int)(x_ratio * j);
                    y = (int)(y_ratio * i);
                    dx = (x_ratio * j) - x;
                    dy = (y_ratio * i) - y;
                    ind = y * w1 + x;
                    a = pixels[ind];
                    b = pixels[ind + 1];
                    c = pixels[ind + w1];
                    d = pixels[ind + w1 + 1];

                    blue = (a & 255) * (1 - dx) * (1 - dy) + (b & 255) * dx * (1-dy) + (c & 255) * dy * (1-dx) + (d & 255) * (dx* dy);
                    green = ((a >> 8) & 255) * (1 - dx)*(1 - dy) + (( b>> 8) & 255) * dx * (1 - dy) + ((c >> 8) & 255) * dy * (1 - dx) + ((d >> 8) & 255) * (dx * dy);
                    red = ((a >> 16) & 255) * (1 - dx) * (1 - dy) + ((b >> 16) & 255)* dx * (1 - dy) + ((c >> 16) & 255) * dy * (1 - dx) + ((d >> 16) & 255) * (dx * dy);

                    temp[offset++] = 0xff000000 | ((((int)red) & 255) << 16) | ((((int)green) & 255) << 8) | (((int)blue) & 255) ;
                }
            }
            return temp;
        }

        @Override
        public void onDraw(Canvas canvas) {
            getPict(); //get picture

            rotate();  //rotate clockwise by 90 degrees

            //addContrast(100);
            //canvas.drawColor(Color.BLACK);
            //canvas.drawBitmap(pixels, 0, width, 0, 0, width, height, false, null);    //draw bright rotated picture

            addBrightness(100);    // +100%

            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(pixels, 0, width, 0, 0, width, height, false, null);

            nwidth = 434;
            nheight = 405;
            newPixels = new int [nwidth * nheight];
            if (fast) newPixels = fastScale(width, height, nwidth, nheight);
            else newPixels = goodScale(width, height, nwidth, nheight);

            fast = !fast;

            Display display =((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            int w = display.getWidth();
            int h = display.getHeight();
            int x = w / 2 - nwidth / 2;
            int y = h / 2 - nheight / 2;

            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(newPixels, 0, nwidth, x, y, nwidth, nheight, false, null);
        }

        @Override
        public void onClick(View v) {
            v.invalidate();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new ImgTransform(this));
    }
}
