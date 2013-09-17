package ru.zulyaev.ifmo.android.lesson2;

/**
 * @author Никита
 */
public interface Image {
    int getPixel(int y, int x);
    int getWidth();
    int getHeight();
}
