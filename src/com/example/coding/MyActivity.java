package com.example.coding;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MyActivity extends Activity implements View.OnClickListener {

    static final int TO_WIDTH = 405;
    static final int TO_HEIGHT = 434;

    ImageView view;

    int[] table;
    int width;
    int height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new ImageView(this);
        view.setScaleType(ImageView.ScaleType.CENTER);
        view.setClickable(true);
        view.setOnClickListener(this);
        setContentView(view);

        readPicture();

        next();
    }

    private void next() {
        showImage(resizeImageFast(table, width, height), TO_HEIGHT, TO_WIDTH);
    }

    private int[] resizeImageFast(int[] colors, int fromWidth, int fromHeight) {

        int[] tableTo = new int[TO_HEIGHT*TO_WIDTH];
        int[] tableToRevers = new int[TO_HEIGHT*TO_WIDTH];
        for(int i = 0; i < TO_WIDTH*TO_HEIGHT; i++) {
            int y = i / TO_WIDTH;
            int x = i % TO_WIDTH;
            int fromX = x * fromWidth / TO_WIDTH;
            int fromY = y * fromHeight / TO_HEIGHT;

            tableTo[i] = colors[fromY*fromWidth + fromX];
        }
        for(int i = 0; i < TO_HEIGHT*TO_WIDTH; i++) {
            int x = i % TO_HEIGHT;
            int y = i / TO_HEIGHT;
            int k = (TO_HEIGHT-x-1)*TO_WIDTH + TO_WIDTH - 1 - (TO_WIDTH - y-1);

            tableToRevers[i] = tableTo[k];

        }

        return tableToRevers;
    }
    private int[] resizeImageGood(int[] colors, int fromWidth, int fromHeight) {
        int[] tableTo = new int[TO_HEIGHT*TO_WIDTH];
        int[] tableToRevers = new int[TO_HEIGHT*TO_WIDTH];
        for(int i = 0; i < TO_WIDTH*TO_HEIGHT; i++) {
            int y = i / TO_WIDTH;
            int x = i % TO_WIDTH;
            int fromX = x * fromWidth / TO_WIDTH;
            int fromY = y * fromHeight / TO_HEIGHT;

            int firstColor = colors[fromY*fromWidth + fromX];
            int rightColor = colors[fromY*fromWidth + fromX];
            int leftColor = colors[fromY*fromWidth + fromX];
            int upColor = colors[fromY*fromWidth + fromX];
            int downColor = colors[fromY*fromWidth + fromX];

            if(fromX + 1 < fromWidth)
                rightColor = colors[fromY*fromWidth + fromX+1];
            if(fromX - 1 >= 0)
                leftColor = colors[fromY*fromWidth + fromX-1];
            if(fromY + 1 < fromHeight)
                downColor = colors[(fromY+1)*fromWidth + fromX];
            if(fromY - 1 >= 0)
                upColor = colors[(fromY-1)*fromWidth + fromX];

            int multi = 2;
            int sum = 6;
            int r = Color.red(firstColor) * multi +
                    Color.red(leftColor) +
                    Color.red(rightColor) +
                    Color.red(upColor) +
                    Color.red(downColor);
            r = r/sum;
            int b = Color.blue(firstColor) * multi +
                    Color.blue(leftColor) +
                    Color.blue(rightColor) +
                    Color.blue(upColor) +
                    Color.blue(downColor);
            b = b/sum;
            int g = Color.green(firstColor) * multi +
                    Color.green(leftColor) +
                    Color.green(rightColor) +
                    Color.green(upColor) +
                    Color.green(downColor);
            g = g/sum;
            tableTo[i] = Color.argb(255, r, g, b);
        }
        for(int i = 0; i < TO_HEIGHT*TO_WIDTH; i++) {
            int x = i % TO_HEIGHT;
            int y = i / TO_HEIGHT;
            int k = (TO_HEIGHT-x-1)*TO_WIDTH + TO_WIDTH - 1 - (TO_WIDTH - y-1);

            tableToRevers[i] = tableTo[k];

        }

        return tableToRevers;
    }

    private void readPicture() {
        Bitmap bitmapNat = BitmapFactory.decodeResource(getResources(), R.raw.source);
        width = bitmapNat.getWidth();
        height = bitmapNat.getHeight();
        table = new int[width * height];
        bitmapNat.getPixels(table, 0, width, 0, 0, width, height);
        for(int i = 0; i < table.length; i++) {
            int e = table[i];
            int alphaX = Color.alpha(e);
            int redX = Color.red(e);
            int greenX = Color.green(e);
            int blueX = Color.blue(e);

            double kf = 1.8;
            blueX = Math.min((int)(blueX*kf), 255);
            redX = Math.min((int)(redX*kf), 255);
            greenX = Math.min((int)(greenX*kf), 255);
            table[i] = Color.argb(alphaX, redX, greenX, blueX);

        }
        bitmapNat.recycle();
    }

    Bitmap bitmap;

    private void showImage(int[] colors, int width, int height) {
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
        view.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        showImage(resizeImageGood(table, width, height), TO_HEIGHT, TO_WIDTH);
    }
}