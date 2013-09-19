package com.polarnick.polaris.utils.graphics;

import android.graphics.Bitmap;
import com.polarnick.polaris.concurrency.AsyncCallback;
import com.polarnick.polaris.concurrency.AsyncProgressCallback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Date: 18.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public abstract class ImageProcessingBase {

    private int optimalThreadsCount = 1;
    private ExecutorService threadsPool = Executors.newFixedThreadPool(optimalThreadsCount + 1);

    /**
     * @param optimalThreadsCount recommended count of threads to be used over all time(if two or more methods will be executed
     *                            simultaneously - they will share given threads, but not <b>optimalThreadsCount<b/> threads per method)
     */
    public void setOptimalThreadsCount(int optimalThreadsCount) {
        if (this.optimalThreadsCount != optimalThreadsCount) {
            this.optimalThreadsCount = optimalThreadsCount;
            this.threadsPool = Executors.newFixedThreadPool(optimalThreadsCount + 1);
        }
    }

    public void process(final Bitmap image, final AsyncCallback<Bitmap> callback) {
        process(image, null, callback);
    }

    public void process(final Bitmap image, final AsyncProgressCallback progressHolder, final AsyncCallback<Bitmap> callback) {
        threadsPool.execute(new Runnable() {
            @Override
            public void run() {
                final int targetWidth = getTargetWidth(image);
                final int targetHeight = getTargetHeight(image);
                byte[] sourceBytes = ImageBitmapUtils.convertToByteArray(image);
                byte[] targetBytes = new byte[4 * targetHeight * targetWidth];
                CountDownLatch finish = new CountDownLatch(optimalThreadsCount);
                for (int i = 0; i < optimalThreadsCount; i++) {
                    final int fromY = targetHeight * i / optimalThreadsCount;
                    final int toY = targetHeight * (i + 1) / optimalThreadsCount;
                    threadsPool.execute(
                            getProcessor(sourceBytes, image.getWidth(), image.getHeight(),
                                    targetBytes, targetWidth, targetHeight,
                                    fromY, toY)
                                    .setReportProgress(1.0 / optimalThreadsCount, progressHolder)
                                    .setNotifyWhenFinish(finish));
                }
                try {
                    finish.await();
                    callback.onSuccess(ImageBitmapUtils.createBitmapFromBytes(targetBytes, targetWidth, targetHeight));
                } catch (InterruptedException e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    protected int getTargetWidth(Bitmap sourceImage) {
        return sourceImage.getWidth();
    }

    protected int getTargetHeight(Bitmap sourceImage) {
        return sourceImage.getHeight();
    }

    protected abstract ImageProcessor getProcessor(byte[] source, int sourceWidth, int sourceHeight,
                                                   byte[] target, int targetWidth, int targetHeight,
                                                   int fromY, int toY);

    protected abstract class ImageProcessor implements Runnable {


        protected final byte[] source;
        protected final byte[] target;
        protected final int sourceWidth;
        protected final int sourceHeigth;
        protected final int targetWidth;
        protected final int targetHeight;
        protected final int fromY;
        protected final int toY;

        protected AsyncProgressCallback progressHolder = null;
        protected double fullProgress;

        private CountDownLatch notifyAtFinish = null;

        protected ImageProcessor(byte[] source, int sourceWidth, int sourceHeight,
                                 byte[] target, int targetWidth, int targetHeight,
                                 int fromY, int toY) {
            this.source = source;
            this.sourceWidth = sourceWidth;
            this.sourceHeigth = sourceHeight;

            this.target = target;
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;

            this.fromY = fromY;
            this.toY = toY;
        }

        /**
         * @param fullProgress   this value should be summary added to progressHolder at the end of task
         * @param progressHolder where to register progress, can be null - then processor will not report progress
         */
        public ImageProcessor setReportProgress(double fullProgress, AsyncProgressCallback progressHolder) {
            this.progressHolder = progressHolder;
            this.fullProgress = fullProgress;
            return this;
        }

        public ImageProcessor setNotifyWhenFinish(CountDownLatch latch) {
            this.notifyAtFinish = latch;
            return this;
        }

        @Override
        public void run() {
            process();
            if (notifyAtFinish != null) {
                notifyAtFinish.countDown();
            }
        }

        protected abstract void process();

    }

}
