package com.example.les2_1;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.SurfaceHolder;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 17.09.13
 * Time: 13:39
 * To change this template use File | Settings | File Templates.
 */
public class DrawThread extends Thread {
    private boolean runFlag = false;
    private SurfaceHolder surfaceHolder;

    private long lastFpsCalcUptime;
    private long frameCounter;
    private static final long FPS_CALC_INTERVAL = 1000L;
    private long fps;

    public DrawThread(SurfaceHolder surfaceHolder, Resources resources){
        this.surfaceHolder = surfaceHolder;

    }

    public void setRunning(boolean run) {
        runFlag = run;
    }

    @Override
    public void run() {
        Canvas canvas;
        while (runFlag) {
            measureFps();

            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);

                //canvas.scale((float)MyActivity.screen_w/w,(float)MyActivity.screen_h/h);
                synchronized (surfaceHolder) {
                    canvas.drawColor(Color.BLACK);
                    //canvas.drawBitmap(MyActivity.fast,0,0,null);
                    if (MySurfaceView.state == 0)
                        canvas.drawBitmap(MyActivity.fast,0,MyActivity.end_w,0,0,MyActivity.end_w,MyActivity.end_h,false,null);
                    else
                        canvas.drawBitmap(MyActivity.amazing,0,MyActivity.end_w,0,0,MyActivity.end_w,MyActivity.end_h,false,null);
                }
            }
            finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void measureFps() {
        frameCounter++;
        long now = SystemClock.uptimeMillis();
        long delta = now - lastFpsCalcUptime;
        if (delta > FPS_CALC_INTERVAL) {
            fps = frameCounter * FPS_CALC_INTERVAL / delta;
            frameCounter = 0;
            lastFpsCalcUptime = now;
        }
    }
}
