package lib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.graphics.ColorUtils;
import android.view.MotionEvent;
import android.view.View;

import lib.util.PixelPercentUtil;

/**
 * 四散阴影 seekbar
 * Created by Gxx on 2018/3/2.
 * <p>
 * 网上资料启发阴影绘制
 * <p>
 * https://www.cnblogs.com/irrienberith/p/3953358.html
 * <p>
 * bitmap 添加阴影
 * <p>
 * http://blog.csdn.net/harvic880925/article/details/51889104
 * <p>
 * 硬件加速相关资料
 * <p>
 * https://www.jianshu.com/p/9cd7097a4fcf
 */

public class ShadowSeekBar extends View
{
    private int mViewW;
    private int mViewH;
    private float x;

    private int mSelectedProgressWidth;
    private int mProgressWidth;
    private int mSelectedProgressColor;
    private int mProgressColor;
    private int mEdgeSpan;
    private float mCircleRadius;

    private Paint mPaint;
    private int mPaintFlags;

    public ShadowSeekBar(Context context)
    {
        super(context);
        // 除文字阴影外，其他图形+图片的阴影都需要关掉硬件加速才有效果
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        init();
    }

    private void init()
    {
        mSelectedProgressWidth = PixelPercentUtil.WidthPxToPercent(4);
        mProgressWidth = PixelPercentUtil.WidthPxToPercent(3);

        mSelectedProgressColor = Color.WHITE;
        mProgressColor = ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.5f));

        mEdgeSpan = PixelPercentUtil.WidthPxToPercent(30);
        mCircleRadius = PixelPercentUtil.WidthPxToPercent(34) / 2f;

        x = mEdgeSpan + mCircleRadius;

        mPaintFlags = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;
        mPaint = new Paint();
    }

    private int getMeasureSize(int mode, int size)
    {
        int result;
        if (mode == MeasureSpec.AT_MOST)
        {
            result = PixelPercentUtil.WidthPxToPercent(60);
            if (result > size)
            {
                result = size;
            }
        }
        else
        {
            result = size;
        }

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(getMeasureSize(widthMode, widthSize), getMeasureSize(heightMode, heightSize));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        x = calTouchX(event);
        invalidate();
        return true;
    }

    private float calTouchX(MotionEvent event)
    {
        float x = event.getX();
        if (x < mEdgeSpan + mCircleRadius)
        {
            x = mEdgeSpan + mCircleRadius;
        }
        else if (x > mViewW - mEdgeSpan - mCircleRadius)
        {
            x = mViewW - mEdgeSpan - mCircleRadius;
        }

        return x;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        mViewW = w;
        mViewH = h;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();

        // 选中的进度条部分
        mPaint.reset();
        mPaint.setFlags(mPaintFlags);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mSelectedProgressWidth);
        mPaint.setColor(mSelectedProgressColor);
        mPaint.setShadowLayer(PixelPercentUtil.WidthPxToPercent(8), 0, 0, ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.1f)));

        float start_x = mEdgeSpan;
        float end_x = x;
        canvas.drawLine(start_x, mViewH / 2f, end_x, mViewH / 2f, mPaint);

        // 未选中的进度条部分
        start_x = end_x;
        end_x = mViewW - mEdgeSpan;
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setColor(mProgressColor);
        canvas.drawLine(start_x, mViewH / 2f, end_x, mViewH / 2f, mPaint);

        // 控制点
        mPaint.reset();
        mPaint.setFlags(mPaintFlags);
        mPaint.setColor(mSelectedProgressColor);
        mPaint.setShadowLayer(PixelPercentUtil.WidthPxToPercent(8), 0, 0, ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.1f)));
        canvas.drawCircle(x, mViewH / 2f, mCircleRadius, mPaint);

        canvas.restore();
    }
}
