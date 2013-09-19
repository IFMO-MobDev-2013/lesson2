package ru.ifmo.mobdev.loboda.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
    }

    class MyView extends View {
        public static final int width = 700;
        public static final int height = 750;
        public static final int newWidth = 405;
        public static final int newHeight = 434;
        private boolean quality;
        private BitmapFactory.Options options;
        private Paint paint;
        private int[] colours;
        private int[] newColours;

        public MyView(Context context) {
            super(context);
            Bitmap bitmap;
            quality = true;
            paint = new Paint();
            paint.setARGB(255, 255, 255, 255);
            options = new BitmapFactory.Options();
            options.inScaled = false;
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source, options);
            colours = new int[width * height];
            newColours = new int[newWidth * newHeight];
            bitmap.getPixels(colours, 0, width, 0, 0, width, height);
        }

        @Override
        public void onDraw(Canvas canvas) {
            quality = !quality;
            long start = System.currentTimeMillis();
            if (quality) {
                for (int i = 0; i < newHeight; ++i) {
                    double topBound = i * 1.73;
                    double bottomBound = (i + 1) * 1.73;
                    for (int j = 0; j < newWidth; ++j) {
                        double red = 0;
                        double green = 0;
                        double blue = 0;
                        double alpha = 0;
                        double square = 0;
                        double leftBound = j * 1.73;
                        double rightBound = (j + 1) * 1.73;
                        for (int k = (int) (topBound); k <= (int) (bottomBound); ++k) {
                            if(k >= height){
                                break;
                            }
                            for (int h = (int) (leftBound); h <= (int) (rightBound); ++h) {
                                if (h < width) {
                                    double localSquare = (Math.min(h + 1, rightBound) - Math.max(h, leftBound)) * (Math.max(k + 1, bottomBound) - Math.min(k, topBound));
                                    square += localSquare;
                                    int colour = colours[k * width + h];
                                    blue += localSquare * ((colour << 24) >>> 24);
                                    green += localSquare * ((colour << 16) >>> 24);
                                    red += localSquare * ((colour << 8) >>> 24);
                                    alpha += localSquare * ((colour >>> 24));
                                }
                            }
                        }
                        red /= square;
                        green /= square;
                        blue /= square;
                        alpha /= square;
                        // Increasing the brightness(converting to YUV colour scheme)
                        int Y = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                        int U = (int) ((-0.14713) * red - 0.28886 * green + 0.436 * blue);
                        int V = (int) (0.615 * red - 0.51499 * green - 0.10001 * blue);
                        Y += 30;
                        red = Y + 1.13983 * V;
                        if (red > 0xFF) red = 0xFF;
                        green = Y - 0.39465 * U - 0.58060 * V;
                        if (green > 0xFF) green = 0xFF;
                        blue = Y + 2.03211 * U;
                        if (blue > 0xFF) blue = 0xFF;
                        newColours[j * newHeight + (newHeight - i - 1)] = (((int) alpha << 24) + ((int) red << 16) + ((int) green << 8) + ((int) blue));
                    }
                }
            } else {
                for (int i = 0; i < newHeight; ++i) {
                    for (int j = 0; j < newWidth; j++) {
                        // Fast increasing the brightness
                        int colour = colours[(int) (i * 1.73) * width + (int) (j * 1.73)];
                        int blue = (colour << 24) >>> 24;
                        int green = (colour << 16) >>> 24;
                        int red = (colour << 8) >>> 24;
                        blue += 30;
                        green += 30;
                        red += 30;
                        if(red > 0xFF){
                            red = 0xFF;
                        }
                        if(green > 0xFF){
                            green = 0xFF;
                        }
                        if(blue > 0xFF){
                            blue = 0xFF;
                        }
                        int alpha = colour >>> 24;
                        newColours[j * newHeight + (newHeight - i - 1)] = (((int) alpha << 24) + ((int) red << 16) + ((int) green << 8) + ((int) blue));
                    }
                }
            }
            canvas.drawBitmap(newColours, 0, newHeight, 0, 0, newHeight, newWidth, true, paint);
            if(quality){
                canvas.drawText(("Quality version: " + new Long(System.currentTimeMillis() - start)).toString() + "ms", 3, 420, paint);
            } else {
                canvas.drawText(("Fast version: " + new Long(System.currentTimeMillis() - start)).toString() + "ms.", 3, 420, paint);
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            invalidate();
            return super.onTouchEvent(event);
        }
    }
}