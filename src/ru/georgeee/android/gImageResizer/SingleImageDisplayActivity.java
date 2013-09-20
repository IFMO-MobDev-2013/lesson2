package ru.georgeee.android.gImageResizer;

import android.app.Activity;
import android.os.Bundle;

public class SingleImageDisplayActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ResizableImageView(this));
    }




}
