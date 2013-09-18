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
import com.polarnick.polaris.utils.graphics.ImageAsyncProcessingChain;
import com.polarnick.polaris.utils.graphics.ImageFastScaler;
import com.polarnick.polaris.utils.graphics.ImageProcessingBase;
import com.polarnick.polaris.utils.graphics.ImageRotator;

/**
 * Date: 16.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class MainActivity extends Activity {

    private static final int THREADS_COUNT = Runtime.getRuntime().availableProcessors() + 1;
    private static final double SCALE_FACTOR = 1 / 1.73;

    private final ImageProcessingBase imageRotator = new ImageRotator();
    private final ImageProcessingBase imageScaler = new ImageFastScaler(SCALE_FACTOR);

    private ImageView imageView;

    private int screenWidth;
    private int screenHeight;

    private Bitmap sourceImage;

    private boolean calculating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initScreenSize();
        initSourceImage();
        imageView = new ImageView(this, sourceImage, screenWidth, screenHeight);
        setContentView(imageView);
    }

    private void initScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void initSourceImage() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        sourceImage = BitmapFactory.decodeResource(getResources(), R.drawable.source_image, options);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !calculating) {
            calculating = true;
            new ImageAsyncProcessingChain(sourceImage, THREADS_COUNT).process(imageRotator).process(imageScaler)
                    .asyncExecute(new AsyncCallback<Bitmap>() {
                        @Override
                        public void onSuccess(final Bitmap resultImage) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.updateImage(resultImage);
                                    calculating = false;
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable reason) {
                            Log.e(this.getClass().getName(), "Image processing failed!", reason);
                        }
                    });
            return true;
        } else {
            return false;
        }
    }

}
