package com.ifmomd.lesson2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created with IntelliJ IDEA.
 * User: satori
 * Date: 9/19/13
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class Image {
    public Bitmap bm;
    private int[] pixels;
    private int[] buf;
    public int WIDTH;
    public int HEIGHT;
    private final int RATIO = 173;
    public Image(Image ij) {
        bm = ij.bm.copy(Bitmap.Config.ARGB_8888, false);
        pixels = ij.pixels.clone();
        buf = ij.buf.clone();
        WIDTH = ij.WIDTH;
        HEIGHT = ij.HEIGHT;
    }
    public Image(Bitmap bm_) {
        bm = bm_;
        HEIGHT = bm.getHeight();
        WIDTH = bm.getWidth();
        pixels = new int[HEIGHT * WIDTH];
        bm.getPixels(pixels, 0, WIDTH, 0, 0 , WIDTH, HEIGHT);
        buf = pixels.clone();
    }

    public void rotate() {
        //int NEW_HEIGHT = WIDTH;
        //int NEW_WIDTH =  HEIGHT;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                pixels[j * HEIGHT + (HEIGHT - i - 1)] = buf[i * WIDTH + j];
            }
        }
        int tmp = WIDTH;
        WIDTH = HEIGHT;
        HEIGHT = tmp;
        buf = pixels.clone();
        bm = Bitmap.createBitmap(pixels, /*0, WIDTH,*/ WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
    }
    public void compressFast() {
        int NEW_WIDTH = (WIDTH * 100) / RATIO;
        int NEW_HEIGHT = (HEIGHT * 100) / RATIO;
        pixels = new int[NEW_WIDTH * NEW_HEIGHT];
        for (int i = 0; i < NEW_HEIGHT; i++) {
            for (int j =0; j < NEW_WIDTH; j++) {
                assert i * 100 / RATIO  < HEIGHT && j * 100 / RATIO < WIDTH;
                pixels[i * NEW_WIDTH + j] = buf [((i * RATIO) / 100) * WIDTH + ((j * RATIO) / 100)];
            }
        }
        WIDTH = NEW_WIDTH;
        HEIGHT = NEW_HEIGHT;
        buf = pixels.clone();
        bm = Bitmap.createBitmap(pixels, 0, WIDTH, WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
    }
 //   private static double getValue (double[] p, double x) {
//        return p[1] + 0.5 * x*(p[2] - p[0] + x*(2.0*p[0] - 5.0*p[1] + 4.0*p[2] - p[3] + x*(3.0*(p[1] - p[2]) + p[3] - p[0])));
//    }
    //public static Image createImage (int resource) {
//        return new Image(BitmapFactory.decodeResource(null,  resource));
//
//    }
    public void resizeBilinear(/*int[] pixels, int w, int h, int w2, int h2*/) {
        int NEW_WIDTH = (WIDTH * 100) / RATIO;
        int NEW_HEIGHT = (HEIGHT * 100) / RATIO;
        pixels = new int[NEW_WIDTH * NEW_HEIGHT];
        int a, b, c, d, x, y, index = 0;
        float x_ratio = ((float)(WIDTH - 1))/(float)NEW_WIDTH ;
        float y_ratio = ((float)(HEIGHT - 1))/(float)NEW_HEIGHT ;
        float x_diff, y_diff, blue, red, green ;
        int offset = 0 ;
        for (int i=0;i<NEW_HEIGHT;i++) {
            for (int j=0;j<NEW_WIDTH;j++) {
                assert ((i * 100) / RATIO  < HEIGHT) && ((j * 100) / RATIO < WIDTH);

                y = (i * RATIO) / 100;
                x = (j * RATIO) / 100;
                //x = (int)(x_ratio * j) ;
                //y = (int)(y_ratio * i) ;
                x_diff = (x_ratio * j) - x ;
                y_diff = (y_ratio * i) - y ;
                index = (y*WIDTH+x) ;
                a = buf[index] ;
                b = buf[index+1] ;
                c = buf[index+WIDTH] ;
                d = buf[index+WIDTH+1] ;

                // blue element
                // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                blue = (a&0xff)*(1-x_diff)*(1-y_diff) + (b&0xff)*(x_diff)*(1-y_diff) +
                        (c&0xff)*(y_diff)*(1-x_diff)   + (d&0xff)*(x_diff*y_diff);

                // green element
                // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                green = ((a>>8)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>8)&0xff)*(x_diff)*(1-y_diff) +
                        ((c>>8)&0xff)*(y_diff)*(1-x_diff)   + ((d>>8)&0xff)*(x_diff*y_diff);

                // red element
                // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                red = ((a>>16)&0xff)*(1-x_diff)*(1-y_diff) + ((b>>16)&0xff)*(x_diff)*(1-y_diff) +
                        ((c>>16)&0xff)*(y_diff)*(1-x_diff)   + ((d>>16)&0xff)*(x_diff*y_diff);

                pixels[offset++] =
                        0xff000000 | // hardcode alpha
                                ((((int)red)<<16)&0xff0000) |
                                ((((int)green)<<8)&0xff00) |
                                ((int)blue) ;
            }
        }
        WIDTH = NEW_WIDTH;
        HEIGHT = NEW_HEIGHT;
        buf = pixels.clone();
        bm = Bitmap.createBitmap(pixels, 0, WIDTH, WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        //return temp ;
    }
    public void brighten(int x) {

        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            int blue = buf[i] & 0xff;
            int green = (buf[i] >> 8) & 0xff;
            int red = (buf[i] >> 16) & 0xff;
            int alpha = (buf[i] >> 24) & 0xff;
            //int alpha = alpha(pixels[i]);
            //int red = red(pixels[i]);
            //int green = green(pixels[i]);
            //int blue = blue(pixels[i]);
            blue = (blue + x) >= 256 ? 255 : (blue + x);
            alpha = (alpha + x) >= 256 ? 255 : (alpha + x);
            green = (green + x) >= 256 ? 255 : (green + x);
            red = (red + x) >= 256 ? 255 : (red + x);

            pixels[i] = alpha << 24 | red << 16 |  green << 8 | blue;

        }
        buf = pixels.clone();
        bm = Bitmap.createBitmap(pixels, 0, WIDTH, WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
    }




}
