package com.ifmomd.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

public class MainActivity extends Activity {

    boolean goodQuality = false;
    int imageWidth = 700;
    int imageHeight = 750;
    int newWidth = 405;
    int newHeight = 434;
    Bitmap imageBitmap;
    int[] imagePixels = new int[imageWidth * imageHeight];
    int[] imageBuffer = new int[imageWidth * imageHeight];
    Paint textPaint = new Paint();


    public class MainView extends View {

        public MainView(Context context) {
            super(context);

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    goodQuality = !goodQuality;
                    updateImage();
                    invalidate();
                }
            });
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(imageBuffer, 0, newHeight, 0, 0, newHeight, newWidth, false, null);
            canvas.drawText(goodQuality ? "Good compression" : "Fast compression", 50, 50, textPaint);
        }

    }

    void updateImage() {
        imageBuffer = makeBrighter(
            rotatePicture(
                goodQuality ? goodCompress() : fastCompress(),
                newHeight,
                newWidth
            )
        );
    }

    int[] fastCompress() {
        int[] buffer = new int[newWidth * newHeight];
        for (int i = 0; i < newHeight; ++i) {
            for (int j = 0; j < newWidth; ++j) {
                buffer[newWidth * i + j] = imagePixels[(i * (imageHeight - 1) / (newHeight - 1)) * imageWidth +
                        j * (imageWidth - 1) / (newWidth - 1)];
            }
        }
        return buffer;
    }

    int[] goodCompress() {
        int[] buffer = new int[newWidth * newHeight];

        for (int i = 0; i < newHeight; i++) {
            float tmp = (float) i / (newHeight - 1) * (imageHeight - 1);

            int height = (int) Math.floor(tmp);
            if (height < 0) {
                height = 0;
            } else {
                if (height >= imageHeight - 1) {
                    height = imageHeight - 2;
                }
            }

            float u = (tmp - height);

            for (int j = 0; j < newWidth; j++) {
                tmp = (float) j / (newWidth - 1) * (imageWidth - 1);

                int width = (int) Math.floor(tmp);
                if (width < 0) {
                    width = 0;
                } else {
                    if (width >= imageWidth - 1) {
                        width = imageWidth - 2;
                    }
                }

                float t = tmp - width;

                float d1 = (1 - t) * (1 - u);
                float d2 = t * (1 - u);
                float d3 = t * u;
                float d4 = (1 - t) * u;

                int p1 = imagePixels[height * imageWidth + width];
                int p2 = imagePixels[height * imageWidth + width + 1];
                int p3 = imagePixels[(height + 1) * imageWidth + width + 1];
                int p4 = imagePixels[(height + 1) * imageWidth + width + 1];

                int red = (int) ((p1 & 0xFF0000) * d1 + (p2 & 0xFF0000) * d2 + (p3 & 0xFF0000) * d3 + (p4 & 0xFF0000) * d4) & 0xFF0000;
                int green = (int) ((p1 & 0x00FF00) * d1 + (p2 & 0x00FF00) * d2 + (p3 & 0x00FF00) * d3 + (p4 & 0x00FF00) * d4) & 0x00FF00;
                int blue = (int) ((p1 & 0x0000FF) * d1 + (p2 & 0x0000FF) * d2 + (p3 & 0x0000FF) * d3 + (p4 & 0x0000FF) * d4) & 0x0000FF;

                buffer[i * newWidth + j] = red | green | blue;
            }
        }
        return buffer;
    }

    int[] rotatePicture(int[] picture, int height, int width) {
        int[] buffer = new int[height * width];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                buffer[j * height + (height - i - 1)] = picture[i * width + j];
            }
        }
        return buffer;
    }

    int[] makeBrighter(int[] picture) {
        int red;
        int green;
        int blue;
        int[] buffer = new int[picture.length];
        for (int i = 0; i < picture.length; ++i) {
            red = (picture[i] & 0xFF0000) >> 16;
            green = (picture[i] & 0x00FF00) >> 8;
            blue = (picture[i] & 0xFF);

            red = Math.min((int) (red * 2), 255);
            green = Math.min((int) (green * 2), 255);
            blue = Math.min((int) (blue * 2), 255);

            buffer[i] = red << 16 | green << 8 | blue;
        }
        return buffer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        imageBitmap.getPixels(imagePixels, 0, imageWidth, 0, 0, imageWidth, imageHeight);
        updateImage();
        textPaint.setARGB(160, 0, 0, 0);
        textPaint.setTextSize(40);
        setContentView(new MainView(this));
    }

}
