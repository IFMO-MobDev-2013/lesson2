package ru.zulyaev.ifmo.android.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Никита
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView view;

    private Bitmap bitmap;
    private List<Image> images = new ArrayList<Image>(5);
    private int next;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new ImageView(this);
        setContentView(view);

        view.setScaleType(ImageView.ScaleType.CENTER);
        view.setClickable(true);
        view.setOnClickListener(this);

        init();
    }

    private void init() {
        Bitmap source = BitmapFactory.decodeResource(getResources(), R.raw.source);
        Image sourceImage = new RealImage(source);

        images.add(
                new ImageLighteningView(
                        new ImageRotationView(
                                new ImageScalingView(sourceImage, 434, 405)
                        )
                )
        );

        images.add(
                new ImageLighteningView(
                        new ImageRotationView(
                                new SimpleImageScalingView(sourceImage, 434, 405)
                        )
                )
        );

        showNext();
    }

    private void showNext() {
        view.setImageBitmap(generate(next++));
        next %= images.size();
    }

    private Bitmap generate(int index) {
        Image image = images.get(index);
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                pixels[i * width + j] = image.getPixel(i, j);
            }
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        return bitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    @Override
    public void onClick(View v) {
        showNext();
    }
}