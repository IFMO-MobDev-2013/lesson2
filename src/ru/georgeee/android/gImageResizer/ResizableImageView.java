package ru.georgeee.android.gImageResizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import ru.georgeee.android.gImageResizer.R;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 18.09.13
 * Time: 1:07
 * To change this template use File | Settings | File Templates.
 */
public class ResizableImageView extends View {

    public final static BitmapFactory.Options IMAGE_LOAD_OPTIONS;

    static {
        IMAGE_LOAD_OPTIONS = new BitmapFactory.Options();
        IMAGE_LOAD_OPTIONS.inScaled = false;
        IMAGE_LOAD_OPTIONS.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    public final static int NEW_WIDTH = 405;
    public final static int NEW_HEIGHT = 434;
    public final static float BRIGHTNESS_CORRECTION_FACTOR = 0.2f;

    GestureDetector gestureDetector;

    boolean fastMode = false;
    long fastBitmapGenerateTime;
    long qualityBitmapGenerateTime;
    Bitmap fastBitmap;
    Bitmap qualityBitmap;
    Bitmap origBitmap;
    Paint paint;

    {
        origBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source, IMAGE_LOAD_OPTIONS);

        fastBitmapGenerateTime = SystemClock.uptimeMillis();
        fastBitmap = getBitmap(origBitmap, true);
        fastBitmapGenerateTime = SystemClock.uptimeMillis() - fastBitmapGenerateTime;

        qualityBitmapGenerateTime = SystemClock.uptimeMillis();
        qualityBitmap = getBitmap(origBitmap, false);
        qualityBitmapGenerateTime = SystemClock.uptimeMillis() - qualityBitmapGenerateTime;

        paint = new Paint();
    }

    public ResizableImageView(Context context) {
        super(context);
        initGestureDetector(context);
    }

    private void initGestureDetector(Context context){
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) {
                fastMode = !fastMode;
                postInvalidate();
            }
        });
    }

    protected Bitmap getBitmap(Bitmap origBitmap, boolean fastMode) {
        int origWidth = origBitmap.getWidth();
        int origHeight = origBitmap.getHeight();
        int[] origPixels = new int[origWidth * origHeight];
        origBitmap.getPixels(origPixels, 0, origWidth, 0, 0, origWidth, origHeight);
        ImageManipulator manipulator = new ImageManipulator();
        manipulator.setPixels(origPixels, origWidth, origHeight);
        if (fastMode) {
            manipulator.fastScaleRotateBritenAtOnce(NEW_WIDTH, NEW_HEIGHT, BRIGHTNESS_CORRECTION_FACTOR);
        } else {
            manipulator.betterScale(NEW_WIDTH, NEW_HEIGHT);
            manipulator.rotateClockwise90();
            manipulator.changeBrightness(BRIGHTNESS_CORRECTION_FACTOR);
        }
        return Bitmap.createBitmap(manipulator.getPixels(), manipulator.getWidth(), manipulator.getHeight(), origBitmap.getConfig());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap;
        long generateTime;
        if (fastMode) {
            bitmap = fastBitmap;
            generateTime = fastBitmapGenerateTime;
        } else {
            bitmap = qualityBitmap;
            generateTime = qualityBitmapGenerateTime;
        }

        canvas.drawBitmap(bitmap, 0, 0, paint);

        paint.setARGB(127, 0, 0, 0);
        canvas.drawRect(0, 10, 350, 30, paint);
        paint.setARGB(255, 255, 255, 255);
        paint.setTextSize(20);
        canvas.drawText("Gen time: " + generateTime + "ms (fast mode=" + fastMode + ")", 0, 26, paint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

}
