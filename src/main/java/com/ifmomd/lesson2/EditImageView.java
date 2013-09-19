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
    public static final int ROTATE_TIMES = 1;
    private Context context;
    private Bitmap sourceBitmap;
    private boolean useFast;




    public Bitmap loadImageBitmap(String sourceFileName) throws IOException {
        return BitmapFactory.decodeStream(context.getAssets().open(sourceFileName));
    }


    public EditImageView(Context context) throws IOException {
        super(context);
        this.context = context;
        sourceBitmap = loadImageBitmap(SOURCE_FILE_NAME);
        useFast = true;

    }



    @Override
    protected void onDraw(Canvas canvas) {
        Image image = new Image(sourceBitmap);
        image.changeBrightness(BRIGHTNESS_CHANGE);
        for (int i = 0; i < ROTATE_TIMES % 4; i++) {
            image.rotateLeft();
        }
        if (useFast) {
            image.fastSqueeze(SQUEEZE_COEFFICIENT);
        } else {
            image.squeeze(SQUEEZE_COEFFICIENT);
        }
        useFast ^= true;



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
