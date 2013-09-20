package main.java.com.ifmomd.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;
import com.ifmomd.lesson2.R;

public class ImageView extends View {

    private final BitmapFactory.Options IMAGE_LOAD_OPTIONS;
    private final Bitmap.Config IMAGE_CONFIG;
    private final Bitmap BITMAP_IMAGE;
    private Image[] images;
    private int imageIndex;

    public ImageView(Context context) {
        super(context);

        //set image config
        IMAGE_CONFIG = Bitmap.Config.ARGB_8888;

        //set image load options
        IMAGE_LOAD_OPTIONS = new BitmapFactory.Options();
        IMAGE_LOAD_OPTIONS.inScaled = false;
        IMAGE_LOAD_OPTIONS.inPreferredConfig = IMAGE_CONFIG;

        //Decode image from resources
        BITMAP_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.source, IMAGE_LOAD_OPTIONS);
        int[] imagePixels = new int[BITMAP_IMAGE.getWidth() * BITMAP_IMAGE.getHeight()];
        BITMAP_IMAGE.getPixels(imagePixels, 0, BITMAP_IMAGE.getWidth(), 0, 0, BITMAP_IMAGE.getWidth(), BITMAP_IMAGE.getHeight());

        // create source image
        Image image = new Image(imagePixels, BITMAP_IMAGE.getWidth(), BITMAP_IMAGE.getHeight());

        // add images to image states
        images = new Image[3];
        images[0] = image;

        image = image.rotateClockwise();
        image = image.changeBrightness(image.countBrightness());

        images[1] = image.fastScale(405, 434);
        images[2] = image.bilinearInterpolationScale(405, 434);

        // change image by cick the screen
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageIndex = (imageIndex + 1) % images.length;
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //calculate offset for centralizing image
        int xOffset = (canvas.getWidth() - images[imageIndex].getWidth()) / 2;
        int yOffset = (canvas.getHeight() - images[imageIndex].getHeight()) / 2;
        //draw current image state
        canvas.drawBitmap(images[imageIndex].getPixels(), 0, images[imageIndex].getWidth(), xOffset, yOffset, images[imageIndex].getWidth(), images[imageIndex].getHeight(), true, null);
    }
}
