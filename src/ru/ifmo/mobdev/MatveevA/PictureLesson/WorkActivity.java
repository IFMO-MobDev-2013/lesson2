package ru.ifmo.mobdev.MatveevA.PictureLesson;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import java.lang.*;


public class WorkActivity extends Activity {

    private static final int WIDTH = 700;
    private static final int HEIGHT = 750;

    private static final int NEW_WIDTH = 434;
    private static final int NEW_HEIGHT = 405;

    private int changeColor(int color) {
        int red;
        int green;
        int blue;
        int clarity;
        int answer = 0;

        blue = (color & 0x000000FF);
        green = ((color >> 8) & 0x000000FF);
        red = ((color >> 16) & 0x000000FF);
        clarity = ((color >> 24) & 0x000000FF);

        blue = Math.min(blue * 2, 255);
        green = Math.min(green * 2, 255);
        red = Math.min(red * 2, 255);

        answer = (answer | (clarity << 24));
        answer = (answer | (red << 16));
        answer = (answer | (green << 8));
        answer = (answer | blue);

        return answer;
    }

    private int[] rotate(int[] pixels) {

        int[] answer = new int[WIDTH*HEIGHT];

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                answer[j * HEIGHT + HEIGHT - i - 1] = pixels[i * WIDTH + j];
            }
        }

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++)  {
                answer[i * HEIGHT + j] = changeColor(answer[i * HEIGHT + j]);
            }
        }

        return answer;
    }

    int[] getLowQualityPicture(int[] pixels) {

        int[] answer = new int[NEW_HEIGHT * NEW_WIDTH];

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                int newI = (int) Math.floor((double) i / 1.73);
                int newJ = (int) Math.floor((double) j / 1.73);

                answer[newI * NEW_WIDTH + newJ] = pixels[i * HEIGHT + j];
            }
        }

        return answer;
    }



    int[] getHighQualityPicture(int[] pixels) {

        int colors[][] = new int[NEW_WIDTH * NEW_HEIGHT][4];
        int count[] = new int[NEW_HEIGHT * NEW_WIDTH];
        int answer[] = new int[NEW_HEIGHT * NEW_WIDTH];

        for (int i = 0; i < NEW_HEIGHT * NEW_WIDTH; i++)  {
            count[i] = 0;
            colors[i][0] = 0;
            colors[i][1] = 0;
            colors[i][2] = 0;
            colors[i][3] = 0;
        }

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {

                int newI = (int) Math.floor((double) i / 1.73);
                int newJ = (int) Math.floor((double) j / 1.73);


                int color = pixels[i * HEIGHT + j];

                int blue = (color & 0x000000FF);
                int green = ((color >> 8) & 0x000000FF);
                int red = ((color >> 16) & 0x000000FF);
                int clarity = ((color >> 24) & 0x000000FF);

                colors[newI * NEW_WIDTH + newJ][0] += clarity;
                colors[newI * NEW_WIDTH + newJ][1] += red;
                colors[newI * NEW_WIDTH + newJ][2] += green;
                colors[newI * NEW_WIDTH + newJ][3] += blue;

                count[newI * NEW_WIDTH + newJ]++;
            }
        }

        for (int i = 0; i < NEW_HEIGHT * NEW_WIDTH; i++)  {
            colors[i][0] /= Math.max(count[i], 1);
            colors[i][1] /= Math.max(count[i], 1);
            colors[i][2] /= Math.max(count[i], 1);
            colors[i][3] /= Math.max(count[i], 1);

            int cur = 0;

            cur = (cur | (colors[i][0] << 24));
            cur = (cur | (colors[i][1] << 16));
            cur = (cur | (colors[i][2] << 8));
            cur = (cur | colors[i][3]);

            answer[i] = cur;
        }

        return answer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.work);

        Bitmap workPicture = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);

        int[] pixels = new int[WIDTH*HEIGHT];

        workPicture.getPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);

        int[] rotatedPixels;

        rotatedPixels = rotate(pixels);

        int priority = getIntent().getExtras().getInt("priority");

        if (priority == 1) {
            rotatedPixels = getLowQualityPicture(rotatedPixels);
        }
        else {
            rotatedPixels = getHighQualityPicture(rotatedPixels);
        }

        Bitmap newWorkPicture = Bitmap.createBitmap(rotatedPixels, 0, NEW_WIDTH, NEW_WIDTH, NEW_HEIGHT, Bitmap.Config.ARGB_8888);


        ImageView imgView = (ImageView) findViewById(R.id.workImage);
        imgView.setImageBitmap(newWorkPicture);
    }

}
