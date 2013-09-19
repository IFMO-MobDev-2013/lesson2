package com.polarnick.polaris.utils.graphics;

import android.graphics.Bitmap;
import com.google.common.base.Preconditions;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Date: 17.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ImageBitmapUtils {

    public static final int RED_INDEX = 0;
    public static final int GREEN_INDEX = 1;
    public static final int BLUE_INDEX = 2;

    public static final int HUE_INDEX = 0;
    public static final int SATURATION_INDEX = 1;
    public static final int VALUE_INDEX = 2;

    public static byte[] convertToByteArray(Bitmap image) {
        byte[] res = new byte[image.getHeight() * image.getWidth() * 4];
        Buffer sourceBuffer = ByteBuffer.wrap(res);
        image.copyPixelsToBuffer(sourceBuffer);
        return res;
    }

    public static Bitmap createBitmapFromBytes(byte[] buffer, int width, int height) {
        Preconditions.checkArgument(buffer.length == 4 * width * height, "Buffer contains bigger image, than given size!");
        Bitmap res = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        res.copyPixelsFromBuffer(ByteBuffer.wrap(buffer));
        return res;
    }

    /**
     * Converts color represented in RGB to HSV representation.
     *
     * @param rgb color in RGB format(Red, Green, Blue).<p/>
     *            <b>rgb[0]</b> - red component (if <b>rgb[0] = 0</b> - there are no red component,
     *            if <b>rgb[0] = -1</b> - red component is maximum)<p/>
     *            <b>rgb[1]</b> - blue component<p/>
     *            <b>rgb[2]</b> - green component<p/>
     * @param hsv color in HSV format(Hue, Saturation, Value).<p/>
     *            <b>hsv[0]</b> - Hue component, contains in <b>[0.0, 360.0)</b><p/>
     *            <b>hsv[1]</b> - Saturation component, contains in <b>[0.0, 1.0]</b><p/>
     *            <b>hsv[2]</b> - Value component, contains in <b>[0.0, 1.0]<p/>
     * @see <a href="http://en.wikipedia.org/wiki/HSL_and_HSV">Article on wikipedia</a>
     * @see #convertHSVToRGB(double[], byte[])
     */
    public static void convertRGBToHSV(byte[] rgb, double[] hsv) {
        Preconditions.checkArgument(rgb.length == 3, "RGBA representaition must contain three components: " +
                "Red, Green, Blue.");
        Preconditions.checkArgument(hsv.length == 3, "HSV representaition must contain three components: " +
                "Hue, Saturation, Value.");
        int red = rgb[RED_INDEX];
        if (red < 0) {
            red += 256;
        }
        int green = rgb[GREEN_INDEX];
        if (green < 0) {
            green += 256;
        }
        int blue = rgb[BLUE_INDEX];
        if (blue < 0) {
            blue += 256;
        }
        int max = Math.max(Math.max(red, green), blue);
        int min = Math.min(Math.min(red, green), blue);

        if (max == min) {
            hsv[HUE_INDEX] = 0;
        } else if (max == red && green >= blue) {
            hsv[HUE_INDEX] = 60.0 * (green - blue) / (max - min);
        } else if (max == red && green < blue) {
            hsv[HUE_INDEX] = 60.0 * (green - blue) / (max - min) + 360;
        } else if (max == green) {
            hsv[HUE_INDEX] = 60.0 * (blue - red) / (max - min) + 120;
        } else if (max == blue) {
            hsv[HUE_INDEX] = 60.0 * (red - green) / (max - min) + 240;
        }

        if (max == 0) {
            hsv[SATURATION_INDEX] = 0;
        } else {
            hsv[SATURATION_INDEX] = 1.0 - 1.0 * min / max;
        }

        hsv[VALUE_INDEX] = 1.0 * max / 256;
    }

    /**
     * Converts color represented in HSV to RGBA representation.
     *
     * @param hsv color in HSV format(Hue, Saturation, Value).<p/>
     *            <b>hsv[0]</b> - Hue component, contains in <b>[0.0, 360.0)</b><p/>
     *            <b>hsv[1]</b> - Saturation component, contains in <b>[0.0, 1.0]</b><p/>
     *            <b>hsv[2]</b> - Value component, contains in <b>[0.0, 1.0]</b><p/>
     * @param rgb color in RGB format(Red, Green, Blue).<p/>
     *            <b>rgb[0]</b> - red component (if <b>rgb[0] = 0</b> - there are no red component,
     *            if <b>rgb[0] = -1</b> - red component is maximum)<p/>
     *            <b>rgb[1]</b> - blue component<p/>
     *            <b>rgb[2]</b> - green component<p/>
     * @see <a href="http://en.wikipedia.org/wiki/HSL_and_HSV">Article on wikipedia</a>
     * @see #convertRGBToHSV(byte[], double[])
     */
    public static void convertHSVToRGB(double[] hsv, byte[] rgb) {
        Preconditions.checkArgument(hsv.length == 3, "HSV representaition must contain three components: " +
                "Hue, Saturation, Value.");
        Preconditions.checkArgument(rgb.length == 3, "RGB representaition must contain three components: " +
                "Red, Green, Blue.");
        Preconditions.checkArgument(0 <= hsv[HUE_INDEX] && hsv[HUE_INDEX] < 360, "Hue component must be in range [0.0, 360.0)");

        int hi = (int) (hsv[HUE_INDEX] / 60);
        double vMin = (1.0 - hsv[SATURATION_INDEX]) * hsv[VALUE_INDEX] * 100.0;
        double a = (hsv[VALUE_INDEX] * 100.0 - vMin) * (hsv[HUE_INDEX] * 100 % 60) / 60;
        double vInc = vMin + a;
        double vDec = hsv[VALUE_INDEX] * 100.0 - a;
        int red;
        int green;
        int blue;
        final int resV = (int) (hsv[VALUE_INDEX] * 255.0);
        final int resVInc = (int) (vInc * 2.55);
        final int resVMin = (int) (vMin * 2.55);
        final int resVDec = (int) (vDec * 2.55);
        if (hi == 0) {
            red = resV;
            green = resVInc;
            blue = resVMin;
        } else if (hi == 1) {
            red = resVDec;
            green = resV;
            blue = resVMin;
        } else if (hi == 2) {
            red = resVMin;
            green = resV;
            blue = resVInc;
        } else if (hi == 3) {
            red = resVMin;
            green = resVDec;
            blue = resV;
        } else if (hi == 4) {
            red = resVInc;
            green = resVMin;
            blue = resV;
        } else if (hi == 5) {
            red = resV;
            green = resVMin;
            blue = resVDec;
        } else {
            throw new IllegalStateException();
        }

        rgb[RED_INDEX] = (byte) red;
        rgb[GREEN_INDEX] = (byte) green;
        rgb[BLUE_INDEX] = (byte) blue;
    }

}
