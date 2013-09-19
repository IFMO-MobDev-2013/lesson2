package ru.ifmo.ctddev.skripnikov.androidhw2;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MyActivity extends Activity {
    private static final int W = 405;
    private static final int H = 434;
    private int oldW;
    private int oldH;
    private int[] m = new int[H];
    private Paint p = new Paint();
    private Bitmap original;

    private class myView extends View implements View.OnClickListener {
        private boolean fast = true;

        public myView(Context context) {
            super(context);
        }

        @Override
        public void onClick(View v) {
            v.invalidate();
        }

        public void onDraw(Canvas canvas) {
            long time1 = System.currentTimeMillis();
            int[] pixels;
            if (fast) {
                pixels = fastScale();
                fast = false;
            } else {
                pixels = veryLongScale();
                fast = true;
            }
            pixels = rotate(pixels);
            lightenUp(pixels);
            canvas.drawBitmap(pixels, 0, H, 0, 0, H, W, false, p);
            long time2 = System.currentTimeMillis();
            canvas.drawText("TIME: " + Long.toString(time2 - time1) + " ms", 20, 20, p);
        }
    }

    private int[] fastScale() {
        int[] o = new int[oldW * oldH];
        original.getPixels(o, 0, oldW, 0, 0, oldW, oldH);
        int[] pixels = new int[W * H];
        for (int h = 0; h < H; h++)
            for (int w = 0; w < W; w++)
                pixels[h * W + w] = o[m[h] * oldW + m[w]];
        return pixels;
    }

    private int[] veryLongScale() {
        Pixel[][] pixel = new Pixel[H][W];
        double k = (double) W / oldW;
        double kk = 100 / k / k;
        double[] d = new double[oldH+1];
        for (int i = 0; i <= oldH; i++)
            d[i] = i*k;
        int[] id = new int[oldH+1];
        for (int i = 0; i <= oldH; i++)
            id[i] = (int)d[i];
        for (int h = 0; h < H; h++)
            for (int w = 0; w < W; w++)
                pixel[h][w] = new Pixel();
        int[] o = new int[oldW * oldH];
        original.getPixels(o, 0, oldW, 0, 0, oldW, oldH);
        for (int h = 0; h < oldH; h++)
            for (int w = 0; w < oldW; w++) {
                if (id[w+1] >= 405 || id[h+1] >= 434) {
                    continue;
                }
                if (id[w+1] > id[w] && id[h+1] > id[h]) {
                    pixel[id[h]][id[w]].setColor(o[h * oldW + w], (id[w+1] - d[w]) * (id[h+1] - d[h]) * kk);
                    pixel[id[h+1]][id[w+1]].setColor(o[h * oldW + w], (d[w+1] - id[w+1]) * (d[h+1] - id[h+1]) * kk);
                    pixel[id[h]][id[w+1]].setColor(o[h * oldW + w], (id[w+1] - id[w+1]) * (id[h+1] - d[h]) * kk);
                    pixel[id[h+1]][id[w]].setColor(o[h * oldW + w], (id[w+1] - d[w]) * (d[h+1] - id[h+1]) * kk);
                } else if (id[w+1] > id[w]) {
                    pixel[id[h]][id[w]].setColor(o[h * oldW + w], (id[w+1] - d[w]) / k * 100);
                    pixel[id[h]][id[w+1]].setColor(o[h * oldW + w], 100 -  (id[w+1] - d[w]) / k * 100);
                } else if (id[h+1] > id[h]) {
                    pixel[id[h]][id[w]].setColor(o[h * oldW + w], (id[h+1] - d[h]) / k * 100);
                    pixel[id[h+1]][id[w]].setColor(o[h * oldW + w], 100 - (id[h+1] - d[h]) / k * 100);
                } else {
                    pixel[id[h]][id[w]].setColor(o[h * oldW + w], 100);
                }
            }
        int[] pix = new int[H * W];
        for (int h = 0; h < H; h++)
            for (int w = 0; w < W; w++)
                pix[h * W + w] = pixel[h][w].getColor();
        return pix;
    }

    private int[] rotate(int[] pixels) {
        int[] newPixels = new int[W * H];
        for (int h = 0; h < H; h++)
            for (int w = 0; w < W; w++)
                newPixels[H * (w + 1) - 1 - h] = pixels[h * W + w];
        return newPixels;
    }

    private void lightenUp(int[] pixels) {
        for (int h = 0; h < H; h++)
            for (int w = 0; w < W; w++) {
                int a = Color.alpha(pixels[h * W + w]);
                int r = Color.red(pixels[h * W + w]);
                int g = Color.green(pixels[h * W + w]);
                int b = Color.blue(pixels[h * W + w]);
                r = (r + 255) / 2;
                g = (g + 255) / 2;
                b = (b + 255) / 2;
                pixels[h * W + w] = Color.argb(a, r, g, b);
            }
    }

    private class Pixel {
        private ArrayList<Integer> colors = new ArrayList<Integer>();
        private ArrayList<Double> per = new ArrayList<Double>();

        public void setColor(int c, double p) {
            colors.add(c);
            per.add(p);
        }

        public int getColor() {
            int s = 0;
            int a = 0;
            int r = 0;
            int g = 0;
            int b = 0;
            for (int i = 0; i < per.size(); i++)
                s += per.get(i);
            for (int i = 0; i < per.size(); i++)
                per.set(i, per.get(i) / s);
            for (int i = 0; i < colors.size(); i++) {
                int da = Color.alpha(colors.get(i));
                int dr = Color.red(colors.get(i));
                int dg = Color.green(colors.get(i));
                int db = Color.blue(colors.get(i));
                da *= per.get(i);
                dr *= per.get(i);
                dg *= per.get(i);
                db *= per.get(i);
                a += da;
                r += dr;
                g += dg;
                b += db;
            }
            return Color.argb(a, r, g, b);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        original = BitmapFactory.decodeResource(getResources(),
                R.drawable.source);
        oldW = original.getWidth();
        oldH = original.getHeight();
        double k = (double) oldW / W;
        p.setTextSize(20);
        p.setARGB(255, 255, 255, 255);
        for (int i = 0; i < H; i++)
            m[i] = (int) (i * k);
        myView view = new myView(this);
        view.setOnClickListener(view);
        setContentView(view);
    }
}
