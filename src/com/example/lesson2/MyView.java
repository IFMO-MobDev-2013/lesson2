package com.example.lesson2;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {
    static class Simple {
        int width;
        int height;
        int[] bytes;
        Bitmap orig;

        public Simple(Bitmap orig) {
            int w = orig.getWidth();
            int h = orig.getHeight();
            int[] arr = new int[w * h];
            this.orig = orig;
            orig.getPixels(arr, 0, w, 0, 0, w, h);
            init(arr, w, h);
        }

        public void slowScale(int w, int h) {
            int[] temp = bilinear(bytes, width, height, w, h);
            init(temp, w, h);
        }

        public int[] bilinear(int[] arr, int w, int h, int w2, int h2) {
            int[] temp = new int[w2 * h2];
            int x;
            int y;
            float xs = ((float) (w - 1)) / w2;
            float ys = ((float) (h - 1)) / h2;
            float dx;
            float dy;
            int offset = 0;
            for (int i = 0; i < h2; ++i) {
                for (int j = 0; j < w2; ++j) {
                    x = (int) (xs * j);
                    y = (int) (ys * i);
                    dx = (xs * j) - x;
                    dy = (ys * i) - y;
                    int index = (y * w + x);
                    int a = arr[index];
                    int b = arr[index + 1];
                    int c = arr[index + w];
                    int d = arr[index + w + 1];

                    float blue = (a & 0xff) * (1 - dx) * (1 - dy) + (b & 0xff) * (dx) * (1 - dy) + (c & 0xff) * (dy) * (1 - dx) + (d & 0xff) * (dx * dy);

                    float green = ((a >> 8) & 0xff) * (1 - dx) * (1 - dy) + ((b >> 8) & 0xff) * (dx) * (1 - dy) + ((c >> 8) & 0xff) * (dy) * (1 - dx) + ((d >> 8) & 0xff) * (dx * dy);

                    float red = ((a >> 16) & 0xff) * (1 - dx) * (1 - dy) + ((b >> 16) & 0xff) * (dx) * (1 - dy) + ((c >> 16) & 0xff) * (dy) * (1 - dx) + ((d >> 16) & 0xff) * (dx * dy);

                    temp[offset++] = 0xff000000 | ((((int) red) << 16) & 0xff0000) | ((((int) green) << 8) & 0xff00) | ((int) blue);
                }
            }
            return temp;
        }

        public void fastScale(int w, int h) {
            int[] temp = new int[w * h];
            int xscale = (width << 16) / w;
            int yscale = (height << 16) / h;
            int x = 0;
            int y = 0;
            int length = temp.length;
            int i = 0;
            while (i < length) {
                if (x == w) {
                    y++;
                    x = 0;
                }
                temp[i] = bytes[((yscale * y) >> 16) * width + ((xscale * x) >> 16)];
                x++;
                i++;
            }
            init(temp, w, h);
        }

        public void brightness(float k) {
            int length = bytes.length;
            int i = 0;
            while (i < length) {
                bytes[i] = brightness(bytes[i], k);
                ++i;
            }
        }

        public int brightness(int color, float k) {
            int red = (color & 0x0000ff);
            int green = (color & 0x00ff00) >> 8;
            int blue = (color & 0xff0000) >> 16;
            red *= k;
            if (red >= 256) red = 255;
            blue *= k;
            if (blue >= 256) blue = 255;
            green *= k;
            if (green >= 256) green = 255;
            return red | (green << 8) | (blue << 16);
        }

        public void init(int[] bytes, int width, int height) {
            this.bytes = bytes;
            this.width = width;
            this.height = height;
        }

        public void rotate90() {
            int[] temp = new int[bytes.length];
            int x = 0;
            int y = 0;
            int i = 0;
            int length = bytes.length;
            while (i < length) {
                if (x == width) {
                    y++;
                    x = 0;
                }
                temp[x * height + (height - y - 1)] = bytes[i];
                x++;
                i++;
            }
            init(temp, height, width);
        }

        public Bitmap toBitmap() {
            return Bitmap.createBitmap(bytes, width, height, Bitmap.Config.RGB_565);
        }
    }

    private Bitmap orig;
    private Bitmap imp;
    private boolean v = true;
    private Paint p;
    private long d = 0;

    public MyView(Context context, Bitmap orig) {
        super(context);
        this.orig = orig;
        p = new Paint();
        p.setColor(Color.WHITE);
        update();
    }

    public void update() {
        Simple s = new Simple(orig);
        long start = SystemClock.currentThreadTimeMillis();

        int w = 405;
        int h = 434;
        float k = 2;

        if (v) {
            s.slowScale(w, h);
        } else {
            s.fastScale(w, h);
        }
        s.rotate90();
        s.brightness(k);
        imp = s.toBitmap();
        long end = SystemClock.currentThreadTimeMillis();
        d = end - start;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(imp, 0, 0, null);
        p.setColor(Color.RED);
        canvas.scale(3, 3);
        if (v) {
            canvas.drawText("Slow: " + d + " ms", 150, 30, p);
        } else {
            canvas.drawText("Fast: " + d + " ms", 150, 30, p);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        v = !v;
        update();
        return super.onTouchEvent(event);
    }
}
