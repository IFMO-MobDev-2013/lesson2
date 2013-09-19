package com.ifmomd.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import java.io.IOException;
import java.util.Arrays;

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
        public static final int maxAlpha = 0xFF000000;
        public static final int noAlphaMask = 0x00FFFFFF;
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

        public void rotateLeft() {
            int[] newPixels = new int[pixels.length];
            for (int i = 0, y = 0; i < height; i++, y += width) {
                for (int j = 0, k = 0; j < width; j++, k += height) {
                    newPixels[k + height - (i + 1)] = pixels[y + j];
                }
            }

            int newHeight = width;
            width = height;
            height = newHeight;
            pixels = newPixels;

        }

        public void changeBrightness(double x) {
            if (x < 1.) {
                throw new IllegalArgumentException("x must be >= 0.");
            }
            int newColor;
            int tmpColor;
            for (int i = 0; i < pixels.length; i++) {
                newColor = 0xFF000000;

                tmpColor = (int) ((pixels[i] & maskRed) * x);
                newColor |= (tmpColor & maskPositive) > maskRed ? maskRed : tmpColor & maskRed;

                tmpColor = (int) ((pixels[i] & maskGreen) * x);
                newColor |= (tmpColor & maskPositive) > maskGreen ? maskGreen : tmpColor & maskGreen;

                tmpColor = (int) ((pixels[i] & maskBlue) * x);
                newColor |= (tmpColor & maskPositive) > maskBlue ? maskBlue : tmpColor & maskBlue;

                pixels[i] = newColor;

            }
        }

        public void fastSqueezeWidth(double k) {
            if (k < 1.) {
                throw new IllegalArgumentException("k must be >= 1.");
            }
            int newWidth = (int) (width / k);
            int[] newPixels = new int[height * newWidth];
            int[] remains = new int[newWidth];
            for (int i = 0; i < remains.length; i++) {
                remains[i] = (int) (k * i);
            }
            for (int i = 0, y = 0, p = 0; i < height; i++, y += width, p += newWidth) {
                for (int j = 0; j < newWidth; j++) {
                    newPixels[p + j] = pixels[y + remains[j]];
                }
            }

            width = newWidth;
            pixels = newPixels;


        }


        public void fastSqueezeHeight(double k) {
            if (k < 1.) {
                throw new IllegalArgumentException("k must be >= 1.");
            }
            int newHeight = (int) (height / k);
            int[] newPixels = new int[newHeight * width];
            int[] remains = new int[newHeight];
            for (int i = 0; i < remains.length; i++) {
                remains[i] = (int) (k * i);
            }

            for (int i = 0, p = 0; i < newHeight; i++, p += width) {
                final int y = remains[i] * width;
                for (int j = 0; j < width; j++) {
                    newPixels[p + j] = pixels[y + j];
                }

            }
            height = newHeight;
            pixels = newPixels;

        }

        public void fastSqueeze(double k) {
            fastSqueezeHeight(k);
            fastSqueezeWidth(k);
        }


        public void squeezeWidth(double k) { // middle color of overlapped pixels
            if (k < 1.) {
                throw new IllegalArgumentException("k must be >= 1.");
            }
            int newWidth = (int) (width / k);
            int[] newPixels = new int[height * newWidth];
            int[] remains = new int[newWidth];
            int[] temporaryRed = new int[newWidth];
            int[] temporaryGreen = new int[newWidth];
            int[] temporaryBlue = new int[newWidth];
            int[] cellSizes = new int[newWidth];


            for (int i = 0; i < remains.length; i++) {
                remains[i] = (int) (k * i);
            }
            for (int i = 0; i < newWidth; i++) {
                cellSizes[i] = ((i == newWidth - 1 ? width : remains[i + 1]) - remains[i]);
            }




            for (int i = 0, y = 0, p = 0; i < height; i++, y += width, p += newWidth) {

                for (int j = 0; j < newWidth; j++) {

                    for (int l = remains[j], r = l + cellSizes[j]; l < r; l++) {
                        temporaryRed[j] += (pixels[y + l] & maskRed);
                        temporaryGreen[j] += (pixels[y + l] & maskGreen);
                        temporaryBlue[j] += (pixels[y + l] & maskBlue);
                    }
                }

                for (int j = 0; j < newWidth; j++) {        //do same operations in group
//


                    newPixels[p + j] = ((temporaryRed[j] / cellSizes[j]) & maskRed
                            | (temporaryGreen[j] / cellSizes[j]) & maskGreen
                            | temporaryBlue[j] / cellSizes[j]) // maskBlue don't needed
                            | maxAlpha;
                }
                Arrays.fill(temporaryRed, 0);       //fill is quicker than relocate
                Arrays.fill(temporaryGreen, 0);
                Arrays.fill(temporaryBlue, 0);

//                Arrays.fill(temporaryColor, 0);

            }

            width = newWidth;
            pixels = newPixels;
        }

        public void squeezeHeight(double k) {  // middle color of overlapped pixels
            if (k < 1.) {
                throw new IllegalArgumentException("k must be >= 1.");
            }
            int newHeight = (int) (height / k);
            int[] newPixels = new int[newHeight * width];
            int[] remains = new int[newHeight];
            for (int i = 0; i < remains.length; i++) {
                remains[i] = (int) (k * i);
            }

            int[] cellSizes = new int[newHeight];
            for (int i = 0; i < newHeight; i++) {
                cellSizes[i] = ((i == newHeight - 1 ? height : remains[i + 1]) - remains[i]);

            }


            int[] temporaryRed = new int[width];
            int[] temporaryGreen = new int[width];
            int[] temporaryBlue = new int[width];

            for (int i = 0, p = 0, y = 0; i < newHeight; i++, p += width) {

                for (int l = remains[i], r = l + cellSizes[i]; l < r; l++, y += width) {
                    for (int j = 0; j < width; j++) {
                        temporaryRed[j] += (pixels[y + j] & maskRed);
                        temporaryGreen[j] += (pixels[y + j] & maskGreen);
                        temporaryBlue[j] += (pixels[y + j] & maskBlue);

                    }
                }

                for (int j = 0; j < width; j++) {        //do same operations in group


                    newPixels[p + j] = ((temporaryRed[j] / cellSizes[i]) & maskRed
                            | (temporaryGreen[j] / cellSizes[i]) & maskGreen
                            | temporaryBlue[j] / cellSizes[i]) // maskBlue don't needed
                            | maxAlpha;
                }
                Arrays.fill(temporaryRed, 0);       //fill is quicker than relocate
                Arrays.fill(temporaryGreen, 0);
                Arrays.fill(temporaryBlue, 0);


            }
            height = newHeight;
            pixels = newPixels;

        }

        public void squeeze(double k) {
            squeezeHeight(k);
            squeezeWidth(k);
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
        image.rotateLeft();
        image.squeeze(1.83);


        canvas.drawBitmap(image.getBitmap(), 0, 0, null);


    }
}
