package ru.georgeee.android.gImageResizer;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 18.09.13
 * Time: 2:13
 * To change this template use File | Settings | File Templates.
 */
public class ImageManipulator {
    int[] pixels;
    int width;
    int height;

    public void setPixels(int[] pixels, int width, int height) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void fastScale(int newWidth, int newHeight) {
        int[] newPixels = new int[newHeight * newWidth];
        int xScale = (width << 16) / newWidth;
        int yScale = (height << 16) / newHeight;
        for (int pixel = 0, x = 0, y = 0; pixel < newPixels.length; ++pixel, ++x) {
            if (x == newWidth) {
                x = 0;
                ++y;
            }
            newPixels[pixel] = pixels[((yScale * y) >> 16) * width + ((xScale * x) >> 16)];
        }
        setPixels(newPixels, newWidth, newHeight);
    }

    public void changeBrightness(float correctionFactor) {
        for (int pixel = 0; pixel < pixels.length; ++pixel) {
            pixels[pixel] = changeColorBrightness(pixels[pixel], correctionFactor);
        }
    }

    public void rotateClockwise90() {
        int[] newPixels = new int[height * width];
        for (int pixel = 0, x = 0, y = 0; pixel < newPixels.length; ++pixel, ++x) {
            if (x == width) {
                x = 0;
                ++y;
            }
            newPixels[x * height + (height - y - 1)] = pixels[pixel];
        }
        setPixels(newPixels, height, width);
    }

    public void betterScale(int newWidth, int newHeight) {
        int[] newPixels = new int[newHeight * newWidth];
        billinearInterpollation(newWidth, newHeight, pixels, newPixels);
        setPixels(newPixels, newWidth, newHeight);
    }

    protected void billinearInterpollation(int newWidth, int newHeight, int pixels[], int newPixels[]) {
        int nexX, newY;
        int y, x;
        //Tmp variables
        float t, u, tmp;
        //Shift in pixels array, just for easy use
        int hShift;
        //Coefficients
        float d1, d2, d3, d4;
        //Pixels, from which we will build new one
        int p1, p2, p3, p4;
        //Color components
        int red, green, blue;

        for (newY = 0; newY < newHeight; newY++) {
            tmp = (float) (newY) / (float) (newHeight - 1) * (height - 1);
            y = (int) Math.floor(tmp);
            if (y < 0) {
                y = 0;
            } else {
                if (y >= height - 1) {
                    y = height - 2;
                }
            }
            u = tmp - y;

            for (nexX = 0; nexX < newWidth; nexX++) {
                tmp = (float) (nexX) / (float) (newWidth - 1) * (width - 1);
                x = (int) Math.floor(tmp);
                if (x < 0) {
                    x = 0;
                } else {
                    if (x >= width - 1) {
                        x = width - 2;
                    }
                }
                t = tmp - x;

                d1 = (1 - t) * (1 - u);
                d2 = t * (1 - u);
                d3 = t * u;
                d4 = (1 - t) * u;

                hShift = y * width;
                p1 = pixels[hShift + x];
                p2 = pixels[hShift + x + 1];

                hShift = (1 + y) * width;
                p3 = pixels[hShift + x + 1];
                p4 = pixels[hShift + x];

                blue = ((int) ((p1 & 0xff0000) * d1 + (p2 & 0xff0000) * d2 + (p3 & 0xff0000) * d3 + (p4 & 0xff0000) * d4)) & 0xff0000;
                green = ((int) ((p1 & 0x00ff00) * d1 + (p2 & 0x00ff00) * d2 + (p3 & 0x00ff00) * d3 + (p4 & 0x00ff00) * d4)) & 0x00ff00;
                red = ((int) ((p1 & 0x0000ff) * d1 + (p2 & 0x0000ff) * d2 + (p3 & 0x0000ff) * d3 + (p4 & 0x0000ff) * d4)) & 0x0000ff;

                newPixels[newY * newWidth + nexX] = red | green | blue;
            }
        }
    }


    //Combined method, to gain more productivity (1 cycle against 3)
    public void fastScaleRotateBritenAtOnce(int newWidth, int newHeight, float correctionFactor) {
        int[] newPixels = new int[newHeight * newWidth];
        int xScale = (width << 16) / newWidth;
        int yScale = (height << 16) / newHeight;
        for (int pixel = 0, x = 0, y = 0; pixel < newPixels.length; ++pixel, ++x) {
            if (x == newWidth) {
                x = 0;
                ++y;
            }
            newPixels[x * newHeight + (newHeight - y - 1)] = changeColorBrightness(
                    pixels[((yScale * y) >> 16) * width + ((xScale * x) >> 16)], correctionFactor);
        }
        setPixels(newPixels, newHeight, newWidth);
    }

    private int changeColorBrightness(int color, float correctionFactor) {
        float red = (float) (color & 0x0000ff);
        float green = (float) ((color & 0x00ff00) >> 8);
        float blue = (float) ((color & 0xff0000) >> 16);

        if (correctionFactor < 0) {
            correctionFactor = 1 + correctionFactor;
            red *= correctionFactor;
            green *= correctionFactor;
            blue *= correctionFactor;
        } else {
            red = (255 - red) * correctionFactor + red;
            green = (255 - green) * correctionFactor + green;
            blue = (255 - blue) * correctionFactor + blue;
        }

        return ((int) red) | (((int) green) << 8) | (((int) blue) << 16);
    }
}
