package com.example.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

public class MyActivity extends Activity
{
    BitmapFactory.Options OPTIONS;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        OPTIONS = new BitmapFactory.Options();
        OPTIONS.inScaled = false;
        OPTIONS.inPreferredConfig = Bitmap.Config.RGB_565;

        BitmapFactory.decodeResource(getResources(), R.drawable.source);
        Bitmap orig = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        MyView view = new MyView(this, orig);
        setContentView(view);
    }
}
