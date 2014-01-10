package com.alimantu.Pict_rev;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    class rev extends View implements View.OnClickListener {

        Bitmap bmp;
        int bmp_width;
        int bmp_height;
        int[] bmp_pixels;
        int[] bmp_pixels_changed;
        int D_Height;
        int D_Width;
        int compress_width;
        int compress_height;
        boolean variant = false;

        public rev(Context context)
        {
            super(context);
            this.setOnClickListener(this);
        }

        public void get_pict()
        {

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            bmp_height = bmp.getHeight();
            bmp_width = bmp.getWidth();
            bmp_pixels = new int[bmp_width * bmp_height];
            bmp.getPixels(bmp_pixels,0,bmp_width,0,0,bmp_width,bmp_height);

        }

        public void in_bright()
        {

            int tmp_red;
            int tmp_green;
            int tmp_blue;
            int tmp = 2;

            for(int i = 0; i < bmp_width*bmp_height; ++i)
            {
                tmp_red = (int)(((bmp_pixels[i] >> 16) & 255) * tmp);
                tmp_green = (int)(((bmp_pixels[i] >> 8) & 255) * tmp);
                tmp_blue = (int)((bmp_pixels[i] & 255) * tmp);

                tmp_red = (tmp_red > 255)? 255 : tmp_red;
                tmp_green = (tmp_green > 255)? 255 : tmp_green;
                tmp_blue = (tmp_blue > 255)? 255 : tmp_blue;

                bmp_pixels[i] = (tmp_red << 16) | (tmp_green << 8) | tmp_blue;
            }
        }

        public int[] compress_fast(int w, int h, int n_w, int n_h){

            int[] tmp_pix = new int[n_w*n_h];
            int tmp_x = (int)((w << 16) / n_w) + 1;
            int tmp_y = (int)((h << 16) / n_h) + 1;
            int x;
            int y;
            for(int i = 0; i < n_h; ++i)
                for(int j = 0; j < n_w; ++j)
                {
                    x = ((j * tmp_x) >> 16);
                    y = ((i * tmp_y) >> 16);
                    tmp_pix[i*n_w + j] = bmp_pixels[y*w + x];
                }

            return tmp_pix;

        }

        public int[] compress_qual(int w, int h, int n_w, int n_h){

            int[] tmp_pix = new int[n_w * n_h];
            int tmp_1, tmp_2, tmp_3, tmp_4, tmp, x, y;
            float tmp_x = ((float)(w - 1)) / n_w;
            float tmp_y = ((float)(h - 1)) / n_h;
            float tmp_red, tmp_green, tmp_blue;
            float inc_x, inc_y;
            int offset = 0;
            for(int i = 0; i < n_h; ++i)
                for(int j = 0; j < n_w; ++j)
                {
                    x = (int)(j * tmp_x);
                    y = (int)(i * tmp_y);
                    inc_x = (j * tmp_x) - x;
                    inc_y = (i * tmp_y) - y;
                    tmp = y*w + x;

                    tmp_1 = bmp_pixels[tmp];
                    tmp_2 = bmp_pixels[tmp + 1];
                    tmp_3 = bmp_pixels[tmp + w];
                    tmp_4 = bmp_pixels[tmp + w + 1];

                    tmp_red = ((tmp_1 >> 16) & 255) * (1 - inc_x) * (1 - inc_y)
                            + ((tmp_2 >> 16) & 255) * inc_x * (1 - inc_y)
                            + ((tmp_3 >> 16) & 255) * (1 - inc_x) * inc_y
                            + ((tmp_4 >> 16) & 255) * inc_x * inc_y;
                    tmp_green = ((tmp_1 >> 8) & 255) * (1 - inc_x) * (1 - inc_y)
                            + ((tmp_2 >> 8) & 255) * inc_x * (1 - inc_y)
                            + ((tmp_3 >> 8) & 255) * (1 - inc_x) * inc_y
                            + ((tmp_4 >> 8) & 255) * inc_x * inc_y;
                    tmp_blue = (tmp_1 & 255) * (1 - inc_x) * (1 - inc_y)
                            + (tmp_2 & 255) * inc_x * (1 - inc_y)
                            + (tmp_3 & 255) * (1 - inc_x) * inc_y
                            + (tmp_4 & 255) * inc_x * inc_y;
                    tmp_pix[offset++] = 0xff000000
                            | ((((int)tmp_red) & 255) << 16)
                            | ((((int)tmp_green) & 255) << 8)
                            | (((int)tmp_blue) & 255);

                }

            return tmp_pix;

        }

        public int[] compress(int w, int h, int n_w, int n_h)
        {

            int[] tmp_pix = new int[n_w * n_h];

            if(variant)
            {
                int tmp_x = (int)((w << 16) / n_w) + 1;
                int tmp_y = (int)((h << 16) / n_h) + 1;
                int x;
                int y;
                for(int i = 0; i < n_h; ++i)
                    for(int j = 0; j < n_w; ++j)
                    {
                        x = ((j * tmp_x) >> 16);
                        y = ((i * tmp_y) >> 16);
                        tmp_pix[i*n_w + j] = bmp_pixels[y*w + x];
                    }
            }
            else
            {
                int tmp_1, tmp_2, tmp_3, tmp_4, tmp, x, y;
                float tmp_x = ((float)(w - 1)) / n_w;
                float tmp_y = ((float)(h - 1)) / n_h;
                float tmp_red, tmp_green, tmp_blue;
                float inc_x, inc_y;
                int offset = 0;
                for(int i = 0; i < n_h; ++i)
                    for(int j = 0; j < n_w; ++j)
                    {
                        x = (int)(j * tmp_x);
                        y = (int)(i * tmp_y);
                        inc_x = (j * tmp_x) - x;
                        inc_y = (i * tmp_y) - y;
                        tmp = y*w + x;

                        tmp_1 = bmp_pixels[tmp];
                        tmp_2 = bmp_pixels[tmp + 1];
                        tmp_3 = bmp_pixels[tmp + w];
                        tmp_4 = bmp_pixels[tmp + w + 1];

                        tmp_red = ((tmp_1 >> 16) & 255) * (1 - inc_x) * (1 - inc_y)
                                + ((tmp_2 >> 16) & 255) * inc_x * (1 - inc_y)
                                + ((tmp_3 >> 16) & 255) * (1 - inc_x) * inc_y
                                + ((tmp_4 >> 16) & 255) * inc_x * inc_y;
                        tmp_green = ((tmp_1 >> 8) & 255) * (1 - inc_x) * (1 - inc_y)
                                + ((tmp_2 >> 8) & 255) * inc_x * (1 - inc_y)
                                + ((tmp_3 >> 8) & 255) * (1 - inc_x) * inc_y
                                + ((tmp_4 >> 8) & 255) * inc_x * inc_y;
                        tmp_blue = (tmp_1 & 255) * (1 - inc_x) * (1 - inc_y)
                                + (tmp_2 & 255) * inc_x * (1 - inc_y)
                                + (tmp_3 & 255) * (1 - inc_x) * inc_y
                                + (tmp_4 & 255) * inc_x * inc_y;
                        tmp_pix[offset++] = 0xff000000
                                | ((((int)tmp_red) & 255) << 16)
                                | ((((int)tmp_green) & 255) << 8)
                                | (((int)tmp_blue) & 255);

                    }
            }

            variant = !variant;

            return tmp_pix;
        }

        public void to_90()
        {

            int[] tmp_pix = new int[bmp_width * bmp_height];
            for(int i = 0; i < bmp_height; ++i)
                for(int j = 0; j < bmp_width; ++j)
                    tmp_pix[j * bmp_height + bmp_height - i - 1] = bmp_pixels[i * bmp_width + j];

            bmp_pixels = tmp_pix;
            int temp = bmp_height;
            bmp_height = bmp_width;
            bmp_width = temp;

        }


        @Override
        public void onDraw(Canvas canvas)
        {

            get_pict();

            to_90();

            in_bright();

            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bmp_pixels, 0, bmp_width, 0, 0, bmp_width, bmp_height, false, null);

            compress_width = (int)(bmp_width / 1.73 / 2);
            compress_height = (int)(bmp_height / 1.73 / 2);

            bmp_pixels_changed = new int [compress_width * compress_height];

//            if(variant)
//                bmp_pixels_changed = compress_fast(bmp_width, bmp_height, compress_width, compress_height);
//            else
//                bmp_pixels_changed = compress_qual(bmp_width, bmp_height, compress_width, compress_height);
            bmp_pixels_changed = compress(bmp_width, bmp_height, compress_width, compress_height);

//            variant = !variant;

            Display new_display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

            int dis_width = new_display.getWidth();
            int dis_height = new_display.getHeight();
            int start_x = dis_width/2 - compress_width/2;
            int start_y = dis_height/2 - compress_height/2;

            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bmp_pixels_changed, 0, compress_width, start_x, start_y, compress_width, compress_height, false, null);

        }

        @Override
        public void onClick(View view)
        {
            view.invalidate();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new rev(this));

    }

}
