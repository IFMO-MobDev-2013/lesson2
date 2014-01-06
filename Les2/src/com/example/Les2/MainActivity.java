package com.example.Les2;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		setContentView(new LessonTwoView(this, height, width));
	}
}