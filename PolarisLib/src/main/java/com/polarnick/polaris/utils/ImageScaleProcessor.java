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

    private final int optimalThreadsCount;
    private final ExecutorService threadsPool;
    private TYPE_OF_SCALING typeOfScaling = TYPE_OF_SCALING.FAST;

    /**
     * @param optimalThreadsCount count of threads to be used over all time(if two or more methods will be executed
     *                            simultaneously - they will share given threads, but not <b>optimalThreadsCount<b/> threads per method)
     */
    public ImageScaleProcessor(int optimalThreadsCount) {
        this.optimalThreadsCount = optimalThreadsCount;
        this.threadsPool = Executors.newFixedThreadPool(optimalThreadsCount);
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
    public void scale(final Bitmap image, final double factor, final AsyncProgressCallback progressHolder, final AsyncCallback<Bitmap> callback) {
        threadsPool.execute(new Runnable() {
            @Override
            public void run() {
                final int targetWidth = ((int) (image.getWidth() * factor));
                final int targetHeight = ((int) (image.getHeight() * factor));
                byte[] sourceBytes = BitmapUtils.convertToByteArray(image);
                byte[] targetBytes = new byte[targetWidth * targetHeight * 4];
                CountDownLatch finish = new CountDownLatch(optimalThreadsCount);
                for (int i = 0; i < optimalThreadsCount; i++) {
                    final int fromY = targetHeight * i / optimalThreadsCount;
                    final int toY = targetHeight * (i + 1) / optimalThreadsCount;
                    threadsPool.execute(
                            new ImageScaler(sourceBytes, image.getWidth(), image.getHeight(),
                                    targetBytes, targetWidth, targetHeight,
                                    typeOfScaling, fromY, toY)
                                    .setReportProgress(1.0 / optimalThreadsCount, progressHolder)
                                    .setNotifyWhenFinish(finish));
                }
                try {
                    finish.await();
                    callback.onSuccess(BitmapUtils.createBitmapFromBytes(targetBytes, targetWidth, targetHeight));
                } catch (InterruptedException e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    public TYPE_OF_SCALING getTypeOfScaling() {
        return typeOfScaling;
    }

    public void setTypeOfScaling(TYPE_OF_SCALING typeOfScaling) {
        this.typeOfScaling = typeOfScaling;
    }

    private static class ImageScaler implements Runnable {

        private final byte[] source;
        private final int sourceWidth;
        private final int sourceHeight;
        private final byte[] target;
        private final int targetWidth;
        private final int targetHeight;
        private final TYPE_OF_SCALING typeOfScaling;
        private final int fromY;
        private final int toY;

        private AsyncProgressCallback progressHolder = null;
        private double fullProgress;

        private CountDownLatch notifyAtFinish = null;

        public ImageScaler(byte[] source, int sourceWidth, int sourceHeight, byte[] target, int targetWidth, int targetHeight, TYPE_OF_SCALING typeOfScaling, int fromY, int toY) {
            this.source = source;
            this.sourceWidth = sourceWidth;
            this.sourceHeight = sourceHeight;
            this.target = target;
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
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
                for (int x = 0; x < targetWidth; x++) {
                    switch (typeOfScaling) {
                        case FAST: {
                            updateFastColor(x, y);
                            break;
                        }
                        case QUALITY: {
                            updateQualityColor(x, y);
                            break;
                        }
                    }
                }
                if (progressHolder != null) {
                    progressHolder.registerProgressPassed(fullProgress * (y + 1 - fromY) / (toY - fromY));
                }
            }
            notifyAtFinish.countDown();
        }

        private void updateQualityColor(int x, int y) {
            throw new NotImplementedException("Quality scaling not implemented yet!");
        }

        private void updateFastColor(int x, int y) {
            int sourceX = x * sourceWidth / targetWidth;
            int sourceY = y * sourceHeight / targetHeight;
            int sourceIndex = sourceY * sourceWidth + sourceX;
            int targetIndex = y * targetWidth + x;
            System.arraycopy(source, sourceIndex * 4, target, targetIndex * 4, 4);
        }
    }

    public static enum TYPE_OF_SCALING {
        FAST,
        QUALITY
    }

}