package com.ifmomd.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;

/**
 * Created by vladimirskipor on 9/16/13.
 */
public class EditImageView extends View {


    public static final String SOURCE_FILE_NAME = "source.png";
    public static final double BRIGHTNESS_CHANGE = 1.8;
    public static final double SQUEEZE_COEFFICIENT = 1.83;
    private Context context;
    private Bitmap sourceBitmap;
    private Image image;
    private ImageUpdater imageUpdater;


    public Bitmap loadImageBitmap(String sourceFileName) throws IOException {
        return BitmapFactory.decodeStream(context.getAssets().open(sourceFileName));
    }


    public EditImageView(Context context) throws IOException {
        super(context);
        this.context = context;
        sourceBitmap = loadImageBitmap(SOURCE_FILE_NAME);
        imageUpdater = new ImageUpdater(SQUEEZE_COEFFICIENT, BRIGHTNESS_CHANGE, 1);

    }

    class ImageUpdater implements Runnable {
        private boolean useFast;
        private final double squeezeCoefficient;
        private final double brightnessChange;
        private final int rotateTimes;


        ImageUpdater(double squeezeCoefficient, double brightnessChange, int rotateTimes) {
            this.squeezeCoefficient = squeezeCoefficient;
            this.brightnessChange = brightnessChange;
            this.rotateTimes = rotateTimes;
            useFast = true;

        }

        @Override
        public void run() {
            image = new Image(sourceBitmap);
            image.changeBrightness(brightnessChange);
            for (int i = 0; i < rotateTimes % 4; i++) {
                image.rotateLeft();
            }
            if (useFast) {
                image.fastSqueeze(squeezeCoefficient);
            } else {
                image.squeeze(squeezeCoefficient);
            }
            useFast ^= true;



        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        AsyncTask.execute(imageUpdater);



        canvas.drawBitmap(image.getBitmap(), 0, 0, null);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            invalidate();
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }
}
