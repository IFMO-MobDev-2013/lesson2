package com.polarnick.day02;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Date: 16.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ImageView extends View {

    private final int screenWidth;
    private final int screenHeight;
    private Bitmap image;

    public ImageView(Context context, Bitmap image, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.image = image;
    }

    public void updateImage(Bitmap image) {
        this.image = image;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (image != null) {
            canvas.drawBitmap(image, (screenWidth - image.getWidth()) / 2, (screenHeight - image.getHeight()) / 2, null);
        }
    }
}
