package lib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import androidx.core.graphics.ColorUtils;
import android.view.View;

/**
 * Created by GAO-xx on 2018/4/9.
 */

public class DottedLineView extends View
{
    private Paint mPaint;
    private int mPaintFlags;

    public DottedLineView(Context context)
    {
        super(context);
        mPaintFlags = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int i = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

        canvas.drawColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.4f)));

        mPaint.reset();
        mPaint.setFlags(mPaintFlags);
        mPaint.setColor(Color.WHITE);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(getMeasuredWidth() /2f - 100f, getMeasuredHeight()/2f - 100f, getMeasuredWidth() /2f + 100f, getMeasuredHeight() /2f + 100f, mPaint);

        mPaint.reset();
        mPaint.setFlags(mPaintFlags);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setPathEffect(new DashPathEffect(new float[]{12, 6}, 1));
        canvas.drawRect(getMeasuredWidth() /2f - 100f, getMeasuredHeight()/2f - 100f, getMeasuredWidth() /2f + 100f, getMeasuredHeight() /2f + 100f, mPaint);

        canvas.restoreToCount(i);
    }
}
