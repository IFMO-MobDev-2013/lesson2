package com.example.Picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class GoodSlowModification extends View {
    public static final double ZOOM = 1.73*2;

    public GoodSlowModification(Context context) {
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

    private int[] zoomPictureHeight(int[] pixels, int width, int height) {
        int length = width*height;
        int[] tmp = new int[length*2];
        for (int i = 0; i < height; i++) {
            int slog = i*width;
            int second_slog = 2*i*width;
            for (int j = 0; j < width; j++)
                tmp[second_slog + j] = pixels[slog + j];
        }
        for (int i = 1; i < 2*height - 2; i+=2) {
            int slog = i*width;
            for (int j = 0; j < width; j++)
                tmp[slog + j] = Union(tmp[slog + j - width], tmp[slog + j + width]);
        }

        for (int j = 0; j < width; j++)
            tmp[(2*height - 1)*width + j] = tmp[(2*height - 2)*width + j];
        return tmp;
    }

    private int[] zoomPictureWidth(int[] pixels, int width, int height) {
        int length = width*height;
        int[] tmp = new int[length*2];
        for (int i = 0; i < length; i++)
            tmp[i*2]  = pixels[i];

        for (int i = 1; i < length*2 - 2; i+=2)
            tmp[i] = Union(tmp[i - 1], tmp[i + 1]);

        tmp[2*length - 1] = tmp[2*length - 2];
        return tmp;
    }
    @Override
    public void onDraw(Canvas canvas) {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
        int Width = image.getWidth();
        int Height = image.getHeight();
        Paint paint = new Paint();
        paint.setARGB(255, 255, 0, 255);
        int[] pixels = new int[Width*Height];
        boolean[] was = new boolean[Width*Height];
        image.getPixels(pixels, 0, Width, 0, 0, Width, Height);

        pixels = zoomPictureHeight(pixels, Width, Height);
        Height *= 2;
        pixels = zoomPictureWidth(pixels, Width, Height);
        Width *= 2;

        // shrinking
        int newWidth = (int)((Width/ZOOM) + Math.floor(Width%ZOOM));
        int newHeight = (int)((Height/ZOOM) + Math.floor(Height%ZOOM));

        for (int i = 0; i < Height; i++) {
            int x = i;
            x = (int)((x/ZOOM) + Math.floor(x%ZOOM));
            int y;
            for (int j = 0; j < Width; j++) {
                y = j;
                y = (int)((y/ZOOM) + Math.floor(y%ZOOM));
                if (!was[x*newWidth]) {
                    pixels[x*newWidth + y] = pixels[i*Width + j];
                    was[i] = true;
                }
                else
                    pixels[x*newWidth + y] = Union(pixels[i*Width + j], pixels[x*newWidth + y]);
            }
        }

        // turning
        int[] imageTurn = new int[newHeight*newWidth];
        for (int i = 0; i < newHeight; i++) {
            int x = i;
            int y, xx, yy;
            for (int j = 0; j < newWidth; j++) {
                y = j;
                yy = newHeight - 1 - x;
                xx = y;
                imageTurn[xx*newHeight + yy] = pixels[i*newWidth + j];
            }
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
        canvas.drawText("GOOD SLOW MODIFICATION", 10, 400, paint);
    }
}
