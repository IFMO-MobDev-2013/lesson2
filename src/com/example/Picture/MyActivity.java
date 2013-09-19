package com.example.Picture;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.lang.Object;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MyActivity extends Activity implements View.OnClickListener {
    int cur = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button fast = (Button) findViewById(R.id.fast_bad_modification);
        Button slow = (Button) findViewById(R.id.slow_good_modification);
        fast.setOnClickListener(this);
        slow.setOnClickListener(this);

     }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fast_bad_modification:
                setContentView(new FastBadModification(this));
                break;
            case R.id.slow_good_modification:
                setContentView(new GoodSlowModification(this));
                break;
        }
    }
}

