package com.example.cat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class MyActivity extends Activity {
    Bitmap bitmap;
    public int[] a = new int[434 * 405];
    public int[] b = new int[434 * 405];
    public int[][] bit = new int[800][800];
    public int state = 0;

    class Cat extends View {

        public Cat(Context context) {
            super(context);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    state++;
                    state %= 2;
                    invalidate();
                }
            });
        };
        @Override
        public void onDraw(Canvas canvas) {
            if (state == 0) {
               canvas.drawBitmap(b, 0, 434, 0, 0, 434, 405, true, null);
            } else {
                canvas.drawBitmap(a, 0, 434, 0, 0, 434, 405, true, null);
           }
            invalidate();
        }
    }
    public double f(int z, double x, double y) {
        if (z == 0) return (x * y * (x - 1) * (x - 2) * (y - 1) * (y - 2)) / 36;
        if (z == 1) return -(x * (x - 1) * (x - 2) * (y - 1) * (y - 2) * (y + 1)) / 12;
        if (z == 2) return x * y * (x - 1) * (x - 2) * (y + 1) * (y - 2) / 12;
        if (z == 3) return -(x * y * (x - 1) * (x - 2) * (y - 1) * (y + 1)) / 36;
        if (z == 4) return -(y * (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y - 2)) / 12;
        if (z == 5) return (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y - 2) * (y + 1) / 4;
        if (z == 6) return -(y * (x - 1) * (x - 2) * (x + 1) * (y + 1) * (y - 2)) / 4;
        if (z == 7) return y * (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y + 1) / 12;
        if (z == 8) return x * y * (x + 1) * (x - 2) * (y - 1) * (y - 2) / 12;
        if (z == 9) return -(x * (x + 1) * (x - 2) * (y - 1) * (y - 2) * (y + 1)) / 4;
        if (z == 10) return x * y * (x + 1) * (x - 2) * (y + 1) * (y - 2) / 4;
        if (z == 11) return -(x * y * (x + 1) * (x - 2) * (y - 1) * (y + 1)) / 12;
        if (z == 12) return -(x * y * (x - 1) * (x + 1) * (y - 1) * (y - 2)) / 36;
        if (z == 13) return x * (x - 1) * (x + 1) * (y - 1) * (y - 2) * (y + 1) / 12;
        if (z == 14) return -(x * y * (x - 1) * (x + 1) * (y + 1) * (y - 2)) / 12;
        return x * y * (x - 1) * (x + 1) * (y - 1) * (y + 1) / 36;


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
        for (int i = 0; i < 700; i++)
            for (int j = 0; j < 750; j++)
                bit[750 - j][i] = bitmap.getPixel(i, j);

        for (int j = 0; j < 434; j++)
            for (int i = 0; i < 405; i++) {
                b[j + i * 434] = bit[(int) (j * 750.0 / 434)][(int) (i * 700.0 / 405)];
                b[j + i * 434] = Color.argb(Math.min((Color.alpha(b[j + i * 434])) * 2, 255), Math.min((Color.red(b[j + i * 434])) * 2, 255), Math.min((Color.green(b[j + i * 434])) * 2, 255), Math.min((Color.blue(b[j + i * 434])) * 2, 255));
            }



        for (int j = 0; j < 434; j++)
            for (int i = 0; i < 405; i++) {
                int q = (int) (j * 750.0 / 434);
                int w = (int) (i * 700.0 / 405);
                double e = (j * 750.0 / 434) - q;
                double r = (int) (i * 700.0 / 405) - w;
                int count = 0;
                double ans_a = 0, ans_r = 0, ans_g = 0, ans_b = 0;
                for (int k = q - 1; k <= q + 2; k++)
                    for (int p = w - 1; p <= w + 2; p++, count++)
                        if (k >= 0 && k < 750 && p >= 0 && p < 700) {
                            ans_a += f(count, e, r) * Color.alpha(bit[k][p]);
                            ans_r += f(count, e, r) * Color.red(bit[k][p]);
                            ans_g += f(count, e, r) * Color.green(bit[k][p]);
                            ans_b += f(count, e, r) * Color.blue(bit[k][p]);
                        }

                a[j + i * 434] = Color.argb(Math.min((int) ans_a * 2, 255), Math.min((int) ans_r * 2, 255), Math.min((int) ans_g * 2, 255), Math.min((int) ans_b * 2, 255));

            }
        super.onCreate(savedInstanceState);
        setContentView(new Cat(this));
    }
}
