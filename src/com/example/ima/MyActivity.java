package com.example.ima;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;

public class MyActivity extends Activity {

    Bitmap bitmapOld;
    Bitmap bitmapFast;
    Bitmap bitmapNice;

    public static final int WIDTH_OLD = 750;
    public static final int HEIGHT_OLD = 700;
    public static final int WIDTH_NEW = 434;
    public static final int HEIGHT_NEW = 405;

    public static final double ROOT = 1.73;
    int x, a1, a2, a3, a4;
    int t = 0;
    int y;
    int xx;
    int yy;
    boolean state = true;

    int pixelsOld[][] = new int[HEIGHT_OLD][WIDTH_OLD];
    int pixelsNew[][] = new int[HEIGHT_NEW][WIDTH_NEW];
    int pixels[] = new int[HEIGHT_OLD * WIDTH_OLD];

    Paint paint = new Paint();

    class MyView extends View {
        public MyView(Context context) {
            super(context);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    state = !state;
                    invalidate();
                }
            });
        }

        @Override
        public void onDraw(Canvas canvas) {
            if(state)
                canvas.drawBitmap(bitmapFast, 0, 0, paint);
            else
                canvas.drawBitmap(bitmapNice, 0, 0, paint);
        }
    }
    void rotate() {
        for(int j = WIDTH_OLD - 1; j >= 0; j--)
            for(int i = 0; i < HEIGHT_OLD; i++)
                  pixelsOld[i][j] = pixels[t++];
    }

    void makeBrighter() {
        for(int i = 0; i < HEIGHT_OLD; i++)
            for(int j = 0; j < WIDTH_OLD; j++)
                pixelsOld[i][j] = Color.argb(Color.alpha(pixelsOld[i][j]), Math.min(255, Color.red(pixelsOld[i][j]) * 2),
                        Math.min(255, Color.green(pixelsOld[i][j]) * 2), Math.min(255, Color.blue(pixelsOld[i][j]) * 2));
    }

    void makeFastBitmap() {
        for(int i = 0; i < HEIGHT_NEW; i++)
            for(int j = 0; j < WIDTH_NEW; j++)
            {
                x = (int)(i * ROOT);
                y = (int)(j * ROOT);
                if(x + 1 < HEIGHT_OLD)
                    xx = x + 1;
                else
                    xx = x;
                if(y + 1 < WIDTH_OLD)
                    yy = y + 1;
                else
                    yy = y;
                pixelsNew[i][j] = Color.argb((Color.alpha(pixelsOld[x][y]) + Color.alpha(pixelsOld[xx][y]) + Color.alpha(pixelsOld[x][yy]) + Color.alpha(pixelsOld[xx][yy]) ) / 4,
                        (Color.red(pixelsOld[x][y]) + Color.red(pixelsOld[xx][y]) + Color.red(pixelsOld[x][yy]) + Color.red(pixelsOld[xx][yy]) ) / 4,
                        (Color.green(pixelsOld[x][y]) + Color.green(pixelsOld[xx][y]) + Color.green(pixelsOld[x][yy]) + Color.green(pixelsOld[xx][yy]) ) / 4,
                        (Color.blue(pixelsOld[x][y]) + Color.blue(pixelsOld[xx][y]) + Color.blue(pixelsOld[x][yy]) + Color.blue(pixelsOld[xx][yy]) ) / 4);
            }
    }

    void makeNiceBitmap() {
        for(int i = 0; i < HEIGHT_NEW; i++)
            for(int j = 0; j < WIDTH_NEW; j++)
            {
                a1 = 0;
                a2 = 0;
                a3 = 0;
                a4 = 0;
                t = 0;
                for(x = Math.max(0, (int)(i * ROOT) - 2); x < Math.min(HEIGHT_OLD, (int)(i * ROOT) + 2); x++)
                    for(y = Math.max(0, (int)(j * ROOT) - 2); y < Math.min(WIDTH_OLD, (int)(j * ROOT) + 2); y++)
                    {
                        a1 += Color.alpha(pixelsOld[x][y]);
                        a2 += Color.red(pixelsOld[x][y]);
                        a3 += Color.green(pixelsOld[x][y]);
                        a4 += Color.blue(pixelsOld[x][y]);
                        t++;
                    }
                pixelsNew[i][j] = Color.argb(a1 / t,  a2 / t, a3 / t, a4 / t);
            }
    }

    Bitmap getBitmap() {
        for(int i = 0; i < HEIGHT_NEW; i++)
            for(int j = 0; j < WIDTH_NEW; j++)
                pixels[j + i * WIDTH_NEW] = pixelsNew[i][j];
        return Bitmap.createBitmap(pixels, 0 , WIDTH_NEW, WIDTH_NEW, HEIGHT_NEW, Bitmap.Config.ARGB_8888);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bitmapOld = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.source);
        bitmapOld.getPixels(pixels, 0, HEIGHT_OLD, 0, 0, HEIGHT_OLD, WIDTH_OLD);
        rotate();
        makeBrighter();
        makeFastBitmap();
        bitmapFast = getBitmap();
        makeNiceBitmap();
        bitmapNice = getBitmap();
        setContentView(new MyView(this));
    }
}
