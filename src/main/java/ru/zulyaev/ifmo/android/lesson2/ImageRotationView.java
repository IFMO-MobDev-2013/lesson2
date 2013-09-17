package ru.zulyaev.ifmo.android.lesson2;

/**
 * @author Никита
 */
public class ImageRotationView implements Image {
    private Image image;

    public ImageRotationView(Image image) {
        this.image = image;
    }

    @Override
    public int getPixel(int y, int x) {
        //noinspection SuspiciousNameCombination
        return image.getPixel(getWidth() - x - 1, y);
    }

    @Override
    public int getWidth() {
        return image.getHeight();
    }

    @Override
    public int getHeight() {
        return image.getWidth();
    }
}
