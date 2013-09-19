package ru.ifmo.smelik.PictureRotate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;

public class RotateActivity extends Activity {

    Bitmap pic;
    int width, height;
    boolean isFast = false, first = true;

    class PictureRotate extends View {

        PictureRotate(Context context) {
            super(context);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFast = !isFast;
                    invalidate();
                    }
                });
        }

        int[] pixels = new int[width * height];
        int newWidth = (int)(width / 1.73), newHeight = (int)(height / 1.73);
        int[] newPixels = new int[newHeight * newWidth];

        private void rotateAndIncreaseBrightness()
        {
            int[] tmpPixels = new int[width * height];
            pic.getPixels(pixels, 0, width, 0, 0, width, height);
            float[] hsv = new float[3];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color.colorToHSV(pixels[y * width + x], hsv);
                    if (hsv[2] >= 0.5)
                        hsv[2] = 1;
                    else
                        hsv[2] *= 2;
                    tmpPixels[x * width + height - y - 1] = Color.HSVToColor(hsv);
                }
            }
            pixels = tmpPixels;
        }

        private void bilinearInterpolation() {
            int x, y;
            int h, w;
            float t, u, tmp;
            float d1, d2, d3, d4;
            int p1, p2, p3, p4;

            int red, green, blue;

            for (y = 0; y < newHeight; y++) {
                tmp = (float)(y) / (float)(newHeight - 1) * (height - 1);
                h = (int) Math.floor(tmp);
                if (h < 0) {
                    h = 0;
                } else {
                    if (h >= height - 1)
                        h = height - 2;
                }
                u = tmp - h;

                for (x = 0; x < newWidth; x++) {
                    tmp = (float)(x) / (float)(newWidth - 1) * (width - 1);
                    w = (int) Math.floor(tmp);
                    if (w < 0) {
                        w = 0;
                    } else {
                        if (w >= width - 1)
                            w = width - 2;
                    }
                    t = tmp - w;

                    d1 = (1 - t) * (1 - u);
                    d2 = t * (1 - u);
                    d3 = t * u;
                    d4 = (1 - t) * u;

                    p1 = pixels[h * width + w];
                    p2 = pixels[h * width + w + 1];
                    p3 = pixels[(h + 1) * width + w + 1];
                    p4 = pixels[(h + 1) * width + w];

                    blue = ((int) ((p1 & 0xff0000) * d1 + (p2 & 0xff0000) * d2 + (p3 & 0xff0000) * d3 + (p4 & 0xff0000) * d4)) & 0xff0000;
                    green = ((int) ((p1 & 0x00ff00) * d1 + (p2 & 0x00ff00) * d2 + (p3 & 0x00ff00) * d3 + (p4 & 0x00ff00) * d4)) & 0x00ff00;
                    red = ((int) ((p1 & 0x0000ff) * d1 + (p2 & 0x0000ff) * d2 + (p3 & 0x0000ff) * d3 + (p4 & 0x0000ff) * d4)) & 0x0000ff;

                    newPixels[y * newWidth + x] = red | green | blue;
                }
            }

        }

        public void doFastCompression(Canvas canvas) {
            int xScale = (width << 16) / newWidth;
            int yScale = (height << 16) / newHeight;

            for (int color = 0, x = 0, y = 0; color < newPixels.length; ++color, ++x) {
                if (x == newWidth) {
                    x = 0;
                    ++y;
                }
                newPixels[color] = pixels[((yScale * y) >> 16) * width + ((xScale * x) >> 16)];
            }

            canvas.drawBitmap(newPixels, 0, newWidth, 0, 0, newWidth, newHeight, false, null);
        }

        public void doNormalCompression(Canvas canvas) {
            bilinearInterpolation();
            canvas.drawBitmap(newPixels, 0, newWidth, 0, 0, newWidth, newHeight, false, null);
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (first) {
                rotateAndIncreaseBrightness();
                first = !first;
            }

            if (isFast) {
                doFastCompression(canvas);
            } else {
                doNormalCompression(canvas);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pic = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        width = pic.getWidth();
        height = pic.getHeight();

        setContentView(new PictureRotate(this));
    }
}
