package main.java.com.ifmomd.lesson2;


import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 19.09.13
 * Time: 0:06
 * To change this template use File | Settings | File Templates.
 */

public class ImageChange {

    private int[] imagePixels;
    private int width, height;

    public ImageChange(int[] imagePixels, int width, int height) {
        this.imagePixels = imagePixels;
        this.width = width;
        this.height = height;
    }

    public int[] getPixels() {
        return imagePixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImageChange fastScale(int newWidth, int newHeight) {
        int[] temp = new int[newWidth * newHeight];
        int x_ratio = (int) ((width << 16) / newWidth) + 1;
        int y_ratio = (int) ((height << 16) / newHeight) + 1;
        int x, y;
        for (int i = 0; i < newHeight; i++)
            for (int j = 0; j < newWidth; j++) {
                x = ((j * x_ratio) >> 16);
                y = ((i * y_ratio) >> 16);
                temp[(i * newWidth) + j] = imagePixels[(y * width) + x];
            }

//        Log.d("debug", "\n fast scale "+ newWidth + " " + newHeight);
        return new ImageChange(temp, newWidth, newHeight);
    }

    public ImageChange BilinearAlgorithm(int newWidth, int newHeight) {
        int[] temp = new int[newWidth * newHeight];
        int a, b, c, d, x, y, index;
        float x_ratio = ((float) (width - 1)) / newWidth;
        float y_ratio = ((float) (height - 1)) / newHeight;
        float x_diff, y_diff, blue, red, green;
        int offset = 0;
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                x = (int) (x_ratio * j);
                y = (int) (y_ratio * i);
                x_diff = (x_ratio * j) - x;
                y_diff = (y_ratio * i) - y;
                index = (y * width + x);
                a = imagePixels[index];
                b = imagePixels[index + 1];
                c = imagePixels[index + width];
                d = imagePixels[index + width + 1];

                // blue element
                // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                blue = (a & 0xff) * (1 - x_diff) * (1 - y_diff) + (b & 0xff) * (x_diff) * (1 - y_diff) +
                        (c & 0xff) * (y_diff) * (1 - x_diff) + (d & 0xff) * (x_diff * y_diff);

                // green element
                // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                green = ((a >> 8) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 8) & 0xff) * (x_diff) * (1 - y_diff) +
                        ((c >> 8) & 0xff) * (y_diff) * (1 - x_diff) + ((d >> 8) & 0xff) * (x_diff * y_diff);

                // red element
                // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                red = ((a >> 16) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 16) & 0xff) * (x_diff) * (1 - y_diff) +
                        ((c >> 16) & 0xff) * (y_diff) * (1 - x_diff) + ((d >> 16) & 0xff) * (x_diff * y_diff);

                temp[offset++] =
                        0xff000000 | // hardcode alpha
                                ((((int) red) << 16) & 0xff0000) |
                                ((((int) green) << 8) & 0xff00) |
                                ((int) blue);
            }
        }
//        Log.d("dgdgdg", "\n good scale "+ newWidth + " " + newHeight);
        return new ImageChange(temp, newWidth, newHeight);
    }

    public ImageChange rotate() {
        int[] temp = new int[width * height];

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                temp[x * height + (height - y - 1)] = imagePixels[y * width + x];
        return new ImageChange(temp, height, width);
    }

    public ImageChange increaseBrightness() {
        for (int i = 0; i < width * height; i++) {
            int curPixel = imagePixels[i];

            float red = (float) (curPixel & 0x0000ff);
            float green = (float) ((curPixel & 0x00ff00) >> 8);
            float blue = (float) ((curPixel & 0xff0000) >> 16);

            red = ((255 - red) * 0.4f + red);
            green = ((255 - green) * 0.4f + green);
            blue = ((255 - blue) * 0.4f + blue);

            imagePixels[i] = ((int) red) | (((int) green) << 8) | (((int) blue) << 16);
        }
        return new ImageChange(imagePixels, width, height);
    }
}
