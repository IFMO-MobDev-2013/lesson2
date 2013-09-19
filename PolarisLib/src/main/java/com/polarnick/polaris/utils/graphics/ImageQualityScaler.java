package com.polarnick.polaris.utils.graphics;

import android.graphics.Bitmap;

import java.util.Arrays;

/**
 * Date: 18.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ImageQualityScaler extends ImageProcessingBase {

    private static final int SOURCE_MULTIPLIER = 3;

    private final double scaleFactor;

    public ImageQualityScaler(double scaleFactor) {
        super();
        this.scaleFactor = scaleFactor;
    }

    @Override
    protected int getTargetWidth(Bitmap sourceImage) {
        return ((int) (sourceImage.getWidth() * scaleFactor));
    }

    @Override
    protected int getTargetHeight(Bitmap sourceImage) {
        return ((int) (sourceImage.getHeight() * scaleFactor));
    }

    @Override
    protected ImageProcessor getProcessor(byte[] source, int sourceWidth, int sourceHeight, byte[] target, int targetWidth, int targetHeight, int fromY, int toY) {
        return new RotatingProcessor(source, sourceWidth, sourceHeight, target, targetWidth, targetHeight, fromY, toY);
    }

    protected class RotatingProcessor extends ImageProcessor {

        public RotatingProcessor(byte[] source, int sourceWidth, int sourceHeight, byte[] target, int targetWidth, int targetHeight, int fromY, int toY) {
            super(source, sourceWidth, sourceHeight, target, targetWidth, targetHeight, fromY, toY);
        }

        @Override
        protected void process() {
            for (int y = fromY; y < toY; y++) {
                for (int x = 0; x < targetWidth; x++) {
                    updateColor(x, y);
                }
                if (progressHolder != null) {
                    progressHolder.registerProgressPassed(fullProgress * (y + 1 - fromY) / (toY - fromY));
                }
            }
        }

        private final int[] sumComponents = new int[4];

        private void updateColor(int targetX, int targetY) {
            int leftX = sourceWidth * SOURCE_MULTIPLIER * targetX / targetWidth;
            int topY = sourceHeigth * SOURCE_MULTIPLIER * targetY / targetHeight;
            int rightX = sourceWidth * SOURCE_MULTIPLIER * (targetX + 1) / targetWidth;
            int bottomY = sourceHeigth * SOURCE_MULTIPLIER * (targetY + 1) / targetHeight;

            int countOfUsedPixels = 0;
            Arrays.fill(sumComponents, 0);
            for (int y = topY; y < bottomY; y++) {
                for (int x = leftX; x < rightX; x++) {
                    int sourceIndex = y / SOURCE_MULTIPLIER * sourceWidth + x / SOURCE_MULTIPLIER;
                    for (int i = 0; i < 4; i++) {
                        if (source[4 * sourceIndex + i] >= 0) {
                            sumComponents[i] = sumComponents[i] + source[4 * sourceIndex + i];
                        } else {
                            sumComponents[i] = sumComponents[i] + source[4 * sourceIndex + i] + 256;
                        }
                    }
                    countOfUsedPixels++;
                }
            }

            int targetIndex = targetY * targetWidth + targetX;
            for (int i = 0; i < 4; i++) {
                target[4 * targetIndex + i] = (byte) (sumComponents[i] / countOfUsedPixels);
            }
        }
    }
}