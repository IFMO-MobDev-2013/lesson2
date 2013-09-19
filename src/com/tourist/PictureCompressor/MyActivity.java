package com.tourist.PictureCompressor;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class MyActivity extends Activity {

    class MyView extends View {
        public static final int W = 700;
        public static final int H = 750;
        public static final int NEW_W = 405;
        public static final int NEW_H = 434;

        public MyView(Context context) {
            super(context);
            getPicture();
        }

        int[] pic = new int[H * W];

        void getPicture() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            bitmap.getPixels(pic, 0, W, 0, 0, W, H);
        }

        void makeColorsBrighter(int[] x) {
            for (int i = 0; i < x.length; i++) {
                int red = (x[i] & 0xFF0000) >> 16;
                int green = (x[i] & 0xFF00) >> 8;
                int blue = x[i] & 0xFF;
                int value = Math.max(red, Math.max(green, blue));
                double multiplier  = (value < 64) ? 2.0 : (1.0 / Math.sqrt(value / 255.0));
                x[i] = ((int)(red * multiplier) << 16) + ((int)(green * multiplier) << 8) + (int)(blue * multiplier);
            }
        }

        int[] tempPic = new int[NEW_H * NEW_W];

        void rotatePicture(int[] pic, int h, int w) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    tempPic[j * h + (h - i - 1)] = pic[i * w + j];
                }
            }
            System.arraycopy(tempPic, 0, pic, 0, h * w);
        }

        int[] compressFaster(int[] pic) {
            int[] newPic = new int[NEW_H * NEW_W];
            for (int i = 0; i < NEW_H; i++) {
                for (int j = 0; j < NEW_W; j++) {
                    newPic[i * NEW_W + j] = pic[(i * (H - 1) / (NEW_H - 1)) * W + (j * (W - 1) / (NEW_W - 1))];
                }
            }
            return newPic;
        }

        int averageColor(int[] pic, int from, int to, int step) {
            int sumRed = 0;
            int sumGreen = 0;
            int sumBlue = 0;
            for (int k = from; k <= to; k += step) {
                int color = pic[k];
                sumRed += (color & 0xFF0000) >> 16;
                sumGreen += (color & 0xFF00) >> 8;
                sumBlue += color & 0xFF;
            }
            int count = (to - from) / step + 1;
            int red = sumRed / count;
            int green = sumGreen / count;
            int blue = sumBlue / count;
            return (red << 16) + (green << 8) + blue;
        }

        int[] compressBetter(int[] pic) {
            int[] tempPic = new int[H * NEW_W];
            {
                double fatness = (double)(W - 1) / (NEW_W - 1);
                for (int i = 0; i < H; i++) {
                    for (int j = 0; j < NEW_W; j++) {
                        int from = Math.max(0, (int)(0.5 + Math.ceil((j - 0.5) * fatness)));
                        int to = Math.min(W - 1, (int)(0.5 + Math.floor((j + 0.5) * fatness)));
                        tempPic[i * NEW_W + j] = averageColor(pic, i * W + from, i * W + to, 1);
                    }
                }
            }
            int[] newPic = new int[NEW_H * NEW_W];
            {
                double fatness = (double)(H - 1) / (NEW_H - 1);
                for (int j = 0; j < NEW_W; j++) {
                    for (int i = 0; i < NEW_H; i++) {
                        int from = Math.max(0, (int)(0.5 + Math.ceil((i - 0.5) * fatness)));
                        int to = Math.min(H - 1, (int)(0.5 + Math.floor((i + 0.5) * fatness)));
                        newPic[i * NEW_W + j] = averageColor(tempPic, from * NEW_W + j, to * NEW_W + j, NEW_W);
                    }
                }
            }
            return newPic;
        }

        boolean faster = true;
        boolean isFast = true;
        boolean updating = false;
        int[] newPic = null;

        void updatePicture() {
            if (updating) {
                return;
            }
            updating = true;
            newPic = faster ? compressFaster(pic) : compressBetter(pic);
            rotatePicture(newPic, NEW_H, NEW_W);
            makeColorsBrighter(newPic);
            isFast = faster;
            updating = false;
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (newPic == null) {
                updatePicture();
            }
            Display display = getWindowManager().getDefaultDisplay();
            Point p = new Point();
            display.getSize(p);
            int left = (p.x - NEW_H) / 2;
            int top = (p.y - NEW_W) / 2;
            canvas.drawBitmap(newPic, 0, NEW_H, left, top, NEW_H, NEW_W, false, null);
            Paint paint = new Paint();
            paint.setColor(0xFFFFFFFF);
            paint.setTextSize(40);
            canvas.drawText((isFast ? "Fast" : "Good") + " compression", left, top / 2, paint);
            paint.setTextSize(20);
            canvas.drawText("Click to change", left, top / 2 + 30, paint);
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                faster = !faster;
                updatePicture();
            }
            return true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
    }
}
