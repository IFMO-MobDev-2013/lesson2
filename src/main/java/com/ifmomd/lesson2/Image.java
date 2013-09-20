package main.java.com.ifmomd.lesson2;


import static android.graphics.Color.*;

/**
 * Class for presentation image in memory. Includes some base algorithms.
 * <p/>
 * Contains width,height parameters and linear array with pixels in ARGB_8888 format.
 */
public class Image {
    private int[] pixels;
    private int width, height;

    /**
     * Create new image
     *
     * @param pixels must be in ARGB_8888 fotmat
     * @param width  image width
     * @param height image height
     */
    public Image(int[] pixels, int width, int height) {
        checkDimensions(width, height);
        checkPixels(pixels, width, height);

        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }

    private void checkDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Incorrect dimensions");
        }
    }

    private void checkPixels(int[] pixels, int width, int height) {
        if (pixels.length != width * height) {
            throw new IllegalArgumentException("Incorrect pixels's size");
        }
    }

    /**
     * Scale current image to new dimensions by nearest neighbor algorithm
     *
     * @param newWidth  new width value
     * @param newHeight new height value
     * @return scaled image
     */
    public Image fastScale(int newWidth, int newHeight) {
        checkDimensions(newWidth, newHeight);

        int[] newPixels = new int[newWidth * newHeight];

        int xRatio = (width << 16) / newWidth + 1;
        int yRatio = (height << 16) / newHeight + 1;

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int x = (j * xRatio) >> 16;
                int y = (i * yRatio) >> 16;
                newPixels[(i * newWidth) + j] = pixels[(y * width) + x];
            }
        }

        return new Image(newPixels, newWidth, newHeight);
    }

    /**
     * Increase image brightness to increaseFactor
     *
     * @param increaseFactor value that will be increased each color channel
     * @return image with changed brightness
     */
    public Image changeBrightness(int increaseFactor) {
        int[] newPixels = new int[width * height];

        for (int i = 0; i < width * height; i++) {
            int alpha = alpha(pixels[i]);
            int red = red(pixels[i]);
            int green = green(pixels[i]);
            int blue = blue(pixels[i]);

            red = checkChannelValue(red + increaseFactor);
            green = checkChannelValue(green + increaseFactor);
            blue = checkChannelValue(blue + increaseFactor);

            newPixels[i] = argb(alpha, red, green, blue);
        }

        return new Image(newPixels, width, height);
    }

    /**
     * Check channel value that it contains in 0..255 range
     *
     * @param value value to be checked
     * @return value, if it contains in 0..255 range; 0 if value < 0; 255 if value > 0
     */
    private int checkChannelValue(int value) {
        if (value < 0) {
            return 0;
        }

        if (value > 0xff) {
            return 0xff;
        }

        return value;
    }

    /**
     * Count brightness as arithmetical mean of all color channels in each pixel
     *
     * @return counted brightness value
     */
    public int countBrightness() {
        int sum = 0;

        for (int i = 0; i < width * height; i++) {
            int red = red(pixels[i]);
            int green = green(pixels[i]);
            int blue = blue(pixels[i]);

            sum += red + green + blue;
        }

        return sum / (3 * width * height);
    }

    /**
     * Rotate image on 90 degrees clockwise
     *
     * @return rotated image
     */
    public Image rotateClockwise() {
        int newWidth = height;
        int newHeight = width;
        int[] newPixels = new int[newWidth * newHeight];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                newPixels[x * newWidth + (height - y - 1)] = pixels[y * width + x];
            }
        }

        return new Image(newPixels, newWidth, newHeight);
    }

    /**
     * Scale current image to new dimensions by bilinear interpolation algorithm
     *
     * @param newWidth  new width value
     * @param newHeight new height value
     * @return scaled image
     */
    public Image bilinearInterpolationScale(int newWidth, int newHeight) {
        int[] newPixels = new int[newWidth * newHeight];

        for (int y = 0; y < newHeight; y++) {
            float tmp = (float) y / (newHeight - 1) * (height - 1);

            int h = (int) Math.floor(tmp);
            if (h < 0) {
                h = 0;
            } else {
                if (h > height - 2) {
                    h = height - 2;
                }
            }

            float u = (tmp - h);

            for (int x = 0; x < newWidth; x++) {
                tmp = (float) x / (newWidth - 1) * (width - 1);

                int w = (int) Math.floor(tmp);
                if (w < 0) {
                    w = 0;
                } else {
                    if (w > width - 2) {
                        w = width - 2;
                    }
                }

                float t = tmp - w;

                float d1 = (1 - t) * (1 - u);
                float d2 = t * (1 - u);
                float d3 = t * u;
                float d4 = (1 - t) * u;

                int p1 = pixels[h * width + w];
                int p2 = pixels[h * width + w + 1];
                int p3 = pixels[(h + 1) * width + w + 1];
                int p4 = pixels[(h + 1) * width + w + 1];

                int alpha = (int) (alpha(p1) * d1 + alpha(p2) * d2 + alpha(p3) * d3 + alpha(p4) * d4);
                int red = (int) (red(p1) * d1 + red(p2) * d2 + red(p3) * d3 + red(p4) * d4);
                int green = (int) (green(p1) * d1 + green(p2) * d2 + green(p3) * d3 + green(p4) * d4);
                int blue = (int) (blue(p1) * d1 + blue(p2) * d2 + blue(p3) * d3 + blue(p4) * d4);

                newPixels[y * newWidth + x] = argb(alpha, red, green, blue);
            }
        }

        return new Image(newPixels, newWidth, newHeight);
    }

    /**
     * Get array of pixels
     *
     * @return array of pixels
     */
    public int[] getPixels() {
        return pixels;
    }

    /**
     * Get image width
     *
     * @return image width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get image Height
     *
     * @return image height
     */
    public int getHeight() {
        return height;
    }
}
