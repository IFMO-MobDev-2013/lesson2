package com.polarnick.polaris.utils;

import android.graphics.Bitmap;
import com.polarnick.polaris.concurrency.AsyncCallback;
import com.polarnick.polaris.concurrency.AsyncProgressCallback;
import org.apache.commons.lang.NotImplementedException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Date: 17.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ImageScaleProcessor {

    private final int threadsCount;
    private final ExecutorService threadsPool;
    private TYPE_OF_SCALING typeOfScaling = TYPE_OF_SCALING.FAST;

    /**
     * @param threadsCount count of threads to be used over all time(if two or more methods will be executed
     *                     simultaneously - they will share given threads, but not <b>threadsCount<b/> threads per method)
     */
    public ImageScaleProcessor(int threadsCount) {
        this.threadsCount = threadsCount;
        this.threadsPool = Executors.newFixedThreadPool(threadsCount);
    }

    /**
     * Asynchronously scale bitmap image.
     *
     * @param image    source image
     * @param factor   how many times the result image's height and width should be bigger than the source one
     * @param callback to provide result
     */
    public void scale(Bitmap image, double factor, AsyncCallback<Bitmap> callback) {
        scale(image, factor, null, callback);
    }

    /**
     * Asynchronously scale bitmap image.
     *
     * @param image          source image
     * @param factor         how many times the result image's height and width should be bigger than the source one
     * @param progressHolder holder, that will be provided with information about progress of task execution.
     *                       (Can be <b>null</b>)
     * @param callback       to provide result
     */
    public void scale(Bitmap image, double factor, AsyncProgressCallback progressHolder, AsyncCallback<Bitmap> callback) {
        Bitmap result = Bitmap.createBitmap((int) (image.getWidth() * factor), (int) (image.getHeight() * factor), Bitmap.Config.RGB_565);
        CountDownLatch finish = new CountDownLatch(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            threadsPool.execute(
                    new ImageScaler(image, result, typeOfScaling,
                            result.getHeight() * i / threadsCount, result.getHeight() * (i + 1) / threadsCount)
                            .setReportProgress(1.0 / threadsCount, progressHolder)
                            .setNotifyWhenFinish(finish));
        }
        try {
            finish.await();
            callback.onSuccess(result);
        } catch (InterruptedException e) {
            callback.onFailure(e);
        }
    }

    public TYPE_OF_SCALING getTypeOfScaling() {
        return typeOfScaling;
    }

    public void setTypeOfScaling(TYPE_OF_SCALING typeOfScaling) {
        this.typeOfScaling = typeOfScaling;
    }

    private static class ImageScaler implements Runnable {

        private final Bitmap source;
        private final Bitmap target;
        private final TYPE_OF_SCALING typeOfScaling;
        private final int fromY;
        private final int toY;

        private AsyncProgressCallback progressHolder = null;
        private double fullProgress;

        private CountDownLatch notifyAtFinish = null;

        public ImageScaler(Bitmap source, Bitmap target, TYPE_OF_SCALING typeOfScaling, int fromY, int toY) {
            this.source = source;
            this.target = target;
            this.typeOfScaling = typeOfScaling;
            this.fromY = fromY;
            this.toY = toY;
        }

        /**
         * @param fullProgress   this value should be summary added to progressHolder at the end of task
         * @param progressHolder where to register progress
         */
        public ImageScaler setReportProgress(double fullProgress, AsyncProgressCallback progressHolder) {
            this.progressHolder = progressHolder;
            this.fullProgress = fullProgress;
            return this;
        }

        public ImageScaler setNotifyWhenFinish(CountDownLatch latch) {
            this.notifyAtFinish = latch;
            return this;
        }

        @Override
        public void run() {
            for (int y = fromY; y < toY; y++) {
                for (int x = 0; x < target.getWidth(); x++) {
                    switch (typeOfScaling) {
                        case FAST: {
                            target.setPixel(x, y, getFastColor(x, y));
                            break;
                        }
                        case QUALITY: {
                            target.setPixel(x, y, getQualityColor(x, y));
                            break;
                        }
                    }
                }
                if (progressHolder != null) {
                    progressHolder.registerProgressPassed(fullProgress * (y + 1) / (toY - fromY));
                }
            }
            notifyAtFinish.countDown();
        }

        private int getQualityColor(int x, int y) {
            throw new NotImplementedException("Quality scaling not implemented yet!");
        }

        private int getFastColor(int x, int y) {
            return source.getPixel(x * source.getWidth() / target.getWidth(), y * source.getHeight() / target.getHeight());
        }
    }

    public static enum TYPE_OF_SCALING {
        FAST,
        QUALITY
    }

}