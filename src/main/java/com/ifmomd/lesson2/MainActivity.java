package com.ifmomd.lesson2;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.WindowManager;
import android.view.View;
import android.graphics.BitmapFactory;


public class MainActivity extends Activity {

    public class MainView extends View {
        Bitmap originalPic;
        int[] compressedBitmap;
        public static final int OLD_WIDTH = 700;
        public static final int OLD_HEIGHT = 750;
        public static final int NEW_WIDTH = 405;
        public static final int NEW_HEIGHT = 434;
        Paint paint;
        public MainView(Context context){
            super(context);

            originalPic =  BitmapFactory.decodeResource(getResources(), R.drawable.source);
            paint = new Paint();
            compressedBitmap = new int[NEW_WIDTH * NEW_HEIGHT];
        }

        public void fastCompression(){
            for(int i = 0; i < NEW_HEIGHT; ++i)
                for(int j = 0; j < NEW_WIDTH; ++j){
                    compressedBitmap[NEW_WIDTH * i + j] =
                            originalPic.getPixel((int)(j * ((double)OLD_WIDTH /(double) NEW_WIDTH)),
                                    (int)(i * ((double)OLD_HEIGHT /(double) NEW_HEIGHT)));
                }
        }

        public void turn_right() {
            int[] tmp = new int[NEW_HEIGHT * NEW_WIDTH];
            for(int i = 0; i < NEW_HEIGHT; ++i)
                for(int j = 0; j < NEW_WIDTH; ++j){
                    tmp[j * NEW_WIDTH + NEW_HEIGHT - i - 1] = compressedBitmap[i * NEW_WIDTH + j];
                }
            compressedBitmap = tmp;
        }

        public void makeBrighter(){
            for(int i = 0; i < NEW_HEIGHT; ++i){
                for(int j = 0; j < NEW_WIDTH; ++j){
                    int color = compressedBitmap[j + i * NEW_WIDTH];
                    int blue = Math.min(255, 2 * (color & 0xFF));
                    int green = Math.min(255, 2 * ((color >> 8) & 0xFF));
                    int red = Math.min(255, 2 * ((color >> 16) & 0xFF));
                    int alpha = Math.min(255,2 * ((color >> 24) & 0xFF));
                    compressedBitmap[NEW_WIDTH * i + j] = Color.argb(alpha, red, green, blue);

                }
            }
        }


        public void onDraw(Canvas canvas) {
            fastCompression();
            makeBrighter();
            turn_right();
            canvas.drawBitmap(compressedBitmap, 0, NEW_WIDTH, 0, 0, NEW_WIDTH, NEW_HEIGHT, true, paint);
            canvas.drawBitmap(originalPic, 500, 0, paint);
            paint.setColor(Color.RED);
            canvas.drawLine(0, NEW_WIDTH, NEW_HEIGHT, NEW_WIDTH, paint);
            canvas.drawLine(NEW_HEIGHT, 0, NEW_HEIGHT, NEW_WIDTH, paint);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(new MainView(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
