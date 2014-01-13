package ru.ifmo.nechaev;

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

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    Bitmap picture;
    int w, h;
    boolean renew = true, thatfirst = true;
    int[] intpic;
    int miniw, minih;
    int[] minipic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        picture = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        w = picture.getWidth();
        h = picture.getHeight();
        intpic = new int[w * h];
        miniw = (int) (w / 1.73) + 1;
        minih = (int) (h / 1.73) + 1;
        minipic = new int[miniw * minih];
        setContentView(new PictureRenew(this));
    }

    class PictureRenew extends View {
        PictureRenew(Context context) {
            super(context);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    renew = !renew;
                    invalidate();
                }
            });
        }

        public void slowCompres(Canvas canvas) {
            int x, y;
            int hh, ww;
            float t, u, tmp;
            float d1, d2, d3, d4;
            int p1, p2, p3, p4;

            int red, green, blue;

            for (y = 0; y < minih; y++) {
                tmp = (float) (y) / (float) (minih - 1) * (h - 1);
                hh = (int) Math.floor(tmp);
                if (hh < 0) {
                    hh = 0;
                } else {
                    if (hh >= h - 1)
                        hh = h - 2;
                }
                u = tmp - hh;

                for (x = 0; x < miniw; x++) {
                    tmp = (float) (x) / (float) (miniw - 1) * (w - 1);
                    ww = (int) Math.floor(tmp);
                    if (ww < 0) {
                        ww = 0;
                    } else {
                        if (ww >= w - 1)
                            ww = w - 2;
                    }
                    t = tmp - ww;

                    d1 = (1 - t) * (1 - u);
                    d2 = t * (1 - u);
                    d3 = t * u;
                    d4 = (1 - t) * u;

                    p1 = intpic[hh * w + ww];
                    p2 = intpic[hh * w + ww + 1];
                    p3 = intpic[(hh + 1) * w + ww + 1];
                    p4 = intpic[(hh + 1) * w + ww];
                    blue = ((int) ((p1 & 0xff0000) * d1 + (p2 & 0xff0000) * d2 + (p3 & 0xff0000) * d3 + (p4 & 0xff0000) * d4)) & 0xff0000;
                    green = ((int) ((p1 & 0x00ff00) * d1 + (p2 & 0x00ff00) * d2 + (p3 & 0x00ff00) * d3 + (p4 & 0x00ff00) * d4)) & 0x00ff00;
                    red = ((int) ((p1 & 0x0000ff) * d1 + (p2 & 0x0000ff) * d2 + (p3 & 0x0000ff) * d3 + (p4 & 0x0000ff) * d4)) & 0x0000ff;

                    minipic[y * miniw + x] = red | green | blue;
                }
            }
            canvas.drawBitmap(minipic, 0, miniw, 0, 0, miniw, minih, false, null);
        }

        public void fastCompres(Canvas canvas) {
            int xScale = (w << 16) / miniw;
            int yScale = (h << 16) / minih;

            for (int color = 0, x = 0, y = 0; color < minipic.length; ++color, ++x) {
                if (x == miniw) {
                    x = 0;
                    ++y;
                }
                minipic[color] = intpic[((yScale * y) >> 16) * w + ((xScale * x) >> 16)];
            }
            canvas.drawBitmap(minipic, 0, miniw, 0, 0, miniw, minih, false, null);
        }

        private void LightAndRotate() {
            int[] tmp = new int[w * h];
            picture.getPixels(intpic, 0, w, 0, 0, w, h);
            int r, g, b;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int t = y * w + x;
                    r = intpic[t] & 0x00FF0000;
                    g = intpic[t] & 0x0000FF00;
                    b = intpic[t] & 0x000000FF;

                    r = r >> 16;
                    g = g >> 8;

                    r = (r * 1.5 <= 255) ? (int) (r * 1.5) : 255;
                    g = (g * 1.5 <= 255) ? (int) (g * 1.5) : 255;
                    b = (b * 1.5 <= 255) ? (int) (b * 1.5) : 255;
                    tmp[x * w + h - y - 1] = 0xFF000000 | r << 16 | g << 8 | b;
                }
            }

            intpic = tmp;
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(minipic, 0, miniw, 0, 0, miniw, minih, false, null);
            if (thatfirst) {
                LightAndRotate();
                thatfirst = !thatfirst;
            }

            if (renew) {
                fastCompres(canvas);
            } else {
                slowCompres(canvas);
            }
        }
    }
}