package com.ifmomd.lesson2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

public class DrawImageThread extends Thread {

	private final SurfaceHolder surfaceHolder;
	private boolean runFlag = false;

	private boolean mode = false; // false == fast, true == quality

	public void changeMode() {
		mode = !mode;
	}

	public DrawImageThread(SurfaceHolder surfaceHolder){
		this.surfaceHolder = surfaceHolder;
	}

	public void setRunning(boolean run) {
		runFlag = run;
	}

	@Override
	public void run() {
		Canvas canvas;
		while(runFlag) {
			canvas = null;

			try {
				canvas = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					//noinspection ConstantConditions
					canvas.drawColor(Color.BLACK);
					canvas.drawBitmap(mode ? ImageHandler.getFastProcessingImage() : ImageHandler.getQualityProcessingImage(), 0, ImageHandler.NEW_WIDTH, 0, 0, ImageHandler.NEW_WIDTH, ImageHandler.NEW_HEIGHT, false, null);
				}
			} catch (NullPointerException e) {
				e.printStackTrace(System.err);
				System.out.println("very bad thing");
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
