package com.Korolyov.Picture;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;


public class MyActivity extends Activity {

    Bitmap imageSource, imageFastScaled, imageQualScaled;
    public int widthSource;
    public int heightSource;
    Paint p = new Paint();
    PictureView pv;
    int state = 0;


    class PictureView extends View {

        public int widthDist = 405;
        public int heightDist = 434;
        int pixelsSource[] = new int[imageSource.getHeight() * imageSource.getWidth()];
        int pixelsDist[] = new int[widthDist * heightDist];
        double scaleRatio = (double) imageSource.getWidth() / (double) widthDist;

        public PictureView(Context context) {
            super(context);
            init();
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    state++;
                    state %= 2;
                    invalidate();
                }
            });
        }


        private Bitmap fastScaleMethod(Bitmap source) {
            for (int i = 0; i < widthDist; i++)
                for (int j = 0; j < heightDist; j++) {
                    int y = (int) (scaleRatio * j);
                    int x = (int) (scaleRatio * i);
                    pixelsDist[i * heightDist + j] = source.getPixel(y, x);
                }
            return Bitmap.createBitmap(pixelsDist, heightDist, widthDist, Bitmap.Config.ARGB_8888);
        }

        private Bitmap qualScaleMethod(Bitmap source) {
            int c00, c01, c10, c11;
            int calpha, cred, cgreen, cblue;
            for (int i = 0; i < widthDist; i++)
                for (int j = 0; j < heightDist; j++) {
                    int y = (int) (scaleRatio * j);
                    int x = (int) (scaleRatio * i);
                    double dy = ((scaleRatio * j) - (double) y);
                    double dx = ((scaleRatio * i) - (double) x);
                    c00 = source.getPixel(y, x);
                    c01 = source.getPixel(y, x + 1);
                    c10 = source.getPixel(y + 1, x);
                    c11 = source.getPixel(y + 1, x + 1);
                    calpha = (int) ((double) Color.alpha(c00) * (1.0 - dx) * (1.0 - dy) +
                            (double) Color.alpha(c10) * (dx) * (1.0 - dy) +
                            (double) Color.alpha(c01) * (1.0 - dx) * (dy) +
                            (double) Color.alpha(c11) * (dx) * (dy));
                    cred = (int) ((double) Color.red(c00) * (1.0 - dx) * (1.0 - dy) +
                            (double) Color.red(c10) * (dx) * (1.0 - dy) +
                            (double) Color.red(c01) * (1.0 - dx) * (dy) +
                            (double) Color.red(c11) * (dx) * (dy));
                    cgreen = (int) ((double) Color.green(c00) * (1.0 - dx) * (1.0 - dy) +
                            (double) Color.green(c10) * (dx) * (1.0 - dy) +
                            (double) Color.green(c01) * (1.0 - dx) * (dy) +
                            (double) Color.green(c11) * (dx) * (dy));
                    cblue = (int) ((double) Color.blue(c00) * (1.0 - dx) * (1.0 - dy) +
                            (double) Color.blue(c10) * (dx) * (1.0 - dy) +
                            (double) Color.blue(c01) * (1.0 - dx) * (dy) +
                            (double) Color.blue(c11) * (dx) * (dy));

                    pixelsDist[i * heightDist + j] = Color.argb(calpha, cred, cgreen, cblue);
                }
            return Bitmap.createBitmap(pixelsDist, heightDist, widthDist, Bitmap.Config.ARGB_8888);

        }


        private Bitmap rotate(Bitmap source) {
            for (int i = 0; i < source.getHeight(); i++)
                for (int j = 0; j < source.getWidth(); j++)
                    pixelsSource[(j) * source.getHeight() + (source.getHeight() - i - 1)] = source.getPixel(j, i);
            return Bitmap.createBitmap(pixelsSource, source.getHeight(), source.getWidth(), Bitmap.Config.ARGB_8888);
        }

        private Bitmap increaseBrightness(Bitmap source) {
            for (int i = 0; i < source.getHeight(); i++)
                for (int j = 0; j < source.getWidth(); j++) {
                    int pixel = source.getPixel(j, i);
                    pixelsDist[i * source.getWidth() + j] = Color.argb(
                            Math.min(255, (int) ((double) Color.alpha(pixel) * 1.5)),
                            Math.min(255, (int) ((double) Color.red(pixel) * 1.5)),
                            Math.min(255, (int) ((double) Color.green(pixel) * 1.5)),
                            Math.min(255, (int) ((double) Color.blue(pixel) * 1.5)));
                }
            return Bitmap.createBitmap(pixelsDist, source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        private void init() {
            imageFastScaled = increaseBrightness(fastScaleMethod(rotate(imageSource)));
            imageQualScaled = increaseBrightness(qualScaleMethod(rotate(imageSource)));


        }


        @Override
        protected void onDraw(Canvas canvas) {

            if (state == 0) {
                canvas.drawBitmap(imageFastScaled, 0, 0, null);
                canvas.drawText("Fast Method", 0, 500, p);
            } else {
                canvas.drawBitmap(imageQualScaled, 0, 0, null);
                canvas.drawText("Quality Method", 0, 500, p);
            }

        }
    }

    void initPaint() {
        p.setARGB(255, 255, 255, 255);
        p.setTextSize(30);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPaint();
        imageSource = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        widthSource = imageSource.getWidth();
        heightSource = imageSource.getHeight();
        pv = new PictureView(this);

        setContentView(pv);
    }

}
