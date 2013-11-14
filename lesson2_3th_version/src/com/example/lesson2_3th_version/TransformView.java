package com.example.lesson2_3th_version;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;


/**
 * Created with IntelliJ IDEA.
 * User: slavian
 * Date: 22.09.13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class TransformView extends View implements View.OnClickListener {
    Bitmap old;
    Bitmap fastBitmap;
    Bitmap goodBitmap;
    int[] img;
    int[] newImg;
    boolean fast = true;
    int height, width, newWidth, newHeight;
    public static final double K = 1.73;
    Paint p = new Paint();
    float s = 1;


    public TransformView(Context contex, int displayHeight, int displayWidth) {
        super(contex);
        initialize();
        makeTwoBms();

        if (newWidth > displayWidth || newHeight > displayHeight) {
            if (newWidth > displayWidth)
                s = ((float) displayWidth) / newWidth;
            else
                s = ((float) displayHeight) / newHeight;
        }

        this.setOnClickListener(this);
    }

    private void initialize() {
        old = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        width = old.getWidth();
        height = old.getHeight();
        newWidth = (int) ((double) width / K);
        newHeight = (int) ((double) height / K);
        img = new int[width * height];
        newImg = new int[newWidth * newHeight];
        old.getPixels(img, 0, width, 0, 0, width, height);
        p.setARGB(255, 255, 255, 255);
    }

    private void brightness() {
        int r, g, b;
        for (int t = 0; t < width * height; ++t) {

            r = img[t] & 0x00FF0000;
            g = img[t] & 0x0000FF00;
            b = img[t] & 0x000000FF;

            r = r >> 16;
            g = g >> 8;

            r = (r * 1.5 <= 255) ? (int) (r * 1.5) : 255;
            g = (g * 1.5 <= 255) ? (int) (g * 1.5) : 255;
            b = (b * 1.5 <= 255) ? (int) (b * 1.5) : 255;

            img[t] = 0xFF000000 | r << 16 | g << 8 | b;
        }
    }

    private void swapHW() {
        int buffer = height;
        height = width;
        width = buffer;

        buffer = newHeight;
        newHeight = newWidth;
        newWidth = buffer;
    }

    private void rotation() {
        int[] t = new int[height * width];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                t[j * height + height - i - 1] = img[i * width + j];
            }
        }

        img = t;
        swapHW();
        // height and width are swapped now!!
    }

    private Bitmap fastScale() {
        int[][] t1 = new int[height][width];
        int[][] t2 = new int[newHeight][newWidth];

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                t1[i][j] = img[i * width + j];
            }
        }

        for (int i = 0; i < newHeight; ++i) {
            for (int j = 0; j < newWidth; ++j) {
                t2[i][j] = t1[(int) (i * K)][(int) (j * K)];
            }
        }

        for (int i = 0; i < newHeight; ++i) {
            for (int j = 0; j < newWidth; ++j) {
                newImg[i * newWidth + j] = t2[i][j];
            }
        }

        //the code below does not work. I have no idea why.
        //the code above is three times slower and cache-unfriendly, but it works.
        // life is life.

//        for(int i=0; i<newHeight; ++i)
//        {
//            for(int j=0; j<newWidth; ++j)
//            {
//                newImg[i*newWidth + j] = img[(int)((double)(newWidth*i+j)*K)];
//            }
//        }

        return Bitmap.createBitmap(newImg, newWidth, newHeight, Bitmap.Config.RGB_565);
    }

    private Bitmap goodScale() {
        int[] temp = new int[newWidth * newHeight];
        int a, b, c, d, x, y;

        float dx, dy;
        float rd, gr, bl;
        int t = 0;
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                x = (int) (K * j);
                y = (int) (K * i);
                dx = (float) (K * j) - x;
                dy = (float) (K * i) - y;
                int pos = y * width + x;
//                |__|__|__|__|
//                |__|a_|b_|__|
//                |__|c_|d_|__|
//                |__|__|__|__|
                a = img[pos];
                b = img[pos + 1];
                c = img[pos + width];
                d = img[pos + width + 1];

                rd = ((a >> 16) & 255) * (1 - dx) * (1 - dy) + ((b >> 16) & 255) * dx * (1 - dy) + ((c >> 16) & 255) * dy * (1 - dx) + ((d >> 16) & 255) * (dx * dy);
                gr = ((a >> 8) & 255) * (1 - dx) * (1 - dy) + ((b >> 8) & 255) * dx * (1 - dy) + ((c >> 8) & 255) * dy * (1 - dx) + ((d >> 8) & 255) * (dx * dy);
                bl = (a & 255) * (1 - dx) * (1 - dy) + (b & 255) * dx * (1 - dy) + (c & 255) * dy * (1 - dx) + (d & 255) * (dx * dy);


                temp[t] = 0xff000000 | ((((int) rd) & 255) << 16) | ((((int) gr) & 255) << 8) | (((int) bl) & 255);
                t++;
            }
        }
        return Bitmap.createBitmap(temp, newWidth, newHeight, Bitmap.Config.RGB_565);
    }

    private void makeTwoBms() {
        rotation();
        brightness();

        fastBitmap = fastScale();
        goodBitmap = goodScale();
    }


    @Override
    public void onDraw(Canvas canvas) {
        canvas.scale(s, s);   // only fo displaying correctly
        if (fast) {
            canvas.drawBitmap(fastBitmap, 5, 5, null);
        } else {
            canvas.drawBitmap(goodBitmap, 5, 5, null);
        }
        fast = !fast;
    }

    @Override
    public void onClick(View v) {
        v.invalidate();
    }

}
