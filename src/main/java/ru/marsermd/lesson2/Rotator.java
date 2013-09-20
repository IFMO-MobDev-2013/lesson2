package ru.marsermd.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by marsermd on 19.09.13.
 */
public class Rotator extends ImageView {
    int height = 750, width = 700;
    int newHeight = 405, newWidth = 434;

    int[] oldPic = new int[height * width];
    int[] rotatedPic = new int[height * width];
    int[] shrinkedRPic = new int[newHeight * newWidth];
    int[] goodShrinkedRPic = new int[newHeight * newWidth];

    boolean rotated = false;
    boolean nextGood = false;
    boolean shrinked = false;
    boolean goodShrinked = false;

    float SHRINK_FACTOR = 1.73f;

    public Rotator(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(new OnClickListener() {
            //@Override
            public void onClick(View v) {
                if (!rotated) {
                    ((BitmapDrawable) getDrawable()).getBitmap().getPixels(oldPic, 0, width, 0, 0, width, height);
                    rotate();
                    rotated = true;
                } else if (!nextGood) {
                    shrink();
                    shrinked = true;
                    nextGood = true;
                } else {
                    goodShrink();
                    goodShrinked = true;
                    nextGood = false;
                }
            }
        });

    }

    void rotate() {
        if (!rotated) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int id = y * width + x;
                    int newId = (width - x - 1) * height + y;
                    rotatedPic[newId] = oldPic[id];
                }
            }
            int t = height;
            height = width;
            width = t;
        }
        Bitmap tmp = Bitmap.createBitmap(rotatedPic, width, height, Bitmap.Config.RGB_565);
        setImageBitmap(tmp);
        invalidate();
    }

    void shrink() {
        if (!shrinked) {
            int r, g, b;
            for (int x = 0; x < width; x += 1) {
                for (int y = 0; y < height; y += 1) {
                    int id = (int) (y * width + x);
                    int newId = (int) (y / SHRINK_FACTOR) * newWidth + (int) (x / SHRINK_FACTOR);
                    b = (rotatedPic[id]) & 0xFF;
                    g = (rotatedPic[id] >> 8) & 0xFF;
                    r = (rotatedPic[id] >> 16) & 0xFF;
                    r = (r * 2) > 255 ? 255 : (r * 2);
                    g = (g * 2) > 255 ? 255 : (g * 2);
                    b = (b * 2) > 255 ? 255 : (b * 2);

                    rotatedPic[id] = Color.argb(255, r, g, b);
                    shrinkedRPic[newId] = rotatedPic[id];
                }
            }
        }
        Bitmap tmp = Bitmap.createBitmap(shrinkedRPic, newWidth, newHeight, Bitmap.Config.RGB_565);
        setImageBitmap(tmp);
        invalidate();
    }

    void goodShrink() {
        if (!goodShrinked) {
            int h, w;
            float t;
            float u;
            float d1, d2, d3, d4;
            int p1, p2, p3, p4;

            int red, green, blue;

            for (int j = 0; j < newHeight; j++) {
                float tmp = j * SHRINK_FACTOR;
                h = (int) tmp;
                if (h < 0) {
                    h = 0;
                } else {
                    if (h >= height - 1) {
                        h = height - 2;
                    }
                }
                u = tmp - h;

                for (int i = 0; i < newWidth; i++) {

                    tmp = i * SHRINK_FACTOR;
                    w = (int) tmp;
                    if (w < 0) {
                        w = 0;
                    } else {
                        if (w >= width - 1) {
                            w = width - 2;
                        }
                    }
                    t = tmp - w;

            /* factors */
                    d1 = (1 - t) * (1 - u);
                    d2 = t * (1 - u);
                    d3 = t * u;
                    d4 = (1 - t) * u;

            /* nearby pixels: a[i][j] */
                    p1 = rotatedPic[h * width + w];
                    p2 = rotatedPic[h * width + w + 1];
                    p3 = rotatedPic[(h + 1) * width + w + 1];
                    p4 = rotatedPic[(h + 1) * width + w];

            /* coponents based on bilinear interpolate */
                    blue = (int) ((p1 & 0xFF) * d1 + (p2 & 0xFF) * d2 + (p3 & 0xFF) * d3 + (p4 & 0xFF) * d4);
                    green = (int) (((p1 >> 8) & 0xFF) * d1 + ((p2 >> 8) & 0xFF) * d2 + ((p3 >> 8) & 0xFF) * d3 + ((p4 >> 8) & 0xFF) * d4);
                    red = (int) (((p1 >> 16) & 0xFF) * d1 + ((p2 >> 16) & 0xFF) * d2 + ((p3 >> 16) & 0xFF) * d3 + ((p4 >> 16) & 0xFF) * d4);

            /* New pixel */
                    goodShrinkedRPic[j * newWidth + i] = Color.argb(255, red, green, blue);
                }
            }
        }
        Bitmap tmp = Bitmap.createBitmap(goodShrinkedRPic, newWidth, newHeight, Bitmap.Config.RGB_565);
        setImageBitmap(tmp);
        invalidate();
    }
}
