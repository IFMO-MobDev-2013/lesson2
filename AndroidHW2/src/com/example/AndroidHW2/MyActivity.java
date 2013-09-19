package com.example.AndroidHW2;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.View;
import static android.graphics.Bitmap.createScaledBitmap;

import java.util.Random;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    class WhirlView extends View {
        private int[] tmp = new int[525000];
        private int[] last = new int[185365];
        private final int WIDHT = 700;
        private final int HEIGHT = 750;
        private int[][] table = new int[WIDHT + 1][HEIGHT + 1];
        private int mode = 0;
        private final int NEW_WIDHT = 404;
        private final int NEW_HEIGHT = 433;
        private double[][][] sup_table = new double[NEW_WIDHT][NEW_HEIGHT][16];
        private int[][] tmp1 = new int[NEW_WIDHT + 1][NEW_HEIGHT + 1];
        private final double K = 1.728110599078341013824884792626;
        private Bitmap map = Bitmap.createBitmap(WIDHT, HEIGHT, Bitmap.Config.ARGB_8888);
        private Bitmap small_map = Bitmap.createBitmap(NEW_HEIGHT, NEW_WIDHT, Bitmap.Config.ARGB_8888);
        private double sup = 0;
        private double f = 0;
        private Paint paint = new Paint();

        public WhirlView(Context context){
            super(context);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mode = (mode + 1) % 3;
                    invalidate();
                }
            });
            map = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            map.getPixels(tmp, 0, WIDHT, 0, 0, WIDHT, HEIGHT);
            int l = -1;
            for(int i = 0; i < HEIGHT; i++)
                for(int j = 0; j < WIDHT; j++){
                    l++;
                    table[j][i] = tmp[l];
                }
            l = 0;
            for(int i = 0; i < NEW_WIDHT; i++)
                for(int j = 0; j < NEW_HEIGHT; j++){
                    double x = i * K - (int) (i * K);
                    double y = j * K - (int) (j * K);
                    sup_table[i][j][0] = (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y - 2) * (y + 1) / 4;
                    sup_table[i][j][1] = -x * (x + 1) * (x - 2) * (y - 1) * (y - 2) * (y + 1) / 4;
                    sup_table[i][j][2] = -y * (x - 1) * (x - 2) * (x + 1) * (y + 1) * (y - 2) / 4;
                    sup_table[i][j][3] =  x * y * (x + 1) * (x - 2) * (y + 1) * (y - 2) / 4;
                    sup_table[i][j][4] = -x * (x - 1) * (x - 2) * (y - 1) * (y - 2) * (y + 1) / 12;
                    sup_table[i][j][5] = -y * (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y - 2) / 12;
                    sup_table[i][j][6] =  x * y * (x - 1) * (x - 2) * (y + 1) * (y - 2) / 12;
                    sup_table[i][j][7] =  x * y * (x + 1) * (x - 2) * (y - 1) * (y - 2) / 12;
                    sup_table[i][j][8] =  x * (x - 1) * (x + 1) * (y - 1) * (y - 2) * (y + 1) / 12;
                    sup_table[i][j][9] =  y * (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y + 1) / 12;
                    sup_table[i][j][10] =  x * y * (x - 1) * (x - 2) * (y - 1) * (y - 2) / 36;
                    sup_table[i][j][11] = -x * y * (x - 1) * (x + 1) * (y + 1) * (y - 2) / 12;
                    sup_table[i][j][12] = -x * y * (x + 1) * (x - 2) * (y - 1) * (y + 1) / 12;
                    sup_table[i][j][13] = -x * y * (x - 1) * (x + 1) * (y - 1) * (y - 2) / 36;
                    sup_table[i][j][14] = -x * y * (x - 1) * (x - 2) * (y - 1) * (y + 1) / 36;
                    sup_table[i][j][15] =  x * y * (x - 1) * (x + 1) * (y - 1) * (y + 1) / 36;


                    l++;
                }
            paint.setTextSize(30);
            paint.setColor(Color.WHITE);
        }
                    ;
        private long badResize(){
            long start = SystemClock.currentThreadTimeMillis();
            int l = 0;
            int sup1;
            int sup2;
            int sup3;
            int sup4;

            for(int i = 0; i <= NEW_WIDHT; i++){
                for(int j = NEW_HEIGHT; j > 0; j--){
                    last[l] = tmp[(int) ((int)(j * K) * WIDHT + i * K)];
                    sup1 =(int) (Color.alpha(last[l]) * K);
                    sup2 =(int) (Color.red(last[l]) * K);
                    sup3 =(int) (Color.green(last[l]) * K);
                    sup4 =(int) (Color.blue(last[l]) * K);
                    sup4 = sup4>255?255:sup4;
                    sup3 = sup3>255?255:sup3;
                    sup2 = sup2>255?255:sup2;
                    sup1 = sup1>255?255:sup1;
                    last[l] = Color.argb(sup1, sup2, sup3, sup4);
                    l++;
                    //last[l] = tmp[j * WIDHT + i];
                }
            }

            return SystemClock.currentThreadTimeMillis() - start;
        }

        private long goodResize(){
            long start = SystemClock.currentThreadTimeMillis();//524704
            int l = -1;
            int sup1;
            int sup2;
            int sup3;
            int sup4;
            for(int i = 0; i <= NEW_WIDHT; i++){
                for(int j = NEW_HEIGHT; j > 0; j--){
                    l++;
                    double x_first = i * K;
                    double y_first = j * K;
                    //double x_second = i * 1.73;
                    //double y_first = j * 1.73;
                    sup = 0;
                    f = 0;
                    for(double k = x_first; k <= x_first + K ; k = k + K / 3){
                        for(double r = y_first; r <= y_first + K ; r = r + K / 3){
                            f = f + Math.sqrt(Math.pow(x_first + K / 2 - k, 2) + Math.pow(y_first + K / 2 - r, 2));//(Math.abs(x_first + K / 2 - k) + Math.abs(y_first + K / 2 - r));
                            sup = sup + tmp[(int) ((int)(j * K) * WIDHT + i * K)]* Math.sqrt(Math.pow(x_first + K / 2 - k, 2) + Math.pow(y_first + K / 2 - r, 2));
                        }
                    }
                    last[l] = (int) (sup / f);
                    sup1 =(int) (Color.alpha(last[l]) * K);
                    sup2 =(int) (Color.red(last[l]) * K);
                    sup3 =(int) (Color.green(last[l]) * K);
                    sup4 =(int) (Color.blue(last[l]) * K);
                    sup4 = sup4>255?255:sup4;
                    sup3 = sup3>255?255:sup3;
                    sup2 = sup2>255?255:sup2;
                    sup1 = sup1>255?255:sup1;
                    last[l] = Color.argb(sup1, sup2, sup3, sup4);
                }
            }
            return SystemClock.currentThreadTimeMillis() - start;
        }

        private long goodResize1(){
            long start = SystemClock.currentThreadTimeMillis();

            double sup1 = 0;
            double sup2 = 0;
            double sup3 = 0;
            double sup4 = 0;
            int l = 0;
            for(int i = 0; i < NEW_WIDHT; i++)
                for(int j =0 ;j < NEW_HEIGHT; j++){
                    sup2 = 0;
                    sup1 = 0;
                    sup3 = 0;
                    sup4 = 0;
                    int q = (int) (i * K);
                    int w = (int) (j * K);
                    if(q >= 0 && q < 700 && w >= 0 && w < 750){
                        sup1 += sup_table[i][j][0] * Color.red(table[q][w]);
                        sup2 += sup_table[i][j][0] * Color.green(table[q][w]);
                        sup3 += sup_table[i][j][0] * Color.blue(table[q][w]);
                        sup4 += sup_table[i][j][0] * Color.alpha(table[q][w]);
                    }

                    if(q >= 0 && q < 700 && w + 1 >= 0 && w + 1 < 750){
                        sup1 += sup_table[i][j][1] * Color.red(table[q][w + 1]);
                        sup2 += sup_table[i][j][1] * Color.green(table[q][w + 1]);
                        sup3 += sup_table[i][j][1] * Color.blue(table[q][w + 1]);
                        sup4 += sup_table[i][j][1] * Color.alpha(table[q][w + 1]);
                    }

                    if(q + 1 >= 0 && q + 1 < 700 && w >= 0 && w < 750){
                        sup1 += sup_table[i][j][2] * Color.red(table[q + 1][w]);
                        sup2 += sup_table[i][j][2] * Color.green(table[q + 1][w]);
                        sup3 += sup_table[i][j][2] * Color.blue(table[q + 1][w]);
                        sup4 += sup_table[i][j][2] * Color.alpha(table[q + 1][w]);
                    }

                    if(q + 1 >= 0 && q + 1 < 700 && w + 1 >= 0 && w + 1 < 750){
                        sup1 += sup_table[i][j][3] * Color.red(table[q + 1][w + 1]);
                        sup2 += sup_table[i][j][3] * Color.green(table[q + 1][w + 1]);
                        sup3 += sup_table[i][j][3] * Color.blue(table[q + 1][w + 1]);
                        sup4 += sup_table[i][j][3] * Color.alpha(table[q + 1][w + 1]);
                    }

                    if(q >= 0 && q < 700 && w - 1 >= 0 && w - 1 < 750){
                        sup1 += sup_table[i][j][4] * Color.red(table[q][w - 1]);
                        sup2 += sup_table[i][j][4] * Color.green(table[q][w - 1]);
                        sup3 += sup_table[i][j][4] * Color.blue(table[q][w - 1]);
                        sup4 += sup_table[i][j][4] * Color.alpha(table[q][w - 1]);
                    }

                    if(q - 1 >= 0 && q - 1 < 700 && w >= 0 && w < 750){
                        sup1 += sup_table[i][j][5] * Color.red(table[q - 1][w]);
                        sup2 += sup_table[i][j][5] * Color.green(table[q - 1][w]);
                        sup3 += sup_table[i][j][5] * Color.blue(table[q - 1][w]);
                        sup4 += sup_table[i][j][5] * Color.alpha(table[q - 1][w]);
                    }

                    if(q + 1 >= 0 && q + 1 < 700 && w - 1 >= 0 && w - 1 < 750){
                        sup1 += sup_table[i][j][6] * Color.red(table[q + 1][w - 1]);
                        sup2 += sup_table[i][j][6] * Color.green(table[q + 1][w - 1]);
                        sup3 += sup_table[i][j][6] * Color.blue(table[q + 1][w - 1]);
                        sup4 += sup_table[i][j][6] * Color.alpha(table[q + 1][w -1]);
                    }

                    if(q - 1 >= 0 && q - 1< 700 && w + 1 >= 0 && w + 1 < 750){
                        sup1 += sup_table[i][j][7] * Color.red(table[q - 1][w + 1]);
                        sup2 += sup_table[i][j][7] * Color.green(table[q - 1][w + 1]);
                        sup3 += sup_table[i][j][7] * Color.blue(table[q - 1][w + 1]);
                        sup4 += sup_table[i][j][7] * Color.alpha(table[q - 1][w + 1]);
                    }

                    if(q >= 0 && q < 700 && w + 2 >= 0 && w + 2 < 750){
                        sup1 += sup_table[i][j][8] * Color.red(table[q][w + 2]);
                        sup2 += sup_table[i][j][8] * Color.green(table[q][w + 2]);
                        sup3 += sup_table[i][j][8] * Color.blue(table[q][w + 2]);
                        sup4 += sup_table[i][j][8] * Color.alpha(table[q][w + 2]);
                    }

                    if(q + 2 >= 0 && q + 2< 700 && w >= 0 && w < 750){
                        sup1 += sup_table[i][j][9] * Color.red(table[q +2][w]);
                        sup2 += sup_table[i][j][9] * Color.green(table[q + 2][w]);
                        sup3 += sup_table[i][j][9] * Color.blue(table[q + 2][w]);
                        sup4 += sup_table[i][j][9] * Color.alpha(table[q + 2][w]);
                    }

                    if(q - 1 >= 0 && q - 1 < 700 && w - 1 >= 0 && w - 1 < 750){
                        sup1 += sup_table[i][j][10] * Color.red(table[q - 1 ][w - 1]);
                        sup2 += sup_table[i][j][10] * Color.green(table[q - 1][w - 1]);
                        sup3 += sup_table[i][j][10] * Color.blue(table[q - 1][w - 1]);
                        sup4 += sup_table[i][j][10] * Color.alpha(table[q - 1][w - 1]);
                    }

                    if(q + 1 >= 0 && q + 1 < 700 && w + 2 >= 0 && w + 2 < 750){
                        sup1 += sup_table[i][j][11] * Color.red(table[q + 1][w + 2]);
                        sup2 += sup_table[i][j][11] * Color.green(table[q + 1][w + 2]);
                        sup3 += sup_table[i][j][11] * Color.blue(table[q + 1][w + 2]);
                        sup4 += sup_table[i][j][11] * Color.alpha(table[q + 1][w + 2]);
                    }

                    if(q + 2 >= 0 && q + 2 < 700 && w + 1 >= 0 && w + 1 < 750){
                        sup1 += sup_table[i][j][12] * Color.red(table[q + 2][w + 1]);
                        sup2 += sup_table[i][j][12] * Color.green(table[q + 2][w + 1]);
                        sup3 += sup_table[i][j][12] * Color.blue(table[q + 2][w + 1]);
                        sup4 += sup_table[i][j][12] * Color.alpha(table[q + 2][w + 1]);
                    }

                    if(q - 1 >= 0 && q - 1 < 700 && w + 2 >= 0 && w + 2 < 750) {
                        sup1 += sup_table[i][j][13] * Color.red(table[q - 1][w + 2]);
                        sup2 += sup_table[i][j][13] * Color.green(table[q - 1][w + 2]);
                        sup3 += sup_table[i][j][13] * Color.blue(table[q - 1][w + 2]);
                        sup4 += sup_table[i][j][13] * Color.alpha(table[q - 1][w + 2]);
                    }

                    if(q + 2 >= 0 && q + 2 < 700 && w - 1 >= 0 && w - 1 < 750){
                        sup1 += sup_table[i][j][14] * Color.red(table[q + 2][w - 1]);
                        sup2 += sup_table[i][j][14] * Color.green(table[q + 2][w - 1]);
                        sup3 += sup_table[i][j][14] * Color.blue(table[q + 2][w - 1]);
                        sup4 += sup_table[i][j][14] * Color.alpha(table[q + 2][w - 1]);
                    }

                    if(q + 2 >= 0 && q + 2 < 700 && w + 2 >= 0 && w + 2 < 750){
                        sup1 += sup_table[i][j][15] * Color.red(table[q + 2][w + 2]);
                        sup2 += sup_table[i][j][15] * Color.green(table[q + 2][w + 2]);
                        sup3 += sup_table[i][j][15] * Color.blue(table[q + 2][w + 2]);
                        sup4 += sup_table[i][j][15] * Color.alpha(table[q + 2][w + 2]);
                    }
                    sup1 = sup1 * K;
                    sup2 = sup2 * K;
                    sup3 = sup3 * K;
                    sup4 = sup4 * K;

                    sup4 = sup4>255?255:sup4;
                    sup3 = sup3>255?255:sup3;
                    sup2 = sup2>255?255:sup2;
                    sup1 = sup1>255?255:sup1;
                    tmp1[i][j] = Color.argb((int) sup4, (int) sup1, (int) sup2, (int) sup3);
                    //l++;
                }

            for(int i = 0; i < NEW_WIDHT; i++){
                for(int j = NEW_HEIGHT; j > 0; j--){
                    last[l] = tmp1[i][j];
                    l++;

                }
                //l++;
            }
            return SystemClock.currentThreadTimeMillis() - start;
        }



        @Override
        public void onDraw(Canvas canvas) {
            long time = 0;
            if(mode == 0){
                time = badResize();
                small_map.setPixels(last, 0, NEW_HEIGHT, 0, 0 , NEW_HEIGHT, NEW_WIDHT);
                canvas.drawBitmap(small_map, 0, 0, null);
            //canvas.drawBitmap(map, 0, 0, null);
            //long time = goodResize();
                canvas.drawText("Time: " + time, 300, 60, paint);
            }

            if(mode == 1){
                time = goodResize1();
                small_map.setPixels(last, 0, NEW_HEIGHT, 0, 0 , NEW_HEIGHT, NEW_WIDHT);
                canvas.drawBitmap(small_map, 0, 0, null);
                //canvas.drawBitmap(map, 0, 0, null);
                //long time = goodResize();
                canvas.drawText("Time: " + time, 300, 60, paint);
            }

            if(mode == 2){
                time = goodResize();
                small_map.setPixels(last, 0, NEW_HEIGHT, 0, 0 , NEW_HEIGHT, NEW_WIDHT);
                canvas.drawBitmap(small_map, 0, 0, null);
                //canvas.drawBitmap(map, 0, 0, null);
                //long time = goodResize();
                canvas.drawText("Time: " + time, 300, 60, paint);
            }

        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new WhirlView(this));
    }
}
