package com.ifmomd.lesson2;

import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.view.Display;
import android.view.WindowManager;

public class MainActivity extends Activity {

	public static int SCREEN_WIDTH; //constant
	public static int SCREEN_HEIGHT; //constant

	ImageHandler imageHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getScreenSize();
		getSourceImage();


		setContentView(new ImageSurfaceView(this));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
	}

	private void getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		SCREEN_WIDTH = size.x;
		SCREEN_HEIGHT = size.y;
	}

	private void getSourceImage() {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		imageHandler = new ImageHandler(BitmapFactory.decodeResource(getResources(), R.drawable.source, options));
	}

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

}
