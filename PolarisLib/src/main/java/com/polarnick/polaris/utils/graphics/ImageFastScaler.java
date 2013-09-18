
package com.polarnick.polaris.utils.graphics;

import android.graphics.Bitmap;

/**
 * Date: 18.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ImageFastScaler extends ImageProcessingBase {

    private final double scaleFactor;

    public ImageFastScaler(double scaleFactor) {
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
                    updateFastColor(x, y);
                }
                if (progressHolder != null) {
                    progressHolder.registerProgressPassed(fullProgress * (y + 1 - fromY) / (toY - fromY));
                }
            }
        }

        private void updateFastColor(int x, int y) {
            int sourceX = x * sourceWidth / targetWidth;
            int sourceY = y * sourceHeigth / targetHeight;
            int sourceIndex = sourceY * sourceWidth + sourceX;
            int targetIndex = y * targetWidth + x;
            System.arraycopy(source, sourceIndex * 4, target, targetIndex * 4, 4);
        }
    }
}
