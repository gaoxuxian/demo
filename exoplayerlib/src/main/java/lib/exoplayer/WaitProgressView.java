package lib.exoplayer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.view.View;

import util.PxUtil;

/**
 * 模仿系统 progress view 效果，具体参数，可定制
 * Created by Gxx on 2018/2/12.
 */

public class WaitProgressView extends View
{
    private Path mProgressPath;
    private Path mTempPath;
    private PathMeasure mMeasure;
    private Paint mPaint;
    private int mPaintFlags;

    private int mProgressWidth;
    private int mViewDefWH;
    private int mProgressColor;
    private long mPeriodDuration;

    private boolean mShow;
    private float mAnimValues;
    private ValueAnimator mAnim;

    public WaitProgressView(Context context)
    {
        super(context);
        mTempPath = new Path();
        mPaint = new Paint();
        mMeasure = new PathMeasure();
        mPaintFlags = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;
        mProgressWidth = PxUtil.sU_1080p(12);
        mViewDefWH = PxUtil.sU_1080p(150);
        mProgressColor = Color.WHITE;
        mPeriodDuration = 1500;
    }

    public void setProgressWidth(int width)
    {
        mProgressWidth = width;
    }

    public void setProgressColor(int color)
    {
        mProgressColor = color;
    }

    /**
     *
     * @param period_duration 每个周期持续时间
     */
    public void setPeriodDuration(long period_duration)
    {
        mPeriodDuration = period_duration;
    }

    public void show(boolean show)
    {
        mShow = show;

        if (show)
        {
            mAnimValues = 0;
            mAnim = ValueAnimator.ofFloat(0, 1);
            mAnim.setDuration(mPeriodDuration);
            mAnim.setRepeatCount(ValueAnimator.INFINITE);
            mAnim.setRepeatMode(ValueAnimator.RESTART);
            mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    mAnimValues = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnim.start();
        }
        else if (mAnim != null)
        {
            mAnim.cancel();
            mAnim.removeAllUpdateListeners();
            mAnim.removeAllListeners();
            mAnim = null;
        }

        invalidate();
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

    private int getMeasureSize(int mode, int size)
    {
        int result;
        if (mode == MeasureSpec.AT_MOST)
        {
            result = mViewDefWH;
            if (result > size)
            {
                result = size;
            }
        }
        else if (size == 0)
        {
            result = mViewDefWH;
        }
        else
        {
            result = size;
        }

        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        initPath(w, h);
    }

    private void initPath(int w, int h)
    {
        mProgressPath = new Path();
        float radius = Math.max(0.5f , Math.min(w / 2f, h / 2f) - mProgressWidth);
        RectF rectF = new RectF(-radius, -radius, radius, radius);
        mProgressPath.addArc(rectF, 0, 360);

        mPaint.reset();
        mPaint.setFlags(mPaintFlags);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setColor(mProgressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mShow && mProgressPath != null)
        {
            canvas.save();
            canvas.rotate(-90, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f);
            canvas.translate(getMeasuredWidth() / 2f, getMeasuredHeight() / 2f);
            mTempPath.reset();
            mMeasure.setPath(mProgressPath, false);
            float stop = mMeasure.getLength() * mAnimValues;
            float start = (float) (stop - ((0.5 - Math.abs(mAnimValues - 0.5)) * mMeasure.getLength() * 0.4f));
            mMeasure.getSegment(start, stop, mTempPath, true);
            canvas.drawPath(mTempPath, mPaint);
            canvas.restore();
        }
    }
}
