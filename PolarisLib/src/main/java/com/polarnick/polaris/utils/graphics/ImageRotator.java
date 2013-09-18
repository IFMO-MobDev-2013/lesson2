package com.polarnick.polaris.utils.graphics;

import android.graphics.Bitmap;

/**
 * Date: 18.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ImageRotator extends ImageProcessingBase {

    public ImageRotator() {
    }

    @Override
    protected int getTargetWidth(Bitmap sourceImage) {
        return sourceImage.getHeight();
    }

    @Override
    protected int getTargetHeight(Bitmap sourceImage) {
        return sourceImage.getWidth();
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
                for (int x = 0; x < sourceHeigth; x++) {
                    final int sourceIndex = (sourceHeigth - x - 1) * sourceWidth + y;
                    final int targetIndex = y * sourceHeigth + x;
                    System.arraycopy(source, 4 * sourceIndex, target, 4 * targetIndex, 4);
                }
                if (progressHolder != null) {
                    progressHolder.registerProgressPassed(fullProgress * (y + 1) / (toY - fromY));
                }
            }
        }
    }
}
