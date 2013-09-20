package com.ifmomd.lesson2;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final double RATE = 1.37;

    private Bitmap getRoughImage() {
        long time = System.currentTimeMillis();
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        int[] pixels = new int[src.getWidth() * src.getHeight()];
        src.getPixels(pixels, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
        int rWidth = (int) (src.getWidth() / RATE);
        int rHeight = (int) (src.getHeight() / RATE);
        Bitmap result = Bitmap.createBitmap(
                ImageProcessor.increaseBrightness(
                        ImageProcessor.turnClockwise(
                                ImageProcessor.roughDownscale(pixels, src.getWidth(), RATE), rWidth), rHeight), rHeight, rWidth, Bitmap.Config.ARGB_8888);
        Toast.makeText(this, "Rough transform: " + (System.currentTimeMillis() - time) + " ms", Toast.LENGTH_SHORT).show();
        return result;
    }

    private Bitmap getFineImage() {
        long time = System.currentTimeMillis();
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        int[] pixels = new int[src.getWidth() * src.getHeight()];
        src.getPixels(pixels, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
        int rWidth = (int) (src.getWidth() / RATE);
        int rHeight = (int) (src.getHeight() / RATE);
        Bitmap result = Bitmap.createBitmap(
                ImageProcessor.increaseBrightness(
                        ImageProcessor.turnClockwise(
                                ImageProcessor.fineDownscale(pixels, src.getWidth(), RATE), rWidth), rHeight), rHeight, rWidth, Bitmap.Config.ARGB_8888);
        Toast.makeText(this, "Fine transform: " + (System.currentTimeMillis() - time) + " ms", Toast.LENGTH_SHORT).show();
        return result;
    }

    ImageView ivResult;

    enum ImageQuality {Rough, Fine}

    ImageQuality currentQuality = ImageQuality.Rough;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivResult = (ImageView)findViewById(R.id.ivResult);
        ivResult.setOnClickListener(this);
        ivResult.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        updateImage();
    }

    private void updateImage() {
        ivResult.setImageBitmap(currentQuality == ImageQuality.Rough ?
                                getRoughImage() : getFineImage());
    }

    @Override
    public void onClick(View view) {
        if (view == ivResult) {
            currentQuality = currentQuality == ImageQuality.Rough ?
                             ImageQuality.Fine : ImageQuality.Rough;
            updateImage();
        }
    }
}