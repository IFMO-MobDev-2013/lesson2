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
        private double ratio[][][];
        private int tableImage [][];

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

            initRatio();
            tableImage = new int [WIDTH][HEIGHT];
            int k = 0;

            for(int i = 0; i < HEIGHT; i++)
                for(int j = 0; j < WIDTH; j++) {
                    tableImage[j][i] = oldImage[k];
                    k++;
                }

        }

        public void initRatio() {
            ratio = new double[NEW_WIDTH + 1][NEW_HEIGHT + 1][16];
            for(int i = 0; i < NEW_WIDTH; i++)
                for(int j = 0; j < NEW_HEIGHT; j++){
                    double x = i * FACTOR - (int) (i * FACTOR);
                    double y = j * FACTOR - (int) (j * FACTOR);
                        ratio[i][j][0] = (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y - 2) * (y + 1) / 4;
                        ratio[i][j][1] = -x * (x + 1) * (x - 2) * (y - 1) * (y - 2) * (y + 1) / 4;
                        ratio[i][j][2] = -y * (x - 1) * (x - 2) * (x + 1) * (y + 1) * (y - 2) / 4;
                        ratio[i][j][3] =  x * y * (x + 1) * (x - 2) * (y + 1) * (y - 2) / 4;
                        ratio[i][j][4] = -x * (x - 1) * (x - 2) * (y - 1) * (y - 2) * (y + 1) / 12;
                        ratio[i][j][5] = -y * (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y - 2) / 12;
                        ratio[i][j][6] =  x * y * (x - 1) * (x - 2) * (y + 1) * (y - 2) / 12;
                        ratio[i][j][7] =  x * y * (x + 1) * (x - 2) * (y - 1) * (y - 2) / 12;
                        ratio[i][j][8] =  x * (x - 1) * (x + 1) * (y - 1) * (y - 2) * (y + 1) / 12;
                        ratio[i][j][9] =  y * (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y + 1) / 12;
                        ratio[i][j][10] =  x * y * (x - 1) * (x - 2) * (y - 1) * (y - 2) / 36;
                        ratio[i][j][11] = -x * y * (x - 1) * (x + 1) * (y + 1) * (y - 2) / 12;
                        ratio[i][j][12] = -x * y * (x + 1) * (x - 2) * (y - 1) * (y + 1) / 12;
                        ratio[i][j][13] = -x * y * (x - 1) * (x + 1) * (y - 1) * (y - 2) / 36;
                        ratio[i][j][14] = -x * y * (x - 1) * (x - 2) * (y - 1) * (y + 1) / 36;
                        ratio[i][j][15] =  x * y * (x - 1) * (x + 1) * (y - 1) * (y + 1) / 36;
                }
        }

        public int setBright(int color) {
            int alpha;
            int red;
            int green;
            int blue;
            alpha = Color.alpha(color) + 100;
            red = Color.red(color) + 100;
            green = Color.green(color) + 100;
            blue = Color.blue(color) + 100;

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
            Bitmap newBitmap = Bitmap.createBitmap(NEW_HEIGHT, NEW_WIDTH, Bitmap.Config.RGB_565);
            newBitmap.setPixels(fastImage, 0, NEW_HEIGHT, 0, 0, NEW_HEIGHT, NEW_WIDTH);
            time = SystemClock.currentThreadTimeMillis() - time;
            return newBitmap;
        }

        public Bitmap createGoodImage() {
            time = SystemClock.currentThreadTimeMillis();
            int[] goodImage = new int[NEW_HEIGHT * NEW_WIDTH];
            int[][] table = new int[NEW_WIDTH + 1][NEW_HEIGHT + 1];

            for(int i = 0; i < NEW_WIDTH; i++) {
                for(int j =0 ;j < NEW_HEIGHT; j++){
                    int x = (int) (i * FACTOR);
                    int y = (int) (j * FACTOR);
                    double alpha = 0;
                    double red = 0;
                    double green = 0;
                    double blue = 0;
                    int a = 0;
                    int b = 0;

                    for (int k = 0; k < 16; k++) {

                        if (k == 0 || k == 1 || k == 4 || k == 8) a = 0;
                        if (k == 2 || k == 3 || k == 6 || k == 11) a = 1;
                        if (k == 5 || k == 7 || k == 10 || k == 13) a = -1;
                        if (k == 9 || k == 12 || k == 14 || k == 15) a = 2;

                        if (k == 0 || k == 2 || k == 5 || k == 9) b = 0;
                        if (k == 1 || k == 3 || k == 7 || k == 12) b = 1;
                        if (k == 4 || k == 6 || k == 10 || k == 14) b = -1;
                        if (k == 8 || k == 11 || k == 13 || k == 15) b = 2;

                        if (x + a >= 0 && x + a < WIDTH && y + b >= 0 && y + b < HEIGHT) {
                            red += ratio[i][j][k] * Color.red(tableImage[x + a][y + b]);
                            green += ratio[i][j][k] * Color.green(tableImage[x + a][y + b]);
                            blue += ratio[i][j][k] * Color.blue(tableImage[x + a][y + b]);
                            alpha += ratio[i][j][k] * Color.alpha(tableImage[x + a][y + b]);
                        }
                    }

                    table[i][j] = setBright(Color.argb((int) alpha, (int) red, (int) green, (int)blue));
                }
            }
            int k = 0;
            for(int i = 0; i < NEW_WIDTH; i++){
                for(int j = NEW_HEIGHT; j > 0; j--){
                    goodImage[k] = table[i][j];
                    k++;
                }
            }

            Bitmap newBitmap = Bitmap.createBitmap(NEW_HEIGHT, NEW_WIDTH, Bitmap.Config.RGB_565);
            newBitmap.setPixels(goodImage, 0, NEW_HEIGHT, 0, 0, NEW_HEIGHT, NEW_WIDTH);
            time = SystemClock.currentThreadTimeMillis() - time;
            return newBitmap;
        }


        @Override
        public void onDraw(Canvas canvas) {
            if (optimization) {
                canvas.drawBitmap(createFastImage(), 0, 0, null);
                canvas.drawText("Fast compression", 10, D_HEIGHT - 80, paint2);
            }else {
                canvas.drawBitmap(createGoodImage(), 0, 0, null);
                canvas.drawText("High-quality compression", 10, D_HEIGHT - 80, paint2);
            }
            canvas.drawText(" Time = " + time, D_WIDTH - 300, D_HEIGHT - 30, paint);
        }
    }
}