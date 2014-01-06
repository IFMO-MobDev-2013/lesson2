package ru.mihver1.android.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;


public class PictureActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    Bitmap originPicture;
    Bitmap fastPicture;
    Bitmap finePicture;

    int originPicturePixels[], originHeight, originWidth;

    private static final double RATIO = 1.73;

    class PictureView extends View {

        private int state = 0;
        Toast t;

        public PictureView(final Context context) {
            super(context);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    state++;
                    state %= 3;
                    invalidate();
                }
            });
            t = Toast.makeText(getContext(), R.string.origin, 1);
            t.show();
        }


        @Override
        protected void onDraw(Canvas canvas) {
            if(state == 0) {
                t.cancel();
                canvas.drawBitmap(originPicture, 0, 0, new Paint());
                t = Toast.makeText(getContext(), R.string.origin, 1);
                t.show();
            } else if(state == 1) {
                t.cancel();
                canvas.drawBitmap(fastPicture, 0, 0, new Paint());
                t = Toast.makeText(getContext(), R.string.fast, 1);
                t.show();
            } else if(state == 2) {
                t.cancel();
                canvas.drawBitmap(finePicture, 0, 0, new Paint());
                t = Toast.makeText(getContext(), R.string.fine, 1);
                t.show();
            }

        }
    }

    public void fastProcessing() {
        int newHeight = (int)(originHeight / RATIO);
        int newWidth = (int)(originWidth / RATIO);

        int newPixels[] = new int[newHeight * newWidth];
        int t = 0;
        for(int i = 0; i < newHeight; ++i) {
            for(int j = 0; j < newWidth; ++j, ++t) {
                if(t == newHeight * newWidth)
                    break;
                newPixels[t] = Color.argb(Color.alpha(originPicturePixels[(int)(i * RATIO) * originWidth + (int)(j * RATIO)]),
                                          Color.red(originPicturePixels[(int)(i * RATIO) * originWidth + (int)(j * RATIO)]),
                                          Color.green(originPicturePixels[(int)(i * RATIO) * originWidth + (int)(j * RATIO)]),
                                          Color.blue(originPicturePixels[(int)(i * RATIO) * originWidth + (int)(j * RATIO)]));
            }
        }
        fastPicture = Bitmap.createBitmap(newPixels, 0, newWidth, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }

    public void fineProcessing() {
        int newHeight = (int)(originHeight / RATIO);
        int newWidth = (int)(originWidth / RATIO);

        int alpha = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        int t = 0;
        int s = 0;

        int newPixels[] = new int[newHeight * newWidth];


        for(int i = 0; i < newHeight; i++) {
            for(int j = 0; j < newWidth; j++, s++) {
                alpha = 0;
                red = 0;
                green = 0;
                blue = 0;
                t = 0;
                for(int x = Math.max(0, (int)(i * RATIO) - 2); x < Math.min(originHeight, (int)(i * RATIO) + 2); ++x) {
                    for(int y = Math.max(0, (int)(j * RATIO) - 2); y < Math.min(originWidth, (int)(j * RATIO) + 2); ++y) {
                        alpha += Color.alpha(originPicturePixels[x * originWidth + y]);
                        red += Color.red(originPicturePixels[x * originWidth + y]);
                        green += Color.green(originPicturePixels[x * originWidth + y]);
                        blue += Color.blue(originPicturePixels[x * originWidth + y]);
                        t++;
                    }
                }
                newPixels[s] = Color.argb(alpha / t, red / t, green / t, blue / t);
            }
        }

        finePicture = Bitmap.createBitmap(newPixels, 0, newWidth, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }

    public void brighter() {
        int length = originHeight * originWidth;
        for(int i = 0; i < length; ++i) {
            originPicturePixels[i] = Color.argb(Color.alpha(originPicturePixels[i]), Math.min(255, Color.red(originPicturePixels[i]) * 2),
                    Math.min(255, Color.green(originPicturePixels[i]) * 2), Math.min(255, Color.blue(originPicturePixels[i]) * 2));
        }
    }

    public void rotate() {
        int buff[][] = new int[originWidth][originHeight];
        int length = originHeight * originWidth;

        for(int i = 0; i < length; ++i) {
            buff[i % originWidth][i / originWidth] = originPicturePixels[i];
        }
        int t = 0;
        for(int i = originWidth - 1; i >= 0; --i) {
            for(int j = 0; j < originHeight; ++j, ++t) {
                originPicturePixels[t] = buff[i][j];
            }
        }
        int temp = originHeight;
        originHeight = originWidth;
        originWidth = temp;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        originPicture = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.source);
        originHeight = originPicture.getHeight();
        originWidth = originPicture.getWidth();
        originPicturePixels = new int[originHeight * originWidth];
        originPicture.getPixels(originPicturePixels, 0, originWidth, 0, 0, originWidth, originHeight);

        brighter();
        rotate();
        rotate();
        rotate(); // Cheat

        fastProcessing();
        fineProcessing();

        View mainView = new PictureView(this);

        setContentView(mainView);
    }
}
