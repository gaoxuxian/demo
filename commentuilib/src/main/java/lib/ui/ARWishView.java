package lib.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.graphics.ColorUtils;
import android.view.MotionEvent;
import android.view.View;

import util.PixelPercentUtil;
import util.ShareData;

/**
 *
 * Created by Gxx on 2018/1/23.
 */

public class ARWishView extends View
{
    private Matrix mMatrix;
    private Bitmap mBitmap;

    private int mPaintFlag;

    private Paint mBmpPaint;
    private BitmapShader mBmpShader;

    private Paint mPaint;
    private float mRadius;
    private float mWhiteBGRadius;

    private int mBigBmpWH;
    private int mSmallBmpWH;

    // bitmap circle params
    private int mSmallBmpCircleRadius;

    private int mViewW;
    private int mViewH;

    private float mCircleX, mCircleY;

    private RectF mRoundRect;

    private final PorterDuffXfermode dst_atop_mode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);

    private boolean mIsFangDa;

    public ARWishView(Context context)
    {
        super(context);

        mPaintFlag = Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG;
        mPaint = new Paint(mPaintFlag);
        mBmpPaint = new Paint(mPaintFlag);
        mMatrix = new Matrix();
        mRoundRect = new RectF();

        mViewH = ShareData.m_screenRealHeight;
        mViewW = ShareData.m_screenRealWidth;
        mBigBmpWH = PixelPercentUtil.WidthPxToPercent(434);
        mSmallBmpWH = PixelPercentUtil.WidthPxToPercent(118);
        mSmallBmpCircleRadius = PixelPercentUtil.WidthPxToPercent(60);

    }
    
    public void setBitmap(Object bitmap)
    {
        if (bitmap == null) return;
        
        if (bitmap instanceof Integer)
        {
            mBitmap = BitmapFactory.decodeResource(getResources(), (int) bitmap);
        }
        else if (bitmap instanceof String)
        {
            mBitmap = BitmapFactory.decodeFile((String) bitmap);
        }
        else if (bitmap instanceof Bitmap && !((Bitmap) bitmap).isRecycled())
        {
            mBitmap = (Bitmap) bitmap;
        }
        
        if (mBitmap != null)
        {
            mBmpShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            float scale = mSmallBmpWH * 1f / mBitmap.getWidth();
            float x = mViewW * 1f / 2f - mSmallBmpCircleRadius;
            float y = PixelPercentUtil.WidthPxToPercent(100 + 32);
            mMatrix.postScale(scale, scale);
            mMatrix.postTranslate(x, y);
            mBmpShader.setLocalMatrix(mMatrix);
            mBmpPaint.setShader(mBmpShader);

            mWhiteBGRadius = mSmallBmpCircleRadius;
            mRoundRect.set(mViewW * 1f / 2f - mSmallBmpCircleRadius, PixelPercentUtil.WidthPxToPercent(100 + 32), mViewW * 1f / 2f + mSmallBmpCircleRadius, PixelPercentUtil.WidthPxToPercent(100 + 32 + 120));
            mRadius = mWhiteBGRadius - PixelPercentUtil.WidthPxToPercent(2);

            mCircleX = mViewW / 2f;
            mCircleY = PixelPercentUtil.WidthPxToPercent(100 + 32 + 60);
        }
    }

    public void fangda()
    {
        mIsFangDa = true;
        final float new_x = (mViewW - mBigBmpWH) / 2f;
        final float new_y = PixelPercentUtil.WidthPxToPercent(240 + 122);

        final float old_x = (mViewW - mSmallBmpWH) / 2f;
        final float old_y = PixelPercentUtil.WidthPxToPercent(100 + 32);

        final float old_scale = mSmallBmpWH * 1f / mBitmap.getWidth();
        final float new_scale = mBigBmpWH * 1f / mBitmap.getWidth();

        final float old_center_x = mViewW / 2f;
        final float old_center_y = PixelPercentUtil.WidthPxToPercent(100 + 32 + 60);

        final float new_center_x = mViewW / 2f;
        final float new_center_y = PixelPercentUtil.WidthPxToPercent(240 + 122 + 217);

        final float old_circle_radius = mSmallBmpWH * 1f / 2f;
        final float new_circle_radius = mBigBmpWH * 1f / 2f;

        final float old_white_bg_round_rect_radius = mSmallBmpCircleRadius;
        final float new_white_bg_round_rect_radius = PixelPercentUtil.WidthPxToPercent(30);

        final float old_left = mViewW * 1f / 2f - mSmallBmpCircleRadius;
        final float old_right = mViewW * 1f / 2f + mSmallBmpCircleRadius;
        final float old_top = PixelPercentUtil.WidthPxToPercent(100 + 32);
        final float old_bottom = PixelPercentUtil.WidthPxToPercent(100 + 32 + 120);

        final float new_left = (mViewW * 1f - PixelPercentUtil.WidthPxToPercent(568)) / 2f;
        final float new_right = (mViewW * 1f + PixelPercentUtil.WidthPxToPercent(568)) / 2f;
        final float new_top = PixelPercentUtil.WidthPxToPercent(240);
        final float new_bottom = PixelPercentUtil.WidthPxToPercent(240 + 680);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();
                float x = old_x + (new_x - old_x) * value;
                float y = old_y + (new_y - old_y) * value;
                float scale = old_scale + (new_scale - old_scale) * value;

                mMatrix.reset();
                mMatrix.postScale(scale, scale);
                mMatrix.postTranslate(x, y);
                mBmpShader.setLocalMatrix(mMatrix);
                mBmpPaint.reset();
                mBmpPaint.setFlags(mPaintFlag);
                mBmpPaint.setShader(mBmpShader);

                mRadius = old_circle_radius + (new_circle_radius - old_circle_radius) * value;
                mCircleX = old_center_x + (new_center_x - old_center_x) * value;
                mCircleY = old_center_y + (new_center_y - old_center_y) * value;

                float left = old_left + (new_left - old_left) * value;
                float right = old_right + (new_right - old_right) * value;
                float top = old_top + ((new_top - old_top) * value);
                float bottom = old_bottom + (new_bottom - old_bottom) * value;
                mRoundRect.set(left, top, right, bottom);

                mWhiteBGRadius = old_white_bg_round_rect_radius + (new_white_bg_round_rect_radius - old_white_bg_round_rect_radius) * value;

                invalidate();
            }
        });
        anim.setDuration(400);
        anim.start();
    }

    public void suoxiao()
    {
        mIsFangDa = false;
        final float new_x = (mViewW - mSmallBmpWH) / 2f;
        final float new_y = PixelPercentUtil.WidthPxToPercent(100 + 32);

        final float old_x = (mViewW - mBigBmpWH) / 2f;
        final float old_y = PixelPercentUtil.WidthPxToPercent(240 + 122);

        final float new_scale = mSmallBmpWH * 1f / mBitmap.getWidth();
        final float old_scale = mBigBmpWH * 1f / mBitmap.getWidth();

        final float old_center_x = mViewW / 2f;
        final float old_center_y = PixelPercentUtil.WidthPxToPercent(240 + 122 + 217);

        final float new_center_x = mViewW / 2f;
        final float new_center_y = PixelPercentUtil.WidthPxToPercent(100 + 32 + 60);

        final float old_circle_radius = mBigBmpWH * 1f / 2f;
        final float new_circle_radius = mSmallBmpWH * 1f / 2f;

        final float new_white_bg_round_rect_radius = mSmallBmpCircleRadius;
        final float old_white_bg_round_rect_radius = PixelPercentUtil.WidthPxToPercent(30);

        final float new_left = mViewW * 1f / 2f - mSmallBmpCircleRadius;
        final float new_right = mViewW * 1f / 2f + mSmallBmpCircleRadius;
        final float new_top = PixelPercentUtil.WidthPxToPercent(100 + 32);
        final float new_bottom = PixelPercentUtil.WidthPxToPercent(100 + 32 + 120);

        final float old_left = (mViewW * 1f - PixelPercentUtil.WidthPxToPercent(568)) / 2f;
        final float old_right = (mViewW * 1f + PixelPercentUtil.WidthPxToPercent(568)) / 2f;
        final float old_top = PixelPercentUtil.WidthPxToPercent(240);
        final float old_bottom = PixelPercentUtil.WidthPxToPercent(240 + 680);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();
                float x = old_x + (new_x - old_x) * value;
                float y = old_y + (new_y - old_y) * value;
                float scale = old_scale + (new_scale - old_scale) * value;

                mMatrix.reset();
                mMatrix.postScale(scale, scale);
                mMatrix.postTranslate(x, y);
                mBmpShader.setLocalMatrix(mMatrix);
                mBmpPaint.reset();
                mBmpPaint.setFlags(mPaintFlag);
                mBmpPaint.setShader(mBmpShader);

                mRadius = old_circle_radius + (new_circle_radius - old_circle_radius) * value;
                mCircleX = old_center_x + (new_center_x - old_center_x) * value;
                mCircleY = old_center_y + (new_center_y - old_center_y) * value;

                float left = old_left + (new_left - old_left) * value;
                float right = old_right + (new_right - old_right) * value;
                float top = old_top + ((new_top - old_top) * value);
                float bottom = old_bottom + (new_bottom - old_bottom) * value;
                mRoundRect.set(left, top, right, bottom);

                mWhiteBGRadius = old_white_bg_round_rect_radius + (new_white_bg_round_rect_radius - old_white_bg_round_rect_radius) * value;

                invalidate();
            }
        });
        anim.setDuration(400);
        anim.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if (mIsFangDa)
            {
                suoxiao();
            }
            else
            {
                fangda();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.save();
        canvas.drawColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.5f)));
        canvas.restore();

        if (mBitmap != null)
        {
            int save = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

            canvas.drawCircle(mCircleX, mCircleY, mRadius, mBmpPaint);

            mPaint.reset();
            mPaint.setFlags(mPaintFlag);
            mPaint.setColor(Color.WHITE);
            mPaint.setXfermode(dst_atop_mode);
            canvas.drawRoundRect(mRoundRect, mWhiteBGRadius, mWhiteBGRadius, mPaint);

            canvas.restoreToCount(save);
        }
    }
}
