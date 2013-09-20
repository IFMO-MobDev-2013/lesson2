package ru.marsermd.lesson2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView rotator = (ImageView)findViewById(R.id.rotator);
        rotator.setScaleType(ImageView.ScaleType.CENTER);

        BitmapFactory.Options decoderOptions = new BitmapFactory.Options();

        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.source, decoderOptions);
        Log.e("OOOO", "" + bmp.getWidth());
        rotator.setImageBitmap(bmp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
