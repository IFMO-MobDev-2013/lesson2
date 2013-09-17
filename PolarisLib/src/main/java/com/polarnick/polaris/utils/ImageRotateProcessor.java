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

    private final int optimalThreadsCount;
    private final ExecutorService threadsPool;

    /**
     * @param optimalThreadsCount count of threads to be used over all time(if two or more methods will be executed
     *                            simultaneously - they will share given threads, but not <b>optimalThreadsCount<b/> threads per method)
     */
    public ImageRotateProcessor(int optimalThreadsCount) {
        this.optimalThreadsCount = optimalThreadsCount;
        this.threadsPool = Executors.newFixedThreadPool(optimalThreadsCount + 1);
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
    public void rotateBy90(final Bitmap image, final AsyncProgressCallback progressHolder, final AsyncCallback<Bitmap> callback) {
        threadsPool.execute(new Runnable() {
            @Override
            public void run() {
                byte[] sourceBytes = BitmapUtils.convertToByteArray(image);
                byte[] targetBytes = new byte[sourceBytes.length];
                CountDownLatch finish = new CountDownLatch(optimalThreadsCount);
                for (int i = 0; i < optimalThreadsCount; i++) {
                    final int fromY = image.getWidth() * i / optimalThreadsCount;
                    final int toY = image.getWidth() * (i + 1) / optimalThreadsCount;
                    threadsPool.execute(
                            new ImageRotatorBy90(sourceBytes, image.getWidth(), image.getHeight(),
                                    targetBytes, fromY, toY)
                                    .setReportProgress(1.0 / optimalThreadsCount, progressHolder)
                                    .setNotifyWhenFinish(finish));
                }
                try {
                    finish.await();
                    callback.onSuccess(BitmapUtils.createBitmapFromBytes(targetBytes, image.getHeight(), image.getWidth()));
                } catch (InterruptedException e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    private static class ImageRotatorBy90 implements Runnable {

        private final byte[] source;
        private final byte[] target;
        private final int sourceWidth;
        private final int sourceHeigth;
        private final int fromY;
        private final int toY;

        private AsyncProgressCallback progressHolder = null;
        private double fullProgress;

        private CountDownLatch notifyAtFinish = null;

        public ImageRotatorBy90(byte[] source, int sourceWidth, int sourceHeight, byte[] target, int fromY, int toY) {
            this.source = source;
            this.sourceWidth = sourceWidth;
            this.sourceHeigth = sourceHeight;
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
                for (int x = 0; x < sourceHeigth; x++) {
                    System.arraycopy(source, 4 * ((sourceHeigth - x - 1) * sourceWidth + y),
                            target, 4 * (y * sourceHeigth + x),
                            4);
                }
                if (progressHolder != null) {
                    progressHolder.registerProgressPassed(fullProgress * (y + 1) / (toY - fromY));
                }
            }
            notifyAtFinish.countDown();
        }
    }

}