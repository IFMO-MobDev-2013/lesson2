package com.polarnick.day02;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * This view draw progress percentages at the screen center. For example: "45%" or "100%". But for example "239%" can't
 * be shown by it, even though the significance of this number.
 * <p/>
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

    private volatile int lastRenderedPercentages;
    private volatile boolean rendering = false;

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
        if (!rendering && progressPercentages > lastRenderedPercentages) {
            lastRenderedPercentages = progressPercentages;
            rendering = true;
            postInvalidate();
        }
    }

    public void setShowPercenages(boolean showPercenages) {
        this.showPercenages = showPercenages;
        if (!rendering) {
            rendering = true;
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        rendering = false;
        if (showPercenages) {
            if (progressPercentages == 239) {
                canvas.drawText("Honor the contract!!!", screenWidth / 2, screenHeight / 2, percenagesPaint);
            } else {
                canvas.drawText(progressPercentages + "%", screenWidth / 2, screenHeight / 2, percenagesPaint);
            }
        }
    }
}
