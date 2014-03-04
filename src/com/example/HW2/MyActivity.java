package com.example.HW2;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new MyView(this));
    }

    private class MyView extends View {
        private boolean optimization = true;
        private long time;
        private final double FACTOR = 1.73;
        private final int NEW_HEIGHT;
        private final int NEW_WIDTH;
        private final int D_HEIGHT;
        private final int D_WIDTH;
        private final int HEIGHT;
        private final int WIDTH;
        private int oldImage[];
        private final Paint paint;
        private final Paint paint2;
        private int tableImage[][];

        public MyView(Context context) {
            super(context);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            D_WIDTH = size.x;
            D_HEIGHT = size.y;

            Bitmap oldBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            HEIGHT = oldBitmap.getHeight();
            WIDTH = oldBitmap.getWidth();
            NEW_HEIGHT = (int) (HEIGHT / FACTOR);
            NEW_WIDTH = (int) (WIDTH / FACTOR);
            oldImage = new int[HEIGHT * WIDTH];
            oldBitmap.getPixels(oldImage, 0, WIDTH, 0, 0, WIDTH, HEIGHT);

            paint = new Paint();
            paint.setTextSize(50);
            paint.setColor(Color.BLUE);

            paint2 = new Paint();
            paint2.setTextSize(50);
            paint2.setColor(Color.YELLOW);


            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    optimization = !optimization;
                    invalidate();
                }
            });

            tableImage = new int[WIDTH][HEIGHT];
            int k = 0;

            for (int i = 0; i < HEIGHT; i++)
                for (int j = 0; j < WIDTH; j++) {
                    tableImage[j][i] = oldImage[k];
                    k++;
                }

        }

        public int setBright(int color) {
            int alpha;
            int red;
            int green;
            int blue;
            final int CONST = 25;
            alpha = Color.alpha(color) + CONST;
            red = Color.red(color) + CONST;
            green = Color.green(color) + CONST;
            blue = Color.blue(color) + CONST;

            if (alpha > 255) alpha = 255;
            if (red > 255) red = 255;
            if (green > 255) green = 255;
            if (blue > 255) blue = 255;
            return Color.argb(alpha, red, green, blue);
        }

        public Bitmap createFastImage() {
            time = SystemClock.currentThreadTimeMillis();
            int[] fastImage = new int[NEW_HEIGHT * NEW_WIDTH];
            int index = 0;

            for (int y = 0; y < NEW_WIDTH; y++) {
                for (int x = NEW_HEIGHT - 1; x >= 0; x--) {
                    fastImage[index] = setBright(oldImage[(int) ((int) (x * FACTOR) * WIDTH + y * FACTOR)]);
                    index++;
                }
            }
            Bitmap newBitmap = Bitmap.createBitmap(NEW_HEIGHT, NEW_WIDTH, Bitmap.Config.ARGB_8888);
            newBitmap.setPixels(fastImage, 0, NEW_HEIGHT, 0, 0, NEW_HEIGHT, NEW_WIDTH);
            time = SystemClock.currentThreadTimeMillis() - time;
            return newBitmap;
        }

        public Bitmap createGoodImage() {
            time = SystemClock.currentThreadTimeMillis();
            int[] goodImage = new int[NEW_HEIGHT * NEW_WIDTH];
            int[][] table = new int[NEW_WIDTH + 1][NEW_HEIGHT + 1];
            int alpha, red, green, blue;
            int t;
            int x, y;

            for (int i = 0; i < NEW_WIDTH; i++)
                for (int j = 0; j < NEW_HEIGHT; j++) {
                    alpha = 0;
                    red = 0;
                    green = 0;
                    blue = 0;
                    t = 0;
                    for (x = Math.max(0, (int) (i * FACTOR) - 2); x < Math.min(WIDTH, (int) (i * FACTOR) + 2); x++)
                        for (y = Math.max(0, (int) (j * FACTOR) - 2); y < Math.min(HEIGHT, (int) (j * FACTOR) + 2); y++) {
                            alpha += Color.alpha(tableImage[x][y]);
                            red += Color.red(tableImage[x][y]);
                            green += Color.green(tableImage[x][y]);
                            blue += Color.blue(tableImage[x][y]);
                            t++;
                        }
                    table[i][j] = Color.argb(Math.min(alpha / t + 25, 255), Math.min(red / t + 25, 255),
                            Math.min(green / t + 25, 255), Math.min(blue / t + 25, 255));
                }

            int k = 0;
            for (int i = 0; i < NEW_WIDTH; i++) {
                for (int j = NEW_HEIGHT; j > 0; j--) {
                    goodImage[k] = table[i][j];
                    k++;
                }
            }

            Bitmap newBitmap = Bitmap.createBitmap(NEW_HEIGHT, NEW_WIDTH, Bitmap.Config.ARGB_8888);
            newBitmap.setPixels(goodImage, 0, NEW_HEIGHT, 0, 0, NEW_HEIGHT, NEW_WIDTH);
            time = SystemClock.currentThreadTimeMillis() - time;
            return newBitmap;
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (optimization) {
                canvas.drawBitmap(createFastImage(), 0, 0, null);
                canvas.drawText("Fast compression", 10, D_HEIGHT - 80, paint2);
            } else {
                canvas.drawBitmap(createGoodImage(), 0, 0, null);
                canvas.drawText("High-quality compression", 10, D_HEIGHT - 80, paint2);
            }
            canvas.drawText(" Time = " + time, D_WIDTH - 300, D_HEIGHT - 30, paint);
        }
    }
}