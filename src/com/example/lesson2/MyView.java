package com.example.lesson2;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View
{
    static class Simple
    {
        int width;
        int height;
        int[] bytes;
        Bitmap orig;

        public Simple(Bitmap orig)
        {
            int w = orig.getWidth();
            int h = orig.getHeight();
            int[] arr = new int[w * h];
            this.orig = orig;
            orig.getPixels(arr, 0, w, 0, 0, w, h);
            init(arr, w, h);
        }

        public void slowScale(int w, int h)
        {
            int[] temp = resizeBilinear(bytes, width, height, w, h);
            init(temp, w, h);
        }

        public int[] resizeBilinear(int[] pixels, int w, int h, int w2, int h2)
        {
            int[] temp = new int[w2 * h2];
            int a, b, c, d, x, y, index;
            float x_ratio = ((float)(w-1)) / w2;
            float y_ratio = ((float)(h-1)) / h2;
            float x_diff, y_diff, blue, red, green;
            int offset = 0;
            for (int i = 0; i < h2; i++)
            {
                for (int j = 0; j < w2; j++)
                {
                    x = (int)(x_ratio * j) ;
                    y = (int)(y_ratio * i) ;
                    x_diff = (x_ratio * j) - x ;
                    y_diff = (y_ratio * i) - y ;
                    index = (y*w+x) ;
                    a = pixels[index] ;
                    b = pixels[index + 1] ;
                    c = pixels[index + w] ;
                    d = pixels[index + w + 1] ;

                    // blue element
                    // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                    blue = (a&0xff)*(1-x_diff)*(1-y_diff) + (b&0xff)*(x_diff)*(1-y_diff) +
                            (c&0xff)*(y_diff)*(1-x_diff)   + (d&0xff)*(x_diff*y_diff);

                    // green element
                    // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                    green = ((a>>8)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>8)&0xff)*(x_diff)*(1-y_diff) +
                            ((c>>8)&0xff)*(y_diff)*(1-x_diff)   + ((d>>8)&0xff)*(x_diff*y_diff);

                    // red element
                    // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                    red = ((a>>16)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>16)&0xff)*(x_diff)*(1-y_diff) +
                            ((c>>16)&0xff)*(y_diff)*(1-x_diff)   + ((d>>16)&0xff)*(x_diff*y_diff);

                    temp[offset++] = 0xff000000 |
                                    ((((int)red)<<16)&0xff0000) |
                                    ((((int)green)<<8)&0xff00) |
                                    ((int)blue) ;
                }
            }
            return temp ;
        }

        public void fastScale(int w, int h)
        {
            int[] temp = new int[w * h];
            int xscale = (width << 16) / w;
            int yscale = (height << 16) / h;
            int x = 0;
            int y = 0;
            int length = temp.length;
            int i = 0;
            while (i < length)
            {
                if (x == w)
                {
                    y++;
                    x = 0;
                }
                temp[i] = bytes[((yscale * y) >> 16) * width + ((xscale * x) >> 16)];
                x++;
                i++;
            }
            init(temp, w, h);
        }

        public void brightness(float k)
        {
            int length =  bytes.length;
            int i = 0;
            while (i < length)
            {
                bytes[i] = brightness(bytes[i], k);
                ++i;
            }
        }

        public int brightness(int color, float k)
        {
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

        public void init(int[] bytes, int width, int height)
        {
            this.bytes = bytes;
            this.width = width;
            this.height = height;
        }

        public void rotate90()
        {
            int[] temp = new int[bytes.length];
            int x = 0;
            int y = 0;
            for (int i = 0; i < bytes.length; i++)
            {
                if (x == width)
                {
                    y++;
                    x = 0;
                }
                temp[x * height + (height - y - 1)] = bytes[i];
                x++;
            }
            init(temp, height, width);
        }

        public Bitmap toBitmap()
        {
             return Bitmap.createBitmap(bytes, width, height, Bitmap.Config.RGB_565);
        }
    }
    private Bitmap orig;
    private Bitmap imp;
    private boolean v = true;
    private Paint p;
    private long d = 0;
    public MyView(Context context, Bitmap orig)
    {
        super(context);
        this.orig = orig;
        p = new Paint();
        p.setColor(Color.WHITE);
        update();
    }

    public void update()
    {
        Simple s = new Simple(orig);
        long start = SystemClock.currentThreadTimeMillis();
        if (v)
        {
            s.slowScale(405, 434);
        }
        else
        {
            s.fastScale(405, 434);
        }
        s.rotate90();
        s.brightness(2);
        imp = s.toBitmap();
        long end = SystemClock.currentThreadTimeMillis();
        d = end - start;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawBitmap(imp, 0, 0, null);
        p.setColor(Color.RED);
        canvas.scale(3, 3);
        if (v)
        {
            canvas.drawText("Slow: "+d+" ms", 150, 30, p);
        }
        else
        {
            canvas.drawText("Fast: "+d+" ms", 150, 30, p);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Log.i("Tag", "Touch");
        v = !v;
        update();
        return super.onTouchEvent(event);
    }
}
