package com.example.l2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: elena
 * Date: 19.09.13
 * Time: 21:52
 * To change this template use File | Settings | File Templates.
 */
public class ImgView extends View {

    private Bitmap draw;

    public ImgView(Context context, Bitmap image) {
        super(context);
        this.draw = image;
    }

    public void updateImage(Bitmap image) {
        this.draw = image;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (draw != null) {
            canvas.drawBitmap(draw, 0, 0, null);


        }
    }
}
