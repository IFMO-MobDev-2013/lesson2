package ru.zulyaev.ifmo.android.lesson2;

import ru.zulyaev.ifmo.android.lesson2.util.ColorUtils;
import ru.zulyaev.ifmo.android.lesson2.util.MathUtils;

/**
 * @author Никита
 */
public enum ImageProcessor {
    GOOD {
        @Override
        public int[] proccess(int[] colors, int srcWidth, int srcHeight, int destWidth, int destHeight) {
            final float[] hslaBuffer = new float[4];
            final int[] result = new int[destWidth * destHeight];

            final int tempWidth = MathUtils.lcm(srcWidth, destWidth);
            final int upscaleWidth = tempWidth / srcWidth;
            final int downscaleWidth = tempWidth / destWidth;
            final int tempHeight = MathUtils.lcm(srcHeight, destHeight);
            final int upscaleHeight = tempHeight / srcHeight;
            final int downscaleHeight = tempHeight / destHeight;
            final int downscaleRate = downscaleWidth * downscaleHeight;

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

                            int color = colors[(y + k) * srcWidth + x + l];
                            int multiplier = xMultiplier * yMultiplier;

                            sa += (color >> 24 & 0xFF) * multiplier;
                            sr += (color >> 16 & 0xFF) * multiplier;
                            sg += (color >> 8  & 0xFF) * multiplier;
                            sb += (color       & 0xFF) * multiplier;
                        }
                    }

                    result[j * destHeight + destHeight - i - 1] = ColorUtils.hsla(
                            ColorUtils.lighten(
                                    ColorUtils.toHSLA(
                                            sr / downscaleRate,
                                            sg / downscaleRate,
                                            sb / downscaleRate,
                                            sa / downscaleRate,
                                            hslaBuffer
                                    ),
                                    0.1f
                            )
                    );
                }
            }
            return result;
        }
    },
    FAST {
        @Override
        public int[] proccess(int[] colors, int srcWidth, int srcHeight, int destWidth, int destHeight) {
            final int[] result = new int[destWidth * destHeight];
            final float[] hslaBuffer = new float[4];

            for (int i = 0; i < destHeight; ++i) {
                for (int j = 0; j < destWidth; ++j) {
                    result[j * destHeight + destHeight - i - 1] = ColorUtils.hsla(
                            ColorUtils.lighten(
                                    ColorUtils.toHSLA(
                                            colors[i * srcHeight / destHeight * srcWidth + j * srcWidth / destWidth], hslaBuffer
                                    ),
                                    0.1f
                            )
                    );
                }
            }

            return result;
        }
    };

    public abstract int[] proccess(int[] colors, int srcWidth, int srcHeight, int destWidth, int destHeight);
}
