package ru.zulyaev.ifmo.android.lesson2;

import ru.zulyaev.ifmo.android.lesson2.util.ColorUtils;
import ru.zulyaev.ifmo.android.lesson2.util.MathUtils;

/**
 * @author Никита
 */
public class ImageScalingView implements Image {
    private final Image image;
    private final int destHeight;
    private final int destWidth;
    private final int[] buffer;

    private final int upscaleWidth;
    private final int downscaleWidth;
    private final int upscaleHeight;
    private final int downscaleHeight;
    private final int downscaleRate;

    public ImageScalingView(Image image, int destHeight, int destWidth) {
        if (image == null) {
            throw new NullPointerException();
        }
        if (destWidth < 1 || destHeight < 1) {
            throw new IllegalArgumentException();
        }

        this.image = image;
        this.destHeight = destHeight;
        this.destWidth = destWidth;
        this.buffer = new int[destHeight * destWidth];

        final int tempWidth = MathUtils.lcm(image.getWidth(), destWidth);
        upscaleWidth = tempWidth / image.getWidth();
        downscaleWidth = tempWidth / destWidth;
        final int tempHeight = MathUtils.lcm(image.getHeight(), destHeight);
        upscaleHeight = tempHeight / image.getHeight();
        downscaleHeight = tempHeight / destHeight;
        downscaleRate = downscaleWidth * downscaleHeight;

        initBuffer();
    }

    private void initBuffer() {
        for (int i = 0; i < destHeight; ++i) {
            final int y = i * downscaleHeight / upscaleHeight;
            final int offsetY = i * downscaleHeight % upscaleHeight;

            for (int j = 0; j < destWidth; ++j) {
                int sa = 0, sr = 0, sg = 0, sb = 0;

                final int x = j * downscaleWidth / upscaleWidth;
                final int offsetX = j * downscaleWidth % upscaleWidth;

                for (int leftY = downscaleHeight, k = 0, offY = offsetY; leftY != 0; ++k) {
                    final int yMultiplier = Math.min(leftY, upscaleHeight - offY);
                    offY = (offY + yMultiplier) % upscaleHeight;
                    leftY -= yMultiplier;

                    for (int leftX = downscaleWidth, l = 0, offX = offsetX; leftX != 0; ++l) {
                        final int xMultiplier = Math.min(leftX, upscaleWidth - offX);
                        offX = (offX + xMultiplier) % upscaleWidth;
                        leftX -= xMultiplier;

                        int color = image.getPixel(y + k, x + l);
                        int multiplier = xMultiplier * yMultiplier;

                        sa += (color >> 24 & 0xFF) * multiplier;
                        sr += (color >> 16 & 0xFF) * multiplier;
                        sg += (color >> 8  & 0xFF) * multiplier;
                        sb += (color       & 0xFF) * multiplier;
                    }
                }

                buffer[i * destWidth + j] = ColorUtils.rgba(sr / downscaleRate, sg / downscaleRate, sb / downscaleRate, sa / downscaleRate);
            }
        }
    }

    @Override
    public int getPixel(int y, int x) {
        return buffer[y * destWidth + x];
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
