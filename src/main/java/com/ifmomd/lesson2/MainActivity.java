package com.ifmomd.lesson2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private final int FAST_TRANSFORM = 0;
    private final int HIGH_QUALITY_TRANSFORM = 1;

    private final int FINAL_WIDTH = 405;
    private final int FINAL_HEIGHT = 434;

    private int SOURCE_WIDTH;
    private int SOURCE_HEIGHT;

    private final double eps = 1e-9;


    private ImageView imageView;
    private TextView textView;

    public static int[] source_pixels;
    public int nextTranformation = FAST_TRANSFORM;


    public void onImageClick(View view) {
        int[] pixels = new int[FINAL_HEIGHT * FINAL_WIDTH];
        if (nextTranformation == FAST_TRANSFORM) {
            int i, j, color, red, blue, green;
            for (i = 0; i < FINAL_HEIGHT; i++) {
                for (j = 0; j < FINAL_WIDTH; j++) {
                    color = source_pixels[((SOURCE_HEIGHT * i) / FINAL_HEIGHT) * SOURCE_WIDTH + (SOURCE_WIDTH * j) / FINAL_WIDTH];
                    blue = (color & 0xFF);
                    green = (color & 0xFF00) / 0x100;
                    red = (color & 0xFF0000) / 0x10000;
                    pixels[j * FINAL_HEIGHT + (FINAL_HEIGHT - 1) - i] =
                                     (color & 0xFF000000) + 0x10000 * Math.min(255, red * 2) + 0x100 * Math.min(255, 2 *green) + Math.min(255, 2 * blue);
                }
            }
            imageView.setImageBitmap(Bitmap.createBitmap(pixels, FINAL_HEIGHT, FINAL_WIDTH, Bitmap.Config.ARGB_8888));
            textView.setText("Fast transformation.");
        } else if (nextTranformation == HIGH_QUALITY_TRANSFORM) {
            int i, j, color, red, blue, green, alpha;
            double[] r, g, b, a;
            r = new double[FINAL_HEIGHT * FINAL_WIDTH];
            g = new double[FINAL_HEIGHT * FINAL_WIDTH];
            b = new double[FINAL_HEIGHT * FINAL_WIDTH];
            a = new double[FINAL_HEIGHT * FINAL_WIDTH];
            for (i = 0; i < FINAL_HEIGHT; i++) {
                for (j = 0; j < FINAL_WIDTH; j++) {
                    r[i * FINAL_WIDTH + j] = 0;
                    g[i * FINAL_WIDTH + j] = 0;
                    b[i * FINAL_WIDTH + j] = 0;
                    a[i * FINAL_WIDTH + j] = 0;
                }
            }
            double width_ratio = (1.0 * FINAL_WIDTH) / SOURCE_WIDTH;
            double height_ratio = (1.0 * FINAL_HEIGHT) / SOURCE_HEIGHT;
            for (i = 0; i < SOURCE_HEIGHT; i++) {
                for (j = 0; j < SOURCE_WIDTH; j++) {
                    color = source_pixels[i * SOURCE_WIDTH + j];
                    blue = (color & 0xFF);
                    green = (color & 0xFF00) / 0x100;
                    red = (color & 0xFF0000) / 0x10000;
                    alpha = (color & 0xFF000000) / 0x1000000;
                    double left_bound = j * width_ratio - eps;
                    double right_bound = (j + 1) * width_ratio + eps;
                    double lower_bound = i * height_ratio - eps;
                    double upper_bound = (i + 1) * height_ratio + eps;
                    int left = Math.max((int) Math.floor(left_bound), 0);
                    int right = Math.min((int) Math.ceil(right_bound), FINAL_WIDTH);
                    int bottom = Math.max((int) Math.floor(lower_bound), 0);
                    int top = Math.min((int) Math.ceil(upper_bound), FINAL_HEIGHT);
                    double w, h;
                    for (int ii = bottom; ii < top; ii++) {
                        for (int jj = left; jj < right; jj++) {
                            w = Math.min(right_bound, jj + 1) - Math.max(left_bound, jj);
                            h = Math.min(upper_bound, ii + 1) - Math.max(lower_bound, ii);
                            if ((w > 0) && (h > 0)) {
                                r[ii * FINAL_WIDTH + jj] += Math.min(2 * red, 255) * w * h;
                                g[ii * FINAL_WIDTH + jj] += Math.min(2 * green, 255) * w * h;
                                b[ii * FINAL_WIDTH + jj] += Math.min(2 * blue, 255) * w * h;
                                a[ii * FINAL_WIDTH + jj] += w * h * alpha;
                            }
                        }
                    }
                }
            }
            for (i = 0; i < FINAL_HEIGHT; i++) {
                for (j = 0; j < FINAL_WIDTH; j++) {
                    pixels[j * FINAL_HEIGHT + (FINAL_HEIGHT - 1) - i] = Math.max((int)a[i * FINAL_WIDTH + j], 255) * 0x1000000
                            + Math.min((int)(r[i * FINAL_WIDTH + j]), 255) * 0x10000
                            + Math.min((int)(g[i * FINAL_WIDTH + j]), 255) * 0x100
                            + Math.min((int)(b[i * FINAL_WIDTH + j]), 255);
                }
            }
            imageView.setImageBitmap(Bitmap.createBitmap(pixels, FINAL_HEIGHT, FINAL_WIDTH, Bitmap.Config.ARGB_8888));
            textView.setText("High quality transformation.");
        }
        nextTranformation = 1 - nextTranformation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.article);
        source_pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        SOURCE_WIDTH = bitmap.getWidth();
        SOURCE_HEIGHT = bitmap.getHeight();
        bitmap.getPixels(source_pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imageView.setImageBitmap(Bitmap.createBitmap(source_pixels, bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888));
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("No transformation, width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight());
    }
}
