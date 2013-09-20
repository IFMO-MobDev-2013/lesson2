package ru.ifmo.ctddev.skripnikov.androidhw2;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;

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
        int maxSize = 16;
        int[][][] colors = new int[H][W][maxSize];
        float[][][] per = new float[H][W][maxSize];
        int[][] size = new int[H][W];
        float k = (float) W / oldW;
        float kk = 100 / k / k;
        float[] d = new float[oldH + 1];
        for (int i = 0; i <= oldH; i++)
            d[i] = i * k;
        int[] id = new int[oldH + 1];
        for (int i = 0; i <= oldH; i++)
            id[i] = (int) d[i];
        int[] o = new int[oldW * oldH];
        original.getPixels(o, 0, oldW, 0, 0, oldW, oldH);
        for (int h = 0; h < oldH; h++)
            for (int w = 0; w < oldW; w++) {
                if (id[w + 1] >= W || id[h + 1] >= H) {
                    continue;
                }
                if (id[w + 1] > id[w] && id[h + 1] > id[h]) {
                    colors[id[h]][id[w]][size[id[h]][id[w]]] = o[h * oldW + w];
                    colors[id[h + 1]][id[w + 1]][size[id[h + 1]][id[w + 1]]] = o[h * oldW + w];
                    colors[id[h]][id[w + 1]][size[id[h]][id[w + 1]]] = o[h * oldW + w];
                    colors[id[h + 1]][id[w]][size[id[h + 1]][id[w]]] = o[h * oldW + w];
                    per[id[h]][id[w]][size[id[h]][id[w]]++] = (id[w + 1] - d[w]) * (id[h + 1] - d[h]) * kk;
                    per[id[h + 1]][id[w + 1]][size[id[h + 1]][id[w + 1]]++] = (d[w + 1] - id[w + 1]) * (d[h + 1] - id[h + 1]) * kk;
                    per[id[h]][id[w + 1]][size[id[h]][id[w + 1]]++] = (id[w + 1] - id[w + 1]) * (id[h + 1] - d[h]) * kk;
                    per[id[h + 1]][id[w]][size[id[h + 1]][id[w]]++] = (id[w + 1] - d[w]) * (d[h + 1] - id[h + 1]) * kk;
                } else if (id[w + 1] > id[w]) {
                    colors[id[h]][id[w]][size[id[h]][id[w]]] = o[h * oldW + w];
                    colors[id[h]][id[w + 1]][size[id[h]][id[w + 1]]] = o[h * oldW + w];
                    per[id[h]][id[w]][size[id[h]][id[w]]++] = (id[w + 1] - d[w]) / k * 100;
                    per[id[h]][id[w + 1]][size[id[h]][id[w + 1]]++] = 100 - (id[w + 1] - d[w]) / k * 100;
                } else if (id[h + 1] > id[h]) {
                    colors[id[h]][id[w]][size[id[h]][id[w]]] = o[h * oldW + w];
                    colors[id[h + 1]][id[w]][size[id[h + 1]][id[w]]] = o[h * oldW + w];
                    per[id[h]][id[w]][size[id[h]][id[w]]++] = (id[h + 1] - d[h]) / k * 100;
                    per[id[h + 1]][id[w]][size[id[h + 1]][id[w]]++] = 100 - (id[h + 1] - d[h]) / k * 100;
                } else {
                    colors[id[h]][id[w]][size[id[h]][id[w]]] = o[h * oldW + w];
                    per[id[h]][id[w]][size[id[h]][id[w]]++] = (float) 100;
                }
            }
        int[] pix = new int[H * W];
        for (int h = 0; h < H; h++)
            for (int w = 0; w < W; w++) {
                int s = 0, a = 0, r = 0, g = 0, b = 0;
                for (int i = 0; i < size[h][w]; i++)
                    s += per[h][w][i];
                for (int i = 0; i < size[h][w]; i++) {
                    float m = per[h][w][i] / s;
                    a += Color.alpha(colors[h][w][i]) * m;
                    r += Color.red(colors[h][w][i]) * m;
                    g += Color.green(colors[h][w][i]) * m;
                    b += Color.blue(colors[h][w][i]) * m;
                }
                pix[h * W + w] = Color.argb(a, r, g, b);
            }
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
