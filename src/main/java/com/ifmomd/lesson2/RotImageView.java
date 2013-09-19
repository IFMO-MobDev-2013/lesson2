package com.ifmomd.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: satori
 * Date: 9/19/13
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class RotImageView extends View implements View.OnClickListener {
    public Image image;
    private Paint p;
    public boolean isFast = true;
    public Image startimage;


    public RotImageView (Context cxt, AttributeSet as) {
        super(cxt,as);

        BitmapFactory.Options f = new BitmapFactory.Options();
        f.inScaled = false;
        f.inPreferredConfig = Bitmap.Config.ARGB_8888;

        image = new Image(BitmapFactory.decodeResource(getResources(), R.drawable.source, new BitmapFactory.Options()));
        p = new Paint();
        setOnClickListener(this);
        startimage = new Image(image);


    }
    @Override
    public void onClick (View v) {

        isFast = isFast ? false : true;
        if(isFast) {
            MainActivity.textview.setText("Fast");
        }
        else {
            MainActivity.textview.setText("Bilinear");
        }

        invalidate();

    }
    @Override
    public void onDraw(Canvas c) {
        int W = c.getWidth();
        int H = c.getHeight();
        float sy = (float) W / (float) image.WIDTH;
        float sx = (float) H / (float) image.HEIGHT;
        c.scale(sy,sx);
        c.drawBitmap(image.bm,0,0,p);


    }
}
