package ru.zulyaev.ifmo.android.lesson2;

/**
 * @author Никита
 */
public class SimpleImageScalingView implements Image {
    private final Image image;
    private final int destHeight;
    private final int destWidth;

    public SimpleImageScalingView(Image image, int destHeight, int destWidth) {
        if (image == null) {
            throw new NullPointerException();
        }
        if (destWidth < 1 || destHeight < 1) {
            throw new IllegalArgumentException();
        }

        this.image = image;
        this.destHeight = destHeight;
        this.destWidth = destWidth;
    }

    @Override
    public int getPixel(int y, int x) {
        return image.getPixel(y * image.getHeight() / destHeight, x * image.getWidth() / destWidth);
    }

    @Override
    public int getWidth() {
        return destWidth;
    }

    @Override
    public int getHeight() {
        return destHeight;
    }
}
