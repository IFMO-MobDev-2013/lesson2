package com.polarnick.polaris.utils;

import android.graphics.Bitmap;
import com.polarnick.polaris.concurrency.AsyncCallback;
import com.polarnick.polaris.concurrency.AsyncProgressCallback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Date: 17.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ImageRotateProcessor {

    private final int threadsCount;
    private final ExecutorService threadsPool;

    /**
     * @param threadsCount count of threads to be used over all time(if two or more methods will be executed
     *                     simultaneously - they will share given threads, but not <b>threadsCount<b/> threads per method)
     */
    public ImageRotateProcessor(int threadsCount) {
        this.threadsCount = threadsCount;
        this.threadsPool = Executors.newFixedThreadPool(threadsCount);
    }

    /**
     * Asynchronously rotates bitmap image by <b>90</b> degree in clockwise direction.
     *
     * @param image    to be rotated
     * @param callback to provide result
     */
    public void rotateBy90(Bitmap image, AsyncCallback<Bitmap> callback) {
        rotateBy90(image, null, callback);
    }

    /**
     * Asynchronously rotates bitmap image by <b>90</b> degree in clockwise direction.
     *
     * @param image          to be rotated
     * @param progressHolder holder, that will be provided with information about progress of task execution.
     *                       (Can be <b>null</b>)
     * @param callback       to provide result
     */
    public void rotateBy90(Bitmap image, AsyncProgressCallback progressHolder, AsyncCallback<Bitmap> callback) {
        Bitmap result = Bitmap.createBitmap(image.getHeight(), image.getWidth(), Bitmap.Config.RGB_565);
        CountDownLatch finish = new CountDownLatch(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            threadsPool.execute(
                    new ImageRotatorBy90(image, result, image.getHeight() * i / threadsCount, image.getHeight() * (i + 1) / threadsCount)
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

    private static class ImageRotatorBy90 implements Runnable {

        private final Bitmap source;
        private final Bitmap target;
        private final int fromY;
        private final int toY;

        private AsyncProgressCallback progressHolder = null;
        private double fullProgress;

        private CountDownLatch notifyAtFinish = null;

        public ImageRotatorBy90(Bitmap source, Bitmap target, int fromY, int toY) {
            this.source = source;
            this.target = target;
            this.fromY = fromY;
            this.toY = toY;
        }

        /**
         * @param fullProgress   this value should be summary added to progressHolder at the end of task
         * @param progressHolder where to register progress
         */
        public ImageRotatorBy90 setReportProgress(double fullProgress, AsyncProgressCallback progressHolder) {
            this.progressHolder = progressHolder;
            this.fullProgress = fullProgress;
            return this;
        }

        public ImageRotatorBy90 setNotifyWhenFinish(CountDownLatch latch) {
            this.notifyAtFinish = latch;
            return this;
        }

        @Override
        public void run() {
            for (int y = fromY; y < toY; y++) {
                for (int x = 0; x < source.getWidth(); x++) {
                    target.setPixel(y, x, source.getPixel(x, y));
                }
                if (progressHolder != null) {
                    progressHolder.registerProgressPassed(fullProgress * (y + 1) / (toY - fromY));
                }
            }
            notifyAtFinish.countDown();
        }
    }

}