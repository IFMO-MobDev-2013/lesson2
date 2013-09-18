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
public class BitmapUtils {

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

}
