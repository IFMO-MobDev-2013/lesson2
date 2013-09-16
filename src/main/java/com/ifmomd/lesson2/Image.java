package main.java.com.ifmomd.lesson2;


import static android.graphics.Color.*;

/**
 * ARGB_8888 bitmaps only
 */
public class Image {
    private int[] pixels;
    private int width, height;

    public Image(int[] pixels, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Incorrect dimensions");
        }

        if (pixels.length != width * height) {
            throw new IllegalArgumentException("Incorrect pixels's size");
        }

        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }

    public Image fastScale(int newWidth, int newHeight) {
        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("Incorrect dimensions");
        }

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

    private int checkChannelValue(int value) {
        if (value < 0) {
            return 0;
        }

        if (value > 255) {
            return 255;
        }

        return value;
    }

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

    public Image bicubicInterpolationScale(int newWidth, int newHeight) {
        return this;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
