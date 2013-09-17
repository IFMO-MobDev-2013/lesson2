package com.polarnick.day02;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import com.polarnick.polaris.concurrency.AsyncCallback;
import com.polarnick.polaris.utils.ImageRotateProcessor;
import com.polarnick.polaris.utils.ImageScaleProcessor;

/**
 * Date: 16.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class MainActivity extends Activity {

    private static final int THREADS_COUNT = Runtime.getRuntime().availableProcessors() + 1;

    private final ImageRotateProcessor imageRotateProcessor = new ImageRotateProcessor(THREADS_COUNT);
    private final ImageScaleProcessor imageScaleProcessor = new ImageScaleProcessor(THREADS_COUNT);

    private ImageView imageView;

    private int screenWidth;
    private int screenHeight;
    private Bitmap sourceImage;
    private Bitmap rotatedSourceImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initScreenSize();
        initSourceImage();
        imageView = new ImageView(this, sourceImage, screenWidth, screenHeight);
        setContentView(imageView);
        initRotatedSourceImage();
    }

    private void initScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void initSourceImage() {
        sourceImage = BitmapFactory.decodeResource(getResources(), R.drawable.source_image);
    }

    private void initRotatedSourceImage() {
        imageRotateProcessor.rotateBy90(sourceImage, new AsyncCallback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                rotatedSourceImage = bitmap;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.updateImage(rotatedSourceImage);
                    }
                });
            }

            @Override
            public void onFailure(Throwable reason) {
                Log.e(this.getClass().getName(), "Image rotating failed!", reason);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            imageScaleProcessor.scale(rotatedSourceImage, 0.5, null,
                    new AsyncCallback<Bitmap>() {
                        @Override
                        public void onSuccess(final Bitmap bitmap) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.updateImage(bitmap);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable reason) {
                            Log.e(this.getClass().getName(), "Image scaling failed!", reason);
                        }
                    }
            );
            return true;
        } else {
            return false;
        }
    }

}
