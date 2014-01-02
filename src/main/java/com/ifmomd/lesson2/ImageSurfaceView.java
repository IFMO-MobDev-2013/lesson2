package com.ifmomd.lesson2;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private DrawImageThread drawImageThread;


	public ImageSurfaceView(Context context) {
		super(context);

		this.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				drawImageThread.changeMode();
				invalidate();
			}
		});

		//noinspection ConstantConditions
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawImageThread = new DrawImageThread(holder);
		drawImageThread.setRunning(true);
		drawImageThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		drawImageThread.setRunning(false);

		while(retry) {
			try {
				drawImageThread.join();
				retry = false;
			} catch (InterruptedException e) {/* retry */}
		}
	}
}
