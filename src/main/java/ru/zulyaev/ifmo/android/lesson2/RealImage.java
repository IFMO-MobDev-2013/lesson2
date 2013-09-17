package ru.zulyaev.ifmo.android.lesson2;

import android.graphics.Bitmap;

/**
 * @author Никита
 */
public class RealImage implements Image {
    private int[] buffer;
    private int width;
    private int height;

    public RealImage(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        buffer = new int[width * height];
        bitmap.getPixels(buffer, 0, width, 0, 0, width, height);
    }

    @Override
    public int getPixel(int y, int x) {
        return buffer[y * width + x];
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
