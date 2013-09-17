package com.polarnick.day02;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Date: 17.09.13
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ProgressView extends View {

    private final int screenWidth;
    private final int screenHeight;
    private final Paint percenagesPaint;

    private volatile boolean showPercenages = false;
    private volatile int progressPercentages;

    public ProgressView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.percenagesPaint = new Paint();
        percenagesPaint.setColor(Color.GREEN);
        percenagesPaint.setTextSize(20f);
    }

    public void updatePercetage(int progress) {
        progressPercentages = progress;
        invalidate();
    }

    public void setShowPercenages(boolean showPercenages) {
        this.showPercenages = showPercenages;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (showPercenages) {
            canvas.drawText(progressPercentages + "%", screenWidth / 2, screenHeight / 2, percenagesPaint);
        }
    }
}
