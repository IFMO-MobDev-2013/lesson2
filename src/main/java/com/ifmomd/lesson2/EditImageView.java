package com.ifmomd.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import java.io.IOException;

/**
 * Created by vladimirskipor on 9/16/13.
 */
public class EditImageView extends View {


    public static final String SOURCE_FILE_NAME = "source.png";
    public static final double BRIGHTNESS_CHANGE = 1.8;
    Context context;
    Bitmap sourceBitmap;

    static final class Image {
        public static final int maskRed = 0x00FF0000;
        public static final int maskGreen = 0x0000FF00;
        public static final int maskBlue = 0x000000FF;
        public static final int maskPositive = 0x7FFFFFFF; // overflow guard
        int[] pixels;
        int width;
        int height;

        Image(Bitmap bitmap) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            pixels = new int[height * width];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        }

        public Bitmap getBitmap() {
            return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        }

        public void rotateRight() {
            int[] newPixels = new int[pixels.length];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    newPixels[j * height + i] = pixels[i * width + j];
                }
            }

            int newHeight = width;
            width = height;
            height = newHeight;
            pixels = newPixels;

        }

        public void changeBrightness(double x) {
            int newColor;
            int tmpColor;
            for (int i = 0; i < pixels.length; i++) {
                newColor = 0xFF000000 ;

                tmpColor = (int)((pixels[i] & maskRed) * x);
                newColor |= (tmpColor & maskPositive) > maskRed ? maskRed : tmpColor & maskRed;

                tmpColor = (int)((pixels[i] & maskGreen) * x);
                newColor |= (tmpColor & maskPositive) > maskGreen ? maskGreen : tmpColor & maskGreen;

                tmpColor = (int)((pixels[i] & maskBlue) * x);
                newColor |= (tmpColor & maskPositive) > maskBlue ? maskBlue : tmpColor & maskBlue;

                pixels[i] = newColor;

            }
        }

        public void squeezeWidth(){

        }

    }



    public Bitmap loadImageBitmap(String sourceFileName) throws IOException {
        return BitmapFactory.decodeStream(context.getAssets().open(sourceFileName));
    }



    public EditImageView(Context context) throws IOException {
        super(context);
        this.context = context;
        sourceBitmap = loadImageBitmap(SOURCE_FILE_NAME);
    }





    @Override
    protected void onDraw(Canvas canvas) {
        Image image = new Image(sourceBitmap);
        image.changeBrightness(BRIGHTNESS_CHANGE);
        image.rotateRight();


        canvas.drawBitmap(image.getBitmap(), 0, 0, null);

//        canvas.drawBitmap(sourceBitmap, 0, 0, null);

    }
}
