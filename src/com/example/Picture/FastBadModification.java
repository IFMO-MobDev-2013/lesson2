package com.example.Picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class FastBadModification extends View {
    public static final double ZOOM = 1.73;
    public FastBadModification(Context context) {
        super(context);
    }

    private int Union(int x, int y) {
        int real_x = x;
        int real_y = y;
        int tmp = 0xFF000000;
        x &= 0x000000FF;
        y &= 0x000000FF;
        tmp += (((x + y)/2) & 0x000000FF);

        x = real_x;
        y = real_y;
        x &= 0x0000FF00;
        y &= 0x0000FF00;
        tmp += (((x + y)/2) & 0x0000FF00);

        x = real_x;
        y = real_y;
        x &= 0x00FF0000;
        y &= 0x00FF0000;
        tmp += (((x + y)/2) & 0x00FF0000);
        return tmp;
    }

    @Override
    public void onDraw(Canvas canvas) {
        set
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
        int Width = image.getWidth();
        int Height = image.getHeight();
        Paint paint = new Paint();
        paint.setARGB(255, 255, 0, 255);
        int[] pixels = new int[Width*Height];
        image.getPixels(pixels, 0, Width, 0, 0, Width, Height);
        int newWidth = (int)((Width/ZOOM) + Math.floor(Width%ZOOM));
        int newHeight = (int)((Height/ZOOM) + Math.floor(Height%ZOOM));

        // shrinking
         for (int i = 0; i < Width*Height; i++) {
            int x = i / Width;
            int y = i % Width;
            x /= ZOOM; y /= ZOOM;
            pixels[x*newWidth + y] = pixels[i];
         }
        // turning
        int[] imageTurn = new int[newHeight*newWidth];
        for (int i = 0; i < newWidth*newHeight; i++) {
            int x = i / newWidth;
            int y = i % newWidth;
            int yy = newHeight - 1 - x;
            int xx = y;
            imageTurn[xx*newHeight + yy] = pixels[i];
        }
        // brightly up X2

        for (int i = 0; i < newHeight*newWidth; i++) {
            int color, checkPixel;
            color = imageTurn[i];
            checkPixel = color &= 0x000000FF;
            checkPixel *= 1.5;
            checkPixel &= 0x000000FF;
            if (checkPixel < color)
                imageTurn[i] = imageTurn[i] - (imageTurn[i] & 0x000000FF) + 0x000000FF;
            else
                imageTurn[i] = imageTurn[i] - (imageTurn[i] & 0x000000FF) + checkPixel;

            color = imageTurn[i];
            color >>>= 8;
            checkPixel = color &= 0x000000FF;
            checkPixel *= 1.5;
            checkPixel &= 0x000000FF;
            if (checkPixel < color)
                imageTurn[i] = imageTurn[i] - (imageTurn[i] & 0x0000FF00) + 0x0000FF00;
            else
                imageTurn[i] = imageTurn[i] - (imageTurn[i] & 0x0000FF00) + (checkPixel << 8);

            color = imageTurn[i];
            color >>>= 16;
            checkPixel = color &= 0x000000FF;
            checkPixel *= 1.5;
            checkPixel &= 0x000000FF;
            if (checkPixel < color)
                imageTurn[i] = imageTurn[i] - (imageTurn[i] & 0x00FF0000) + 0x00FF0000;
            else
                imageTurn[i] = imageTurn[i] - (imageTurn[i] & 0x00FF0000) + (checkPixel << 16);
        }
        // printing
        canvas.drawBitmap(imageTurn, 0, newHeight, 0, 0, newHeight, newWidth, true, null);
        canvas.drawText("NEW WIDTH: " + newHeight, 10, 10, paint);
        canvas.drawText("NEW HEIGHT: " + newWidth, 10, 30, paint);
        canvas.drawText("FAST BAD MODIFICATION", 10, 400, paint);
    }
}

