package com.ifmomd.lesson2;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
    public static  Button button;
    public static  RotImageView myimageview;
    public static TextView textview;
    public static Button refreshButton;
    //public static Image startimage;
    public static Button brightenButton;
    public static Button rotateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.myButton);
        myimageview = (RotImageView)findViewById(R.id.myImageView);

        textview = (TextView)findViewById(R.id.mytextview);
        refreshButton = (Button)findViewById(R.id.refreshButton);
        brightenButton= (Button)findViewById(R.id.brightenButton);
        rotateButton = (Button)findViewById(R.id.rotateButton);
        //startimage = myimageview.image;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myimageview.isFast) {
                    myimageview.image.compressFast();
                }
                else {
                    myimageview.image.resizeBilinear();
                }
                myimageview.invalidate();
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myimageview.image = new Image(myimageview.startimage);
                myimageview.invalidate();

            }
        });
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myimageview.image.rotate();
                myimageview.invalidate();
            }
        });
        brightenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myimageview.image.brighten(50);
                myimageview.invalidate();
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    
}
