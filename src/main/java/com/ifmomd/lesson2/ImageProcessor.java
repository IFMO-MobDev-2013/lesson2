package com.ifmomd.lesson2;

import android.graphics.Color;

public class ImageProcessor {
    public static int[] roughDownscale(int[] oPixels, int oWidth, double downscale) {
        double multiplier = 1 / downscale;
        int rWidth = (int) (oWidth*multiplier);
        int rHeight = (int) (oPixels.length/oWidth * multiplier);
        int[] xsInOrigin = new int[rWidth];
        for (int x = 0; x < rWidth; x++)
            xsInOrigin[x] = (int) (x * downscale);
        int[] result = new int[rHeight * rWidth];
        double yStep = 1 * downscale;
        double yInOrigin = 0;
        int yIndex = 0;
        for (int y = 0; y < rHeight; y++, yInOrigin += yStep, yIndex += rWidth) {
            int yInOriginInteger = (int) yInOrigin;
            for (int x = 0; x < rWidth; x++)
                result[yIndex + x] = oPixels[yInOriginInteger * oWidth + xsInOrigin[x]];
        }

        return result;
    }

    public static int[] fineDownscale(int[] oPixels, int oWidth, double downscale) {
        int oHeight = oPixels.length/oWidth;
        int rWidth = (int) (oWidth / downscale);
        int rHeight = (int) (oHeight / downscale);
        int[] result = new int[rHeight * rWidth];
        double[] ysInOrigin = new double[rHeight + 1];
        for (int y = 0; y < rHeight; y++)
            ysInOrigin[y] = (y * downscale);
        ysInOrigin[rHeight] = oHeight - 1;
        double[] xsInOrigin = new double[rWidth + 1];
        for (int x = 0; x < rWidth; x++)
            xsInOrigin[x] = (x * downscale);
        xsInOrigin[rWidth] = oWidth - 1;
        double yStep = 1 * downscale;
        double yInOrigin = 0;
        int yIndex = 0;
        for (int y = 0; y < rHeight; y++, yInOrigin += yStep, yIndex += rWidth) {
            int yInOriginInteger = (int) yInOrigin;
            for (int x = 0; x < rWidth; x++) {
                int p00 = oPixels[yInOriginInteger * oWidth + (int)xsInOrigin[x]];
                int p01 = oPixels[yInOriginInteger * oWidth + (int)xsInOrigin[x] + 1];
                int p10 = oPixels[(yInOriginInteger+1) * oWidth + (int)xsInOrigin[x]];
                int p11 = oPixels[(yInOriginInteger+1) * oWidth + (int)xsInOrigin[x] + 1];

                int r = (int)(
                        (1-ysInOrigin[y]%1)*((1-xsInOrigin[x]%1)*Color.red(p00)+(xsInOrigin[x]%1)*Color.red(p01)) +
                        (ysInOrigin[y]%1)*((1-xsInOrigin[x]%1)*Color.red(p10)+(xsInOrigin[x]%1)*Color.red(p11))
                );
                int g = (int)(
                        (1-ysInOrigin[y]%1)*((1-xsInOrigin[x]%1)*Color.green(p00)+(xsInOrigin[x]%1)*Color.green(p01)) +
                        (ysInOrigin[y]%1)*((1-xsInOrigin[x]%1)*Color.green(p10)+(xsInOrigin[x]%1)*Color.green(p11))
                );
                int b = (int)(
                        (1-ysInOrigin[y]%1)*((1-xsInOrigin[x]%1)*Color.blue(p00)+(xsInOrigin[x]%1)*Color.blue(p01)) +
                        (ysInOrigin[y]%1)*((1-xsInOrigin[x]%1)*Color.blue(p10)+(xsInOrigin[x]%1)*Color.blue(p11))
                );
                result[y*rWidth+x] = Color.argb(255,r,g,b);
            }
        }
        return result;
    }

    public static int[] turnClockwise(int[] oPixels, int oWidth) {
        int rWidth = oPixels.length/oWidth;
        int[] result = new int[oWidth * rWidth];
        for (int y = 0; y < oWidth; y++)
            for (int x = 0; x < rWidth; x++)
                result[y * rWidth + (rWidth - 1 - x)] = oPixels[x * oWidth + y];
        return result;
    }

    public static int[] increaseBrightness(int[] oPixels, int oWidth) {
        int rHeight = oPixels.length/oWidth;
        int[] result = new int[rHeight * oWidth];
        for (int y = 0; y < rHeight; y++)
            for (int x = 0; x < oWidth; x++) {
                int pixel = oPixels[y * oWidth + x];
                result[y * oWidth + x] = Color.argb(Color.alpha(pixel),
                                                    Color.red(pixel) + (255 - Color.red(pixel)) / 2,
                                                    Color.green(pixel) + (255 - Color.green(pixel)) / 2,
                                                    Color.blue(pixel) + (255 - Color.blue(pixel)) / 2);
            }
        return result;
    }
}
