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
import android.view.MotionEvent;

public class MainActivity extends Activity {

    public class MainView extends View {
        Bitmap originalPic;
        public boolean good_quality = false;
        int[] compressedBitmap;
        public static final int OLD_WIDTH = 700;
        public static final int OLD_HEIGHT = 750;
        public static final int NEW_WIDTH = 405;
        public static final int NEW_HEIGHT = 434;
        Paint paint;

        public MainView(Context context) {
            super(context);

            originalPic = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            paint = new Paint();
            compressedBitmap = new int[NEW_WIDTH * NEW_HEIGHT];
        }

        public void fastCompression() {
            int[] tmp = new int[NEW_HEIGHT * NEW_WIDTH];
            for (int i = 0; i < NEW_HEIGHT; ++i)
                for (int j = 0; j < NEW_WIDTH; ++j) {
                    tmp[NEW_WIDTH * i + j] =
                            originalPic.getPixel((int) (j * ((double) OLD_WIDTH / (double) NEW_WIDTH)),
                                    (int) (i * ((double) OLD_HEIGHT / (double) NEW_HEIGHT)));
                }
            compressedBitmap = tmp;
            good_quality = false;
        }

        public void turn_right() {
            int[] tmp = new int[NEW_HEIGHT * NEW_WIDTH];
            for (int i = 0; i < NEW_HEIGHT; ++i)
                for (int j = 0; j < NEW_WIDTH; ++j) {
                    tmp[j * NEW_HEIGHT + NEW_HEIGHT - i - 1] = compressedBitmap[i * NEW_WIDTH + j];
                }
            compressedBitmap = tmp;
        }

        public void goodQualityComression() {
            int[] cnt = new int[NEW_HEIGHT * NEW_WIDTH];
            int[][] tmp = new int[NEW_HEIGHT * NEW_WIDTH][4];
            for (int i = 0; i < OLD_HEIGHT; i++) {
                for (int j = 0; j < OLD_WIDTH; j++) {
                    int x = (int) (j * ((double) NEW_WIDTH / (double) OLD_WIDTH));
                    int y = (int) (i * ((double) NEW_HEIGHT / (double) OLD_HEIGHT));
                    int color = originalPic.getPixel(j, i);
                    tmp[y * NEW_WIDTH + x][0] += color & 0xFF;
                    tmp[y * NEW_WIDTH + x][1] += (color >> 8) & 0xFF;
                    tmp[y * NEW_WIDTH + x][2] += (color >> 16) & 0xFF;
                    tmp[y * NEW_WIDTH + x][3] += (color >> 24) & 0xFF;
                    cnt[y * NEW_WIDTH + x]++;
                }
            }
            for (int i = 0; i < NEW_HEIGHT; ++i) {
                for (int j = 0; j < NEW_WIDTH; ++j) {
                        for (int k = 0; k < 4; ++k)
                            tmp[i * NEW_WIDTH + j][k] /= Math.max(cnt[i * NEW_WIDTH + j], 1);
                    compressedBitmap[i * NEW_WIDTH + j] = Color.argb(tmp[i * NEW_WIDTH + j][3], tmp[i * NEW_WIDTH + j][2],
                            tmp[i * NEW_WIDTH + j][1], tmp[i * NEW_WIDTH + j][0]);
                }
            }
            good_quality = true;
        }

        public void makeBrighter() {
            for (int i = 0; i < NEW_HEIGHT; ++i) {
                for (int j = 0; j < NEW_WIDTH; ++j) {
                    int color = compressedBitmap[j + i * NEW_WIDTH];
                    int blue = Math.min(255, 2 * (color & 0xFF));
                    int green = Math.min(255, 2 * ((color >> 8) & 0xFF));
                    int red = Math.min(255, 2 * ((color >> 16) & 0xFF));
                    int alpha = Math.min(255, 2 * ((color >> 24) & 0xFF));
                    compressedBitmap[NEW_WIDTH * i + j] = Color.argb(alpha, red, green, blue);

                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            invalidate();
            return super.onTouchEvent(event);
        }

        public void onDraw(Canvas canvas) {
            if (good_quality) {
                fastCompression();
                paint.setColor(Color.BLACK);
                paint.setTextSize(20);
                canvas.drawText("GOOD QUALITY", 20, 500, paint);
            } else {
                goodQualityComression();
                paint.setColor(Color.BLACK);
                paint.setTextSize(20);
                canvas.drawText("BAD QUALITY", 20, 500, paint);
            }
            makeBrighter();
            turn_right();
            canvas.drawBitmap(compressedBitmap, 0, NEW_HEIGHT, 0, 0, NEW_HEIGHT, NEW_WIDTH, true, paint);


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