package com.example.l2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private int[][] image;
    private int height1;
    private int width1;
    private int[] colors;
    private Bitmap draw;
    private int[][] reduced;
    private ImgView iv;
    private Boolean mode;
    private double scale = 1.73;
    private int height;
    private int width;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        iv = new ImgView(this, bm);
        setContentView(iv);
        mode = true;

        height = bm.getHeight();
        width = bm.getWidth();
        image = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image[i][j] = bm.getPixel(i, height - 1 - j);
            }
        }

        /*colors = new int[width*height];
        for (int i = 0; i < height * width; i++) {
            colors[i] = image[i / height][i % width];
        }
        draw = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        draw.setPixels(colors, 0, width, 0, 0, width, height);
        iv.updateImage(draw); */


        int t = height;
        height = width;
        width = t;

        height1 = (int) (height / (1.73));
        width1 = (int) (width / (1.73));
        reduced = new int[height1 + 4][width1 + 4];
        colors = new int[height1 * width1];

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mode) {
                f1();
                iv.updateImage(draw);
                mode = false;
            } else {
                f2();
                iv.updateImage(draw);
                mode = true;
            }
        }

        return true;
    }

    private void f1() {
        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                reduced[i][j] = image[(int) (i * 1.73)][(int) (j * 1.73)];
            }
        }
        changeBrightness();
        for (int i = 0; i < height1 * width1; i++) {
            colors[i] = reduced[i / width1][i % width1];
        }
        draw = Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        draw.setPixels(colors, 0, width1, 0, 0, width1, height1);
    }

    private void f2() {
        resample();
        changeBrightness();
        for (int i = 0; i < height1 * width1; i++) {
            colors[i] = reduced[i / width1][i % width1];

        }
        draw = Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);
        draw.setPixels(colors, 0, width1, 0, 0, width1, height1);

    }

    private void changeBrightness() {
        int a, r, g, b;
        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                a = Color.alpha(reduced[i][j]);
                a = a + (int) (0.3 * (255 - a));
                r = Color.red(reduced[i][j]);
                r = r + (int) (0.3 * (255 - r));
                g = Color.green(reduced[i][j]);
                g = g + (int) (0.3 * (255 - g));
                b = Color.blue(reduced[i][j]);
                b = b + (int) (0.3 * (255 - b));
                reduced[i][j] = (a << 24) | (r << 16) | (g << 8) | (b);

            }
        }
    }

    public void resample() {
        double tmp;
        double u;
        double t;
        int h;
        int w;
        double d1, d2, d3, d4;
        int p1, p2, p3, p4;
        for (int j = 0; j < height1; j++) {
            tmp = (double) j * scale;
            h = (int) StrictMath.floor(tmp);

            if (h >= height - 1) {
                h = height - 2;
            }

            u = tmp - h;

            for (int i = 0; i < width1; i++) {

                tmp = (double) i * scale;
                w = (int) StrictMath.floor(tmp);

                if (w >= width - 1) {
                    w = width - 2;
                }

                t = tmp - w;


                d1 = (1 - t) * (1 - u);
                d2 = t * (1 - u);
                d3 = t * u;
                d4 = (1 - t) * u;

                p1 = image[h][w];
                p2 = image[h][w + 1];
                p3 = image[h + 1][w + 1];
                p4 = image[h + 1][w];

                int blue = (int) ((Color.blue(p1)) * d1 + Color.blue(p2) * d2 + Color.blue(p3) * d3 + Color.blue(p4) * d4);
                int green = (int) ((Color.green(p1)) * d1 + Color.green(p2) * d2 + Color.green(p3) * d3 + Color.green(p4) * d4);
                int red = (int) ((Color.red(p1)) * d1 + Color.red(p2) * d2 + Color.red(p3) * d3 + Color.red(p4) * d4);
                int alpha = (int) ((Color.alpha(p1)) * d1 + Color.alpha(p2) * d2 + Color.alpha(p3) * d3 + Color.alpha(p4) * d4);

                reduced[j][i] = (alpha << 24) | (red << 16) | (green << 8) | (blue);
            }
        }
    }

}


