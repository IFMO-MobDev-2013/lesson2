package com.example.les2_1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class MyActivity extends Activity {
    int screen_w;
    int screen_h;
    public static int end_w = 405;
    public static int end_h = 434;
    public static Bitmap bitmap;
    public static int[] pix;
    public static int[] fast = new int[end_h*end_w];
    public static int[] amazing = new int[end_h*end_w];


    private void fastscale(){
        // Scale
        float kw = (float)bitmap.getWidth()/end_w;
        float kh = (float)bitmap.getHeight()/end_h;
        for (int i=0;i<end_h;i++){
            for (int j=0;j<end_w;j++){
                fast[i*end_w + j]=pix[((int)(i*kh)*bitmap.getWidth()) + ((int)(j*kw))];
            }
        }
        // Rotate
        int[] pic = new int[end_h*end_w];
        for (int i=0;i<end_h;i++){
            for (int j=0;j<end_w;j++){
                pic[j * end_w + (end_h - i -1)] = fast[i*end_w+j];
            }
        }
        for (int i=0;i<fast.length;i++){
            fast[i]=pic[i];
        }
        // Braitnes
        Color color = new Color();
        int r,g,b;
        for (int i=0;i<fast.length;i++){
            int pixel = fast[i];
            fast[i] = Color.argb(Math.min(255, (int) ((double) Color.alpha(pixel) * 1.5)),
                    Math.min(255, (int) ((double) Color.red(pixel) * 1.5)),
                    Math.min(255, (int) ((double) Color.green(pixel) * 1.5)),
                    Math.min(255, (int) ((double) Color.blue(pixel) * 1.5)));
        }
    }

    private void amazingscale(){
        //Scale
        float kw = (float)bitmap.getWidth()/(end_w*2);
        float kh = (float)bitmap.getHeight()/(end_h*2);
        int[] big = new int[end_w*end_h*4];
        for (int i=0;i<end_h*2;i++){
            for (int j=0;j<end_w*2;j++){
                big[i*end_w*2 + j]=pix[((int)(i*kh)*bitmap.getWidth()) + ((int)(j*kw))];
            }
        }
        kw = 2;
        kh = 2;
        for (int i=0;i<end_h-1;i++){
            for (int j=0;j<end_w-1;j++){
                int pix=0;
                int r=0,g=0,b=0;
                Color color = new Color();
                for (int t=0;t<kh;t++)
                    for (int y=0;y<kw;y++){
                        r += color.red(big[(((int) (i * kh + t) * end_w * 2) + ((int) (j * kw + y))) % big.length])/4;
                        g += color.green(big[(((int) (i * kh + t) * end_w * 2) + ((int) (j * kw + y))) % big.length])/4;
                        b += color.blue(big[(((int)(i*kh+t)*end_w*2) + ((int)(j*kw+y)))%big.length])/4;
                    }
                pix = color.rgb(r,g,b);
                amazing[i*end_w + j]=pix;
            }
        }
        // Rotate
        int[] pic = new int[end_h*end_w];
        for (int i=0;i<end_h;i++){
            for (int j=0;j<end_w;j++){
                pic[j * end_w + (end_h - i -1)] = amazing[i*end_w+j];
            }
        }
        for (int i=0;i<amazing.length;i++){
           amazing[i]=pic[i];
        }
        // Braitnes
        Color color = new Color();
        int r,g,b;
        for (int i=0;i<amazing.length;i++){
            int pixel = amazing[i];
            amazing[i] = Color.argb(Math.min(255, (int) ((double) Color.alpha(pixel) * 1.5)),
                    Math.min(255, (int) ((double) Color.red(pixel) * 1.5)),
                    Math.min(255, (int) ((double) Color.green(pixel) * 1.5)),
                    Math.min(255, (int) ((double) Color.blue(pixel) * 1.5)));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.source);
        pix = new int[bitmap.getHeight()*bitmap.getWidth()];
        bitmap.getPixels(pix, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        fastscale();
        amazingscale();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_w=size.x;
        screen_h=size.y;
        super.onCreate(savedInstanceState);
        setContentView(new MySurfaceView(this));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// full screen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    }
}
