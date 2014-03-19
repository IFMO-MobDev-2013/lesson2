package com.example.Scale;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.View;
import android.graphics.BitmapFactory;
import android.widget.ImageButton;

public class MyActivity extends Activity {
    int[] smallBitMap;
    public static final int OLD_WIDTH = 700;
    public static final int OLD_HEIGHT = 750;
    public static final int NEW_WIDTH = 405;
    public static final int NEW_HEIGHT = 434;
    int[][] originalPic = new int[OLD_WIDTH][OLD_HEIGHT];
    Bitmap startPic;
    ImageButton button;
    Paint paint;
    boolean flag;

    public void fast(){
        for(int i = 0; i < NEW_HEIGHT; ++i)
            for(int j = 0; j < NEW_WIDTH; ++j){
                smallBitMap[NEW_WIDTH * i + j] =
                        originalPic[((int)(j * ((double)OLD_WIDTH / NEW_WIDTH)))]
                                [(int)(i * ((double)OLD_HEIGHT / NEW_HEIGHT))];
            }
    }

    public void slowly() {
        int cnt[][] = new int [NEW_HEIGHT][NEW_WIDTH];
        int mid[][][] = new int [NEW_HEIGHT][NEW_WIDTH][4];
        for(int i = 0; i < OLD_HEIGHT; i++) {
            for(int j = 0; j < OLD_WIDTH; j++) {
                int j1 = (int)(j * ((double)NEW_WIDTH / OLD_WIDTH));
                int i1 = (int)(i * ((double)NEW_HEIGHT / OLD_HEIGHT));
                cnt[i1][j1]++;
                int color = originalPic[j][i];
                int blue = color & 0x000000FF;
                int green = (color >> 8) & 0x000000FF;
                int red = (color >> 16) & 0x000000FF;
                int alpha = (color >> 24) & 0x000000FF;
                mid[i1][j1][0] += alpha;
                mid[i1][j1][1] += red;
                mid[i1][j1][2] += green;
                mid[i1][j1][3] += blue;
            }
        }
        for(int i = 0; i < NEW_HEIGHT; i++) {
            for(int j = 0; j < NEW_WIDTH; j++) {
                for(int k = 0; k < 4; k++) {
                    mid[i][j][k] /= Math.max(cnt[i][j],1);
                }
            }
        }
        for(int i = 0; i < NEW_HEIGHT; i++) {
            for(int j = 0; j < NEW_WIDTH; j++) {
                smallBitMap[NEW_WIDTH * i + j] = Color.argb(mid[i][j][0], mid[i][j][1], mid[i][j][2], mid[i][j][3]);
            }
        }

    }

    public void turn() {
        int[] tmp = new int[NEW_HEIGHT * NEW_WIDTH];
        for(int i = 0; i < NEW_WIDTH * NEW_HEIGHT; i++)
            tmp[i] = Color.BLUE;
        for(int i = 0; i < NEW_HEIGHT; ++i)
            for(int j = 0; j < NEW_WIDTH; ++j){
                tmp[j * NEW_HEIGHT + NEW_HEIGHT - i - 1] = smallBitMap[i * NEW_WIDTH + j];
            }
        smallBitMap = tmp;
    }

    public void defecation(){
        for(int i = 0; i < OLD_HEIGHT; i++){
            for(int j = 0; j < OLD_WIDTH; j++){
                int color = startPic.getPixel(j,i);
                int blue = Math.min(255, 2 * (color & 0x000000FF));
                int green = Math.min(255, 2 * ((color >> 8) & 0x000000FF));
                int red = Math.min(255, 2 * ((color >> 16) & 0x000000FF));
                int alpha = (color >> 24) & 0x000000FF;
                originalPic[j][i] = Color.argb(alpha,red,green,blue);
            }
        }
    }

    public void onClick(View v) {
        if (flag == false){
            slowly();
        }
        else{
            fast();
        }
        defecation();
        turn();
        print();
        flag = !flag;
    }

    public void print() {
        Bitmap tmp = Bitmap.createBitmap(smallBitMap,0,NEW_HEIGHT,NEW_HEIGHT,NEW_WIDTH,Bitmap.Config.ARGB_8888);
        button.setImageBitmap(tmp);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startPic = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        defecation();
        flag = false;
        button =(ImageButton) findViewById(R.id.Image);
        paint = new Paint();
        smallBitMap = new int[NEW_HEIGHT * NEW_WIDTH];
        fast();
        turn();
        print();
    }
}