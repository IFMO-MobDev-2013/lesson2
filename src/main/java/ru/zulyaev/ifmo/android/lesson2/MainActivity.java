package ru.zulyaev.ifmo.android.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Iterator;

/**
 * @author Никита
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView view;
    private Button button;

    private Bitmap bitmap;
    private int sourceWidth;
    private int sourceHeight;
    private int destWidth;
    private int destHeight;
    private int[] source;
    private Iterator<ImageProcessor> iterator;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        view = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button);

        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.raw.source);
        sourceWidth = sourceBitmap.getWidth();
        sourceHeight = sourceBitmap.getHeight();
        source = new int[sourceWidth * sourceHeight];
        sourceBitmap.getPixels(source, 0, sourceWidth, 0, 0, sourceWidth, sourceHeight);
        iterator = new CyclicArrayIterator<ImageProcessor>(ImageProcessor.values());
        destWidth = getResources().getInteger(R.integer.width);
        destHeight = getResources().getInteger(R.integer.height);

        showNext();
    }

    private void showNext() {
        new RenderTask().execute();
    }

    @Override
    public void onClick(View v) {
        showNext();
    }

    class RenderTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            button.setEnabled(false);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (bitmap != null) {
                bitmap.recycle();
            }
            int[] pixels = iterator.next().proccess(source, sourceWidth, sourceHeight, destWidth, destHeight);
            //noinspection SuspiciousNameCombination
            return bitmap = Bitmap.createBitmap(pixels, destHeight, destWidth, Bitmap.Config.ARGB_8888);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            view.setImageBitmap(bitmap);
            button.setEnabled(true);
        }
    }
}