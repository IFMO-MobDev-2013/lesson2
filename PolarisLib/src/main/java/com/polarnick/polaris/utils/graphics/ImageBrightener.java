
package com.polarnick.polaris.utils.graphics;

import com.google.common.base.Preconditions;

/**
 * Date: 19.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ImageBrightener extends ImageProcessingBase {

    private double lighteningFactor;

    /**
     * @param lighteningFactor must be in range <b>[1.0, +infinity)</b>
     */
    public ImageBrightener(double lighteningFactor) {
        super();
        Preconditions.checkArgument(lighteningFactor >= 1.0);
        this.lighteningFactor = lighteningFactor;
    }

    @Override
    protected ImageProcessor getProcessor(byte[] source, int sourceWidth, int sourceHeight, byte[] target, int targetWidth, int targetHeight, int fromY, int toY) {
        return new BrightenerProcessor(source, sourceWidth, sourceHeight, target, targetWidth, targetHeight, fromY, toY);
    }

    protected class BrightenerProcessor extends ImageProcessor {

        public BrightenerProcessor(byte[] source, int sourceWidth, int sourceHeight, byte[] target, int targetWidth, int targetHeight, int fromY, int toY) {
            super(source, sourceWidth, sourceHeight, target, targetWidth, targetHeight, fromY, toY);
        }

        @Override
        protected void process() {
            for (int y = fromY; y < toY; y++) {
                for (int x = 0; x < targetWidth; x++) {
                    updateColor2(x, y);
                }
                if (progressHolder != null) {
                    progressHolder.registerProgressPassed(fullProgress * (y + 1 - fromY) / (toY - fromY));
                }
            }
        }

        private byte[] rgbBuffer = new byte[3];
        private double[] hsvBuffer = new double[3];

        private void updateColor(int x, int y) {
            int index = y * targetWidth + x;
            System.arraycopy(source, 4 * index, rgbBuffer, 0, 3);

            ImageBitmapUtils.convertRGBToHSV(rgbBuffer, hsvBuffer);
            hsvBuffer[ImageBitmapUtils.VALUE_INDEX] = Math.min(hsvBuffer[ImageBitmapUtils.VALUE_INDEX] * lighteningFactor, 1.0);
            ImageBitmapUtils.convertHSVToRGB(hsvBuffer, rgbBuffer);

            System.arraycopy(rgbBuffer, 0, target, 4 * index, 3);
            target[4 * index + 3] = source[4 * index + 3];
        }

        private void updateColor2(int x, int y) {
            int index = y * targetWidth + x;
            for (int i = 0; i < 3; i++) {
                int oldComponent = source[4 * index + i];
                if (oldComponent < 0) {
                    oldComponent += 256;
                }
                target[4 * index + i] = (byte) Math.min(oldComponent * lighteningFactor, 255);
            }
            target[4 * index + 3] = source[4 * index + 3];
        }
    }
}
