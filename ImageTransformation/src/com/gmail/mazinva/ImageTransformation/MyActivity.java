package com.gmail.mazinva.ImageTransformation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;


public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    public static final int TIMES = 2;
    boolean counter;

    int[] pixels;
    int oldWidth;
    int oldHeight;

    int[] temp;
    int newWidth;
    int newHeight;

    int[] rotated;

    int[] brightened;


    class ImageTransformation extends View implements View.OnClickListener {

        public ImageTransformation(Context context) {
            super(context);
            this.setOnClickListener(this);
        }

        @Override
        public void  onDraw(Canvas canvas) {
            if (counter == true) {
                temp = resizeImageFast(pixels, oldWidth, oldHeight, newWidth, newHeight);
            } else {
                temp = resizeImageCool(pixels, oldWidth, oldHeight, newWidth, newHeight);
            }
            rotated = rotate90Degrees(temp, newWidth, newHeight);
            brightened = increaseBrightnessInTwoTimes(rotated, newHeight, newWidth);

            if (counter == true) {
                canvas.drawBitmap(brightened, 0, newHeight, 0, 0, newHeight, newWidth, false, null);
            } else {
                canvas.drawBitmap(brightened, 0, newHeight, 0, 200, newHeight, newWidth, false, null);
            }

        }

        @Override
        public void onClick(View v) {
            if (counter == true) {
                counter = false;
            } else {
                counter = true;
            }

            invalidate();
        }

        // A fast one
        public int[] resizeImageFast(int[] pixels,int w1,int h1,int w2,int h2) {
            int[] temp = new int[w2 * h2];
            for (int i = 0; i < h2; i++) {
                for (int j = 0; j < w2; j++) {
                    temp[i * w2 + j] = pixels[i * h1 / h2 * w1 + j * w1 / w2];
                }
            }
            return temp ;
        }

        // A cool one = BilinearInterpolation algorithm
        public int[] resizeImageCool(int[] pixels, int w1, int h, int w2, int h2) {
            int[] temp = new int[w2 * h2];
            int a, b, c, d, x, y, index;
            float x_ratio = ( (float) (w1 - 1)) / w2;
            float y_ratio = ( (float) (h - 1)) / h2;
            float x_diff, y_diff, blue, red, green;
            int offset = 0;
            for (int i = 0; i < h2; i++) {
                for (int j = 0; j < w2; j++) {
                    x = (int) (x_ratio * j);
                    y = (int) (y_ratio * i) ;
                    x_diff = (x_ratio * j) - x ;
                    y_diff = (y_ratio * i) - y ;
                    index = (y * w1 + x) ;
                    a = pixels[index] ;
                    b = pixels[index + 1] ;
                    c = pixels[index + w1] ;
                    d = pixels[index + w1 + 1] ;

                    // blue element
                    blue = (a & 0xff)*(1 - x_diff)*(1 - y_diff) + (b & 0xff)*(x_diff)*(1 - y_diff) +
                           (c & 0xff)*(y_diff)*(1 - x_diff) + (d & 0xff)*(x_diff * y_diff);

                    // green element
                    green = ((a >> 8) & 0xff)*(1 - x_diff)*(1 - y_diff) + ((b >> 8) & 0xff)*(x_diff)*(1 - y_diff) +
                            ((c >> 8) & 0xff)*(y_diff)*(1 - x_diff) + ((d >> 8) & 0xff)*(x_diff*y_diff);

                    // red element
                    red = ((a >> 16) & 0xff)*(1 - x_diff)*(1 - y_diff) + ((b >> 16) & 0xff)*(x_diff)*(1 - y_diff) +
                          ((c >> 16) & 0xff)*(y_diff)*(1 - x_diff) + ((d >> 16) & 0xff)*(x_diff*y_diff);

                    // result
                    temp[offset++] = 0xff000000 | ((( (int) red) << 16) & 0xff0000) |
                                    ((( (int) green) << 8) & 0xff00) | ( (int) blue);
                }
            }
            return temp ;
        }


        // Rotation
        public int[] rotate90Degrees(int[] pixels, int w, int h) {
            int[] rotated = new int[w * h];
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    rotated[j * h + ((h - 1)- i)] = pixels[i * w + j];
                }
            }
            return rotated;
        }


        // Brightness affairs
        public int[] increaseBrightnessInTwoTimes(int[] pixels, int w, int h) {
            int[] brightened = new int[w * h];
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    brightened[i * w + j] = increaseBrightnessInTwoTimes(pixels[i * w + j]);
                }
            }
            return brightened;
        }

        public int increaseBrightnessInTwoTimes(int color) {
            return rgbToHSV(color);
        }

        public int rgbToHSV(int color) {
            double r = (color & 0x00FF0000) >> 16;
            r /= 255;

            double g = (color & 0x0000FF00) >> 8;
            g /= 255;

            double b = (color & 0x000000FF);
            b /= 255;

            return rgbToHSV(r, g, b);
        }

        public int rgbToHSV(double r, double g, double b) {

            double h, s, v;

            double min = Math.min(r, Math.min(g, b));
            double max = Math.max(r, Math.max(g, b));

            v = max;

            double delta = max - min;

            if( max != 0.0 ) {
                s = delta / max;
            } else {
                s = 0;
            }

            if (max == min) {
                h = 0;
            } else if (max == r) {
                if (g >= b) {
                    h = (g - b)/delta;
                } else {
                    h = 6 + (g - b)/delta;
                }
            } else if (max == g) {
                h = 2 + (b - r)/delta;
            } else {
                h = 4 + (r - g)/delta;
            }

            h *= 60; // degrees

            // Ensure brightness in two times
            v = Math.min(TIMES * v, 1.0);

            return hsvToRGB(h, s, v);
        }

        public int hsvToRGB(double h, double s, double v) {
            double r, g, b;

            int hi = (int) (h / 60); // sector 0 to 5
            double VMin = v * (1 - s); // VMin
            double a = (v - VMin) * ((h % 60) / 60);
            double VInc = VMin + a;
            double VDec = v - a;

            switch (hi) {
                case 0:
                    r = v;
                    g = VInc;
                    b = VMin;
                    break;
                case 1:
                    r = VDec;
                    g = v;
                    b = VInc;
                    break;
                case 2:
                    r = VMin;
                    g = v;
                    b = VInc;
                    break;
                case 3:
                    r = VMin;
                    g = VDec;
                    b = v;
                    break;
                case 4:
                    r = VInc;
                    g = VMin;
                    b = v;
                    break;
                default:		// case 5:
                    r = v;
                    g = VMin;
                    b = VDec;
                    break;
            }

            return putIntoSingleInteger(r, g, b);
        }

        public int putIntoSingleInteger(double r, double g, double b) {
            int color;

            color = 0xff; // alpha-chanel
            color <<= 8;

            r = 255 * r; // red
            color += (int) r;
            color <<= 8;

            g = 255 * g; // green
            color += (int) g;
            color <<= 8;

            b = 255 * b; // blue
            color += (int) b;

            return color;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Drawable d = getResources().getDrawable(R.drawable.source);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

        oldWidth = bitmap.getWidth();
        oldHeight = bitmap.getHeight();
        pixels = new int[oldWidth * oldHeight];
        bitmap.getPixels(pixels, 0, oldWidth, 0, 0, oldWidth, oldHeight);

        newWidth = 405;
        newHeight = 434;

        counter = true;
        super.onCreate(savedInstanceState);
        setContentView(new ImageTransformation(this));

    }
}