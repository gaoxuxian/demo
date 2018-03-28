package lib.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import lib.util.PixelPercentUtil;

/**
 * 聚焦
 * Created by Gxx on 2018/1/17.
 */

public class FocusView extends View
{
    private Paint mPaint;
    private int mPaintFlags;

//     view params
    private int mViewWH;
    private int mViewDefWH;

    // circle params
    private float mCircleX;
    private float mCircleY;
    private float mRadius;
    private int mStrokeWidth;
    private int mStrokeColor;

    // 中间太阳参数
    private int mSunLogoRadius;
    private int mSunLineLength;
    private int mSunSpan;

    private AnimatorSet mAnim;
    private boolean mShowSun;
    private boolean mShowCircle;

    public FocusView(Context context)
    {
        super(context);
        mPaintFlags = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;
        mPaint = new Paint(mPaintFlags);

        initDefParams();
    }

    private void initDefParams()
    {
        mViewDefWH = PixelPercentUtil.WidthPxToPercent(110);
        mRadius = PixelPercentUtil.WidthPxToPercent(110) /2f;
        mStrokeWidth = PixelPercentUtil.WidthPxToPercent(2);
        mStrokeColor = 0xfff8f09a;

        mSunLogoRadius = PixelPercentUtil.WidthPxToPercent(4);
        mSunLineLength = PixelPercentUtil.WidthPxToPercent(4);
        mSunSpan = PixelPercentUtil.WidthPxToPercent(2);
    }

    public void setCircleXY(float x, float y)
    {
        mCircleX = x;
        mCircleY = y;
    }

    public void setCircleParams(float radius, int strokeWidth)
    {
        mViewDefWH = (int) ((radius + strokeWidth) * 2 + 0.5f);
        mRadius = radius;
        mStrokeWidth = strokeWidth;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int with_mode = MeasureSpec.getMode(widthMeasureSpec);
        int with_size = MeasureSpec.getSize(widthMeasureSpec);

        int height_mode = MeasureSpec.getMode(heightMeasureSpec);
        int height_size = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(getMeasureSize(with_mode, with_size), getMeasureSize(height_mode, height_size));
    }

    protected int getMeasureSize(int mode, int size)
    {
        return mViewDefWH + mStrokeWidth * 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWH = w;
        mRadius = (mViewWH - mStrokeWidth * 2) / 2f;
        setPivotX(mViewWH / 2f);
        setPivotY(mViewWH / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (mShowCircle)
        {
            canvas.save();
            canvas.translate(mCircleX, mCircleY);
            mPaint.reset();
            mPaint.setFlags(mPaintFlags);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setColor(mStrokeColor);
            canvas.drawCircle(0, 0, mRadius, mPaint);

            if (mShowSun)
            {
                mPaint.reset();
                mPaint.setFlags(mPaintFlags);
                mPaint.setStrokeWidth(0);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mStrokeColor);
                canvas.drawCircle(0, 0, mSunLogoRadius, mPaint);
                drawSunLine(canvas);
                canvas.rotate(45);
                drawSunLine(canvas);
            }

            canvas.restore();
        }
    }

    private void drawSunLine(Canvas canvas)
    {
        mPaint.reset();
        mPaint.setFlags(mPaintFlags);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mStrokeColor);
        canvas.drawLine(0, -(mSunLogoRadius + mSunSpan + mSunLineLength), 0, -(mSunLogoRadius + mSunSpan), mPaint);
        canvas.drawLine(0, mSunLogoRadius + mSunSpan, 0, mSunLogoRadius + mSunSpan + mSunLineLength, mPaint);

        canvas.drawLine(-(mSunLogoRadius + mSunSpan + mSunLineLength), 0, -(mSunLogoRadius + mSunSpan), 0, mPaint);
        canvas.drawLine(mSunLogoRadius + mSunSpan, 0, mSunLogoRadius + mSunSpan + mSunLineLength, 0, mPaint);
    }

    public void showFocus(boolean show)
    {
        cancelAnim();
        setAlpha(1);
        setScaleX(1);
        setScaleY(1);
        setShowPart(show, show);
    }

    private void setShowPart(boolean circle, boolean sun)
    {
        mShowCircle = circle;
        mShowSun = sun;
        invalidate();
    }

    private void doLockFocusAlphaGradientAnim()
    {
        Keyframe keyframe1 = Keyframe.ofFloat(0, 1);
        Keyframe keyframe2 = Keyframe.ofFloat(0.75f, 1);
        Keyframe keyframe3 = Keyframe.ofFloat(1, 0.5f);
        PropertyValuesHolder holder = PropertyValuesHolder.ofKeyframe("alpha", keyframe1, keyframe2, keyframe3);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this, holder);
        mAnim = new AnimatorSet();
        mAnim.setDuration(1300);
        mAnim.playTogether(animator);
        mAnim.start();
    }

    public void doFingerUpAnim()
    {
        cancelAnim();

        float scaleX = getScaleX();
        float scaleY = getScaleY();

        float end = 1f;

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "scaleX", scaleX, end);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "scaleY", scaleY, end);

        mAnim = new AnimatorSet();
        mAnim.setDuration(400);
        mAnim.playTogether(animator1, animator2);
        mAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                setShowPart(true, true);
                doLockFocusAlphaGradientAnim();
            }
        });
        mAnim.start();
    }

    private void cancelAnim()
    {
        if (mAnim != null && (mAnim.isStarted() || mAnim.isRunning()))
        {
            mAnim.cancel();
        }
    }

    public void doLongPressAnim()
    {
        cancelAnim();

        setPivotX(mCircleX);
        setPivotY(mCircleY);

        float start = 2.2f;
        float end = 1.5f;
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "scaleX", start, end);
        animator1.setRepeatCount(1);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "scaleY", start, end);
        animator2.setRepeatCount(1);

        mAnim = new AnimatorSet();
        mAnim.setDuration(400);
        mAnim.playTogether(animator1, animator2);
        mAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                setAlpha(1);
                setShowPart(true, false);
            }
        });
        mAnim.start();
    }
}
