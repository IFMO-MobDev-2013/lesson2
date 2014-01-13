package com.example.hw2;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;

public class MyActivity extends Activity {

    public static final int WIDTH = 700;
    public static final int HEIGHT = 750;
    public static final int NEW_WIDTH = 405;
    public static final int NEW_HEIGHT = 434;
    public static final float RATIO = 1.73f;
    Bitmap picture;
    int[] pixels = new int[WIDTH * HEIGHT];
    int[] new_pixels = new int[NEW_WIDTH * NEW_HEIGHT];
    boolean quality = false;
    boolean rotated = false;
    Paint forText = new Paint();

    public class Picture extends View {

        Picture(Context context) {
            super(context);
            picture.getPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
            forText.setColor(Color.RED);
            forText.setTextSize(25);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                        shrinkPicture(quality);
                        quality = !quality;
                        invalidate();
                }
            });
        }

        public void increaseBrightness() {
            int[] temp = new int[WIDTH * HEIGHT];
            int a, r, b, g;
            for (int i = 0; i < pixels.length; i++) {
                a = (pixels[i] >> 24) & 0x000000FF;
                r = (pixels[i] >> 16) & 0x000000FF;
                g = (pixels[i] >> 8) & 0x000000FF;
                b = (pixels[i] & 0x000000FF);
                r = Math.min(255, r * 2);
                g = Math.min(255, g * 2);
                b = Math.min(255, b * 2);
                temp[i] = (a << 24) | (r << 16) | (g << 8) | b;
            }
            pixels = temp;
        }

        public void rotatePicture() {
            int[] temp = new int[WIDTH * HEIGHT];
            for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    temp[j * HEIGHT + HEIGHT - i - 1] = pixels[i * WIDTH + j];
                }
            }
            pixels = temp;
        }

        public void shrinkPicture(boolean quality) {
            if (quality) {
                int x, y, p0, p1, p2, p3, a, r, g, b;
                float d0, d1, d2, d3, t, u;
                for (int i = 0; i < NEW_WIDTH; i++) {
                    for (int j = 0; j < NEW_HEIGHT; j++) {
                        x = (int) (RATIO * i);
                        if (x < 0) {
                            x = 0;
                        } else if (x >= WIDTH - 1) {
                            x = WIDTH - 2;
                        }
                        y = (int) (RATIO * j);
                        if (y < 0) {
                            y = 0;
                        } else if (y >= HEIGHT - 1) {
                            y = HEIGHT - 2;
                        }
                        p0 = pixels[x * HEIGHT + y];
                        p1 = pixels[x * HEIGHT + y + 1];
                        p2 = pixels[(x + 1) * HEIGHT + y + 1];
                        p3 = pixels[(x + 1) * HEIGHT + y];
                        t = RATIO * j - y;
                        u = RATIO * i - x;
                        d0 = (1 - t) * (1 - u);
                        d1 = t * (1 - u);
                        d2 = u * t;
                        d3 = u * (1 - t);

                        a = (int) (d0 * ((p0 >> 24) & 0xFF) + d1 * ((p1 >> 24) & 0xFF) + d2 * ((p2 >> 24) & 0xFF) + d3 * ((p3 >> 24) & 0xFF));
                        r = (int) (d0 * ((p0 >> 16) & 0xFF) + d1 * ((p1 >> 16) & 0xFF) + d2 * ((p2 >> 16) & 0xFF) + d3 * ((p3 >> 16) & 0xFF));
                        g = (int) (d0 * ((p0 >> 8) & 0xFF) + d1 * ((p1 >> 8) & 0xFF) + d2 * ((p2 >> 8) & 0xFF) + d3 * ((p3 >> 8) & 0xFF));
                        b = (int) (d0 * (p0 & 0xFF) + d1 * (p1 & 0xFF) + d2 * (p2 & 0xFF) + d3 * (p3 & 0xFF));

                        new_pixels[i * NEW_HEIGHT + j] = Color.argb(a, r, g, b);
                    }
                }
            } else {
                for (int i = 0; i < NEW_WIDTH; i++) {
                    for (int j = 0; j < NEW_HEIGHT; j++) {
                            new_pixels[NEW_HEIGHT * i + j] = pixels[(int)(j * RATIO) + (int)(i * RATIO) * HEIGHT];
                    }
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (!rotated) {
                canvas.drawBitmap(pixels, 0, WIDTH, 0, 0 , WIDTH, HEIGHT, false, null);
                canvas.drawText("Original picture", 50, 50, forText);
                rotated = true;
                rotatePicture();
                increaseBrightness();
            } else {
                canvas.drawBitmap(new_pixels, 0, NEW_HEIGHT, 0, 0, NEW_HEIGHT, NEW_WIDTH, false, null);
                if (quality) {
                    canvas.drawText("Compressed using high quality algorithm", 50, 50, forText);
                } else {
                    canvas.drawText("Compressed using fast algorithm", 50, 50, forText);
                }
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picture = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        setContentView(new Picture(this));

    }

}
