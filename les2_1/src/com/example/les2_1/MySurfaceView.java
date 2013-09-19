package com.example.les2_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 17.09.13
 * Time: 13:38
 * To change this template use File | Settings | File Templates.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    public static int state = 0;

    public MySurfaceView(Context context) {
        super(context);
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state++;
                state %= 2;
                invalidate();
            }
        });
        getHolder().addCallback(this);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getHolder(), getResources());
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}
