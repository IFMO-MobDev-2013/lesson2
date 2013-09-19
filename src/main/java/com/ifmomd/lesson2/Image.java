package main.java.com.ifmomd.lesson2;

/*
    material : http://developer.android.com/reference/android/graphics/BitmapFactory.Options.html#inPreferredConfig

*/

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 18.09.13
 * Time: 20:43
 * To change this template use File | Settings | File Templates.
 */

public class Image extends View {

    public static final int NEW_WIDTH = 405;
    public static final int NEW_HEIGHT = 434;
    private BitmapFactory.Options bitmapOptions = null;
    private Bitmap bitmapImage;
    private int[] imagePixels;
    ImageChange imageChange, printImage, fastScaleImage, qualityScaleImage;
    boolean curState = false;
    String curScale = "default image, time = ";
    long fastScaleTime, qualityScaleTime, printTime;
    Paint paint;

    public Image(Context context) {
        super(context);

        //create options for using bitmap
        bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
        bitmapOptions.inDither = false;

        //get the image from resources
        bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.source, bitmapOptions);
        imagePixels = new int[bitmapImage.getWidth() * bitmapImage.getHeight()];
        bitmapImage.getPixels(imagePixels, 0, bitmapImage.getWidth(), 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight());

        imageChange = new ImageChange(imagePixels, bitmapImage.getWidth(), bitmapImage.getHeight());
        printImage = imageChange;

        imageChange = imageChange.rotate();
        imageChange = imageChange.increaseBrightness();

        fastScaleTime = SystemClock.uptimeMillis();        // count the time of fast scale
        fastScaleImage = imageChange.fastScale(NEW_WIDTH, NEW_WIDTH);
        fastScaleTime = SystemClock.uptimeMillis() - fastScaleTime;

        qualityScaleTime = SystemClock.uptimeMillis();     //count the time of quality scale
        qualityScaleImage = imageChange.BilinearAlgorithm(NEW_WIDTH, NEW_HEIGHT);
        qualityScaleTime = SystemClock.uptimeMillis() - qualityScaleTime;


        paint = new Paint();
        paint.setTextSize(30);
        paint.setARGB(255, 255, 255, 255);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!curState) {

                    printImage = fastScaleImage;
                    printTime = fastScaleTime;
                    curState = true;
                    curScale = "fast scale, time = ";
                } else {
                    printImage = qualityScaleImage;
                    printTime = qualityScaleTime;

                    curState = false;
                    curScale = "quality scale, time = ";
                }
                postInvalidate();
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = (canvas.getWidth() - printImage.getWidth()) / 2;             // to print in center
        int y = (canvas.getHeight() - printImage.getHeight()) / 2;
        canvas.drawBitmap(printImage.getPixels(), 0, printImage.getWidth(), x, y, printImage.getWidth(), printImage.getHeight(), false, null);
        canvas.drawText(curScale + printTime, 0, 40, paint);
    }
}
