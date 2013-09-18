package com.polarnick.polaris.utils.graphics;

import android.graphics.Bitmap;
import com.google.common.base.Preconditions;
import com.polarnick.polaris.concurrency.AsyncCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 18.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ImageAsyncProcessingChain {

    private final int optimalThreadCount;
    private final Bitmap sourceImage;
    private final List<ImageProcessingBase> processors = new ArrayList<ImageProcessingBase>();

    public ImageAsyncProcessingChain(Bitmap sourceImage, int optimalThreadCount) {
        Preconditions.checkNotNull(sourceImage);
        Preconditions.checkArgument(optimalThreadCount >= 1);
        this.sourceImage = sourceImage;
        this.optimalThreadCount = optimalThreadCount;
    }

    public ImageAsyncProcessingChain process(ImageProcessingBase processor) {
        processors.add(processor);
        return this;
    }

    public void asyncExecute(AsyncCallback<Bitmap> callback) {
        execute(0, sourceImage, callback);
    }

    private void execute(final int indexOfProcessor, Bitmap imageToProcess, final AsyncCallback<Bitmap> callback) {
        if (indexOfProcessor == processors.size()) {
            callback.onSuccess(imageToProcess);
        } else {
            processors.get(indexOfProcessor).setOptimalThreadsCount(optimalThreadCount);
            processors.get(indexOfProcessor).process(imageToProcess, new AsyncCallback<Bitmap>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    execute(indexOfProcessor + 1, bitmap, callback);
                }

                @Override
                public void onFailure(Throwable reason) {
                    throw new RuntimeException("Failed on processing at " + indexOfProcessor + "th processor", reason);
                }
            });
        }
    }

}