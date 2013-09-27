package com.example.task2;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class ImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ImageView(this));
    }

    public class ImageView extends View {

        private static final int SOURCE_WIDTH = 700, SOURCE_HEIGHT = 750, DESTINATION_WIDTH = 434, DESTINATION_HEIGHT = 405;
        private static final double COEFFICIENT = 1.73, AREA = COEFFICIENT * COEFFICIENT;
        private Paint paint = new Paint();
        private int[] sourcePixels = new int[SOURCE_WIDTH * SOURCE_HEIGHT];
        private int[] destinationPixels = new int[DESTINATION_WIDTH * DESTINATION_HEIGHT];
        private boolean fast = true;
        private int imX, imY;

        public ImageView(Context context) {
            super(context);
            paint.setARGB(255, 255, 255, 255);
            paint.setTextSize(24);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sourcepng);
            bitmap.getPixels(sourcePixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            imX = Math.max(0, (size.x - DESTINATION_WIDTH) / 2);
            imY = Math.max(0, (size.y - DESTINATION_HEIGHT) / 2);
        }

        public void onDraw(Canvas canvas) {
            if (fast) {
                fast();
            } else {
                good();
            }
            canvas.drawBitmap(destinationPixels, 0, 434, imX, imY, 434, 405, false, null);
        }

        private void fast() {
            int i, j, sourceOffset, destinationOffset;
            int[] sourceReverseOffset = new int[DESTINATION_WIDTH];
            for (i = 0; i < DESTINATION_WIDTH; i++) {
                sourceReverseOffset[i] = 524300 - SOURCE_WIDTH * (int) (i * COEFFICIENT);
            }
            for (i = 0; i < DESTINATION_HEIGHT; i++) {
                sourceOffset = (int) (i * COEFFICIENT);
                destinationOffset = i * DESTINATION_WIDTH;
                for (j = 0; j < DESTINATION_WIDTH; j++) {
                    int color = sourcePixels[sourceReverseOffset[j] + sourceOffset];
                    for (int k = 0; k < 4; k++) {
                        int a = (color >>> k * 8) & 0xFF;
                        a = Math.min(255, a + 60) - a;
                        color += a << k * 8;
                    }
                    destinationPixels[destinationOffset + j] = color;
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (event.getX() >= imX && event.getX() <= imX + DESTINATION_WIDTH && event.getY() >= imY && event.getY() <= imY + DESTINATION_HEIGHT) {
                fast ^= true;
                invalidate();
            }
            return super.onTouchEvent(event);
        }

        public void good() {
            double[][][] channels = new double[SOURCE_HEIGHT][SOURCE_WIDTH][3];
            for (int i = 0; i < SOURCE_HEIGHT; i++) {
                int offset = i * SOURCE_WIDTH;
                for (int j = 0; j < SOURCE_WIDTH; j++) {
                    int pixel = sourcePixels[offset + j];
                    channels[i][j][0] = (pixel >>> 16) & 0xFF;
                    channels[i][j][1] = (pixel >>> 8) & 0xFF;
                    channels[i][j][2] = pixel & 0xFF;
                }
            }
            double[] color = new double[3];
            for (int i = 0; i < DESTINATION_WIDTH - 1; i++) {
                double xb = i * COEFFICIENT, xe = (i + 1) * COEFFICIENT;
                int xbi = (int) xb, xei = (int) xe;
                for (int j = 0; j < DESTINATION_HEIGHT - 1; j++) {
                    color[0] = color[1] = color[2] = 0;
                    double yb = j * COEFFICIENT, ye = (j + 1) * COEFFICIENT;
                    for (int k = xbi; k <= xei; k++) {
                        for (int l = (int) yb; l <= (int) ye; l++) {
                            double s = (Math.min(k + 1, xe) - Math.max(k, xb)) * (Math.min(l + 1, ye) - Math.max(l, yb));
                            color[0] += channels[k][l][0] * s;
                            color[1] += channels[k][l][1] * s;
                            color[2] += channels[k][l][2] * s;
                        }
                    }
                    color[0] /= AREA;
                    color[1] /= AREA;
                    color[2] /= AREA;
                    destinationPixels[DESTINATION_WIDTH * j + DESTINATION_WIDTH - 1 - i] = goodBright(color);
                }
            }
            for (int i = 0; i < DESTINATION_WIDTH - 1; i++) {
                color[0] = color[1] = color[2] = 0;
                double xb = i * COEFFICIENT, xe = (i + 1) * COEFFICIENT;
                for (int j = (int) xb; j <= (int) xe; j++) {
                    double length = Math.min(j + 1, xe) - Math.max(j, xb);
                    color[0] += channels[j][699][0] * length;
                    color[1] += channels[j][699][1] * length;
                    color[2] += channels[j][699][2] * length;
                }
                color[0] /= COEFFICIENT;
                color[1] /= COEFFICIENT;
                color[2] /= COEFFICIENT;
                destinationPixels[175769 - i] = goodBright(color);
            }
            for (int i = 0; i < DESTINATION_HEIGHT - 1; i++) {
                color[0] = color[1] = color[2] = 0;
                double xb = i * COEFFICIENT, xe = (i + 1) * COEFFICIENT;
                for (int j = (int) xb; j <= (int) xe; j++) {
                    double length = Math.min(j + 1, xe) - Math.max(j, xb);
                    color[0] += channels[749][j][0] * length;
                    color[1] += channels[749][j][1] * length;
                    color[2] += channels[749][j][2] * length;
                }
                color[0] /= COEFFICIENT;
                color[1] /= COEFFICIENT;
                color[2] /= COEFFICIENT;
                destinationPixels[DESTINATION_WIDTH * i] = goodBright(color);
            }
            destinationPixels[DESTINATION_WIDTH * DESTINATION_HEIGHT - 1] = goodBright(sourcePixels[524999]);
        }

        private int goodBright(double[] color) {
            float[] hsv = new float[3];
            Color.RGBToHSV((int) color[0], (int) color[1], (int) color[2], hsv);
            hsv[1] = Math.min(1, hsv[1] + (float) 0.15);
            hsv[2] = Math.min(1, hsv[2] * (float) 1.6);
            return Color.HSVToColor(hsv);
        }

        private int goodBright(int color) {
            float[] hsv = new float[3];
            Color.RGBToHSV((color >>> 16) & 0xFF, (color >>> 8) & 0xFF, color & 0xFF, hsv);
            hsv[1] = Math.min(1, hsv[1] + (float) 0.15);
            hsv[2] = Math.min(1, hsv[2] * (float) 1.6);
            return Color.HSVToColor(hsv);
        }
    }
}
