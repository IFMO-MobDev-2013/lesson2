package ru.ifmo.mobdev.MatveevA.PictureLesson;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;

public class StartActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ImageButton imgButton = (ImageButton) findViewById(R.id.image1);
        imgButton.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.source));


    }

    public void onClick(View v) {
        Intent intent = new Intent(StartActivity.this, MenuActivity.class);
        startActivity(intent);
    }


}
