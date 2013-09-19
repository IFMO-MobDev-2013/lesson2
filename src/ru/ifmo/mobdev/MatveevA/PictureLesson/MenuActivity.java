package ru.ifmo.mobdev.MatveevA.PictureLesson;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MenuActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        Button highQualityButton = (Button)findViewById(R.id.highQualityButton);
        Button lowQualityButton = (Button)findViewById(R.id.lowQualityButton);

        highQualityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int priority = 0;
                Intent intent = new Intent(MenuActivity.this, WorkActivity.class);
                intent.putExtra("priority", priority);
                startActivity(intent);
            }
        });

        lowQualityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int priority = 1;
                Intent intent = new Intent(MenuActivity.this, WorkActivity.class);
                intent.putExtra("priority", priority);
                startActivity(intent);
            }
        });
    }

}
