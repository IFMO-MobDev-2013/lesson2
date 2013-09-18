package ru.zulyaev.ifmo.android.lesson2;

import ru.zulyaev.ifmo.android.lesson2.util.ColorUtils;

/**
 * @author Никита
 */
public class ImageLighteningView implements Image {
    private Image image;

    public ImageLighteningView(Image image) {
        this.image = image;
    }

    private static final float[] BUFFER = new float[4];

    private static int makeBrighter(int color) {
        return ColorUtils.hsla(ColorUtils.lighten(ColorUtils.toHSLA(color, BUFFER), 0.1f));
    }

    @Override
    public int getPixel(int y, int x) {
        return makeBrighter(image.getPixel(y, x));
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }
}
