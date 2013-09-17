package com.polarnick.polaris.utils;

import android.graphics.Bitmap;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Date: 17.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class BitmapUtils {

    public static byte[] convertToByteArray(Bitmap image) {
        byte[] res = new byte[image.getHeight() * image.getWidth() * 4];
        Buffer sourceBuffer = ByteBuffer.wrap(res);
        image.copyPixelsToBuffer(sourceBuffer);
        return res;
    }

    public static Bitmap createBitmapFromBytes(byte[] buffer, int width, int height) {
        Bitmap res = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        res.copyPixelsFromBuffer(ByteBuffer.wrap(buffer));
        return res;
    }

}
