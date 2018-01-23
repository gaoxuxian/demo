package xx.demo.view;

import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.graphics.ColorUtils;
import android.util.TypedValue;
import android.view.View;

import xx.demo.R;
import xx.demo.util.CameraPercentUtil;
import xx.demo.util.ShareData;

/**
 * Created by admin on 2018/1/23.
 */

public class ARWishViewV2 extends View
{
    private Bitmap mBitmap;

    private BitmapShader mBmpShader;

    private Paint mPaint;
    private float mRadius;

    private int mBmpOrgWH;
    private int mBmpSmlWH;

    private int mBmpOrgLocal;
    private int mBmpSmlLocal;

    private float mCircleX, mCircleY;
    private Bitmap mCircleBmp;

    public ARWishViewV2(Context context)
    {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mBmpOrgWH = CameraPercentUtil.WidthPxToPercent(434);
        mBmpSmlWH = CameraPercentUtil.WidthPxToPercent(118);

        mBmpOrgLocal = CameraPercentUtil.WidthPxToPercent(140 + 80 + 50 + 90 + 217);
        mBmpSmlLocal = CameraPercentUtil.WidthPxToPercent(100 + 32 + 60);
    }

    public void setBitmap(int res, int touXiang)
    {
        mCircleBmp = BitmapFactory.decodeResource(getResources(), res);

        Bitmap touXiangBmp = BitmapFactory.decodeResource(getResources(), touXiang);

        Bitmap localBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ar_find_wish_local);

        // 宽高要比设计稿都大一点
        int bitmapW = CameraPercentUtil.WidthPxToPercent(572);
        int bitmapH = CameraPercentUtil.WidthPxToPercent(744);

        mBitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);

        canvas.save();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        int start = CameraPercentUtil.WidthPxToPercent(2);
        RectF rectF = new RectF(start, start, mBitmap.getWidth() - start, mBitmap.getHeight() - start);
        canvas.drawRoundRect(rectF, CameraPercentUtil.WidthPxToPercent(30), CameraPercentUtil.WidthPxToPercent(30), paint);

        // 第一行文字
        String text = "超过十四个字后面就是省略号嗯额哦无";
        text = "泰恒大厦";
        int textSize = text.length();
        if (textSize > 14)
        {
            text = text.substring(0, 14) + "...";
        }
        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));
        paint.setColor(0xff4c4c4c);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        Rect textBound = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBound);
        float textH = textBound.height();
        float textW = paint.measureText(text);
        float x = (mBitmap.getWidth() - textW - CameraPercentUtil.WidthPxToPercent(38) * 1f) / 2f + CameraPercentUtil.WidthPxToPercent(38) * 1f;
        float y = CameraPercentUtil.WidthPxToPercent(92 + 434 + 36) - fontMetrics.top;
        canvas.drawText(text, x, y, paint);

        // local logo
        x -= CameraPercentUtil.WidthPxToPercent(38);
        y = CameraPercentUtil.WidthPxToPercent(92 + 434 + 36) - (fontMetrics.top - fontMetrics.ascent);
        Matrix matrix = new Matrix();
        float scale = CameraPercentUtil.WidthPxToPercent(32) * 1f / localBmp.getWidth();
        matrix.postScale(scale, scale);
        matrix.postTranslate(x, y);
        canvas.drawBitmap(localBmp, matrix, paint);

        // 第二行文字,用户名称
        text = "大猪佩奇有梦要想";
        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 17, getResources().getDisplayMetrics()));
        paint.setColor(0xff333333);
        fontMetrics = paint.getFontMetrics();
        textW = paint.measureText(text);
        x = (mBitmap.getWidth() - textW - CameraPercentUtil.WidthPxToPercent(16 + 68) * 1f) / 2f + CameraPercentUtil.WidthPxToPercent(16 + 68);
        y = CameraPercentUtil.WidthPxToPercent(92 + 434 + 36 + 44) + textH - fontMetrics.ascent;
        canvas.drawText(text, x, y, paint);

        // 用户头像
        BitmapShader shader = new BitmapShader(touXiangBmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        matrix.reset();
        scale = CameraPercentUtil.WidthPxToPercent(68) * 1f / touXiangBmp.getWidth();
        x -= CameraPercentUtil.WidthPxToPercent(16 + 68);
        y = mBitmap.getHeight() - CameraPercentUtil.WidthPxToPercent(55 + 68 + 2);
        matrix.postScale(scale, scale);
        matrix.postTranslate(x, y);
        shader.setLocalMatrix(matrix);
        paint.reset();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setShader(shader);
        float radius = CameraPercentUtil.WidthPxToPercent(68) / 2f;
        canvas.drawCircle(x + radius, y + radius, radius, paint);

        canvas.restore();

        mBmpShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        matrix.reset();
        scale = CameraPercentUtil.WidthPxToPercent(118) * 1f / CameraPercentUtil.WidthPxToPercent(434);
        float w = bitmapW * scale;
        x = (ShareData.m_screenRealWidth * 1f - w) / 2f;
        y = CameraPercentUtil.WidthPxToPercent(192) - CameraPercentUtil.WidthPxToPercent(92 + 217) * scale;
        matrix.postScale(scale, scale);
        matrix.postTranslate(x, y);
        mBmpShader.setLocalMatrix(matrix);
        mPaint.setShader(mBmpShader);

        mRadius = CameraPercentUtil.WidthPxToPercent(217) * scale + CameraPercentUtil.WidthPxToPercent(3);

        mCircleX = ShareData.m_screenRealWidth / 2f;
        mCircleY = CameraPercentUtil.WidthPxToPercent(192);
    }

    public void test()
    {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();

                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                Matrix matrix = new Matrix();
                float local = mBmpSmlLocal + (mBmpOrgLocal - mBmpSmlLocal) * value;
                float w = mBmpSmlWH + (mBmpOrgWH - mBmpSmlWH) * value;
                float scale = w * 1f / CameraPercentUtil.WidthPxToPercent(434);
                float y = local - CameraPercentUtil.WidthPxToPercent(92 + 217) * scale;
                float x = (ShareData.m_screenRealWidth * 1f - mBitmap.getWidth() * scale) / 2f;
                matrix.postScale(scale, scale);
                matrix.postTranslate(x, y);

                mBmpShader.setLocalMatrix(matrix);
                mPaint.setShader(mBmpShader);
                mRadius = CameraPercentUtil.WidthPxToPercent(217) * scale + CameraPercentUtil.WidthPxToPercent(3);
                mCircleY = local;
                invalidate();
            }
        });

        ValueAnimator animator1 = ValueAnimator.ofFloat(0, 1);
        animator1.setDuration(500);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();
                int start = CameraPercentUtil.WidthPxToPercent(217) + CameraPercentUtil.WidthPxToPercent(3);
                mRadius = start + (CameraPercentUtil.WidthPxToPercent(600) - CameraPercentUtil.WidthPxToPercent(217)) * value;
                invalidate();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(animator).before(animator1);
        set.start();
    }

    public void test3()
    {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();

                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                Matrix matrix = new Matrix();
                float local = mBmpSmlLocal + (mBmpOrgLocal - mBmpSmlLocal) * value;
                float scale = mBmpSmlWH *1f / CameraPercentUtil.WidthPxToPercent(434);
                float y = local - CameraPercentUtil.WidthPxToPercent(92 + 217) * scale;
                float x = (ShareData.m_screenRealWidth * 1f - mBitmap.getWidth() * scale) / 2f;
                matrix.postScale(scale, scale);
                matrix.postTranslate(x, y);

                mBmpShader.setLocalMatrix(matrix);
                mPaint.setShader(mBmpShader);
                mRadius = CameraPercentUtil.WidthPxToPercent(217) * scale + CameraPercentUtil.WidthPxToPercent(3);
                mCircleY = local;
                invalidate();
            }
        });

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();

                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                Matrix matrix = new Matrix();
                float local = mBmpSmlLocal + (CameraPercentUtil.WidthPxToPercent(140 + 80 + 50 + 700) - mBmpSmlLocal) * 0.5f;
                float w = mBmpSmlWH + (mBmpOrgWH - mBmpSmlWH) * value;
                float scale = w * 1f / CameraPercentUtil.WidthPxToPercent(434);
                float y = local - CameraPercentUtil.WidthPxToPercent(92 + 217) * scale;
                float x = (ShareData.m_screenRealWidth * 1f - mBitmap.getWidth() * scale) / 2f;
                matrix.postScale(scale, scale);
                matrix.postTranslate(x, y);

                mBmpShader.setLocalMatrix(matrix);
                mPaint.setShader(mBmpShader);
                float start = CameraPercentUtil.WidthPxToPercent(217) * mBmpSmlWH * 1f / CameraPercentUtil.WidthPxToPercent(434) + CameraPercentUtil.WidthPxToPercent(3);
                mRadius = start + (CameraPercentUtil.WidthPxToPercent(600) - start) * value;
                invalidate();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(valueAnimator, animator);
        set.start();
    }

    // 水滴
    public void test2()
    {
        Keyframe[] key = new Keyframe[3];
        key[0] = Keyframe.ofFloat(0f, 0);
        key[1] = Keyframe.ofFloat(0.67f, 1f);
        key[2] = Keyframe.ofFloat(1, 0.5f);

        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe("", key);
        ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(pvhTranslateX);
        valueAnimator.setDuration(600);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();

                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                Matrix matrix = new Matrix();
                float local = mBmpSmlLocal + (CameraPercentUtil.WidthPxToPercent(140 + 80 + 50 + 700) - mBmpSmlLocal) * value;
                float scale = mBmpSmlWH * 1f / CameraPercentUtil.WidthPxToPercent(434);
                float y = local - CameraPercentUtil.WidthPxToPercent(92 + 217) * scale;
                float x = (ShareData.m_screenRealWidth * 1f - mBitmap.getWidth() * scale) / 2f;
                matrix.postScale(scale, scale);
                matrix.postTranslate(x, y);

                mBmpShader.setLocalMatrix(matrix);
                mPaint.setShader(mBmpShader);
                mRadius = CameraPercentUtil.WidthPxToPercent(217) * scale + CameraPercentUtil.WidthPxToPercent(3);
                mCircleY = local;
                invalidate();
            }
        });

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();

                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                Matrix matrix = new Matrix();
                float local = mBmpSmlLocal + (CameraPercentUtil.WidthPxToPercent(140 + 80 + 50 + 700) - mBmpSmlLocal) * 0.5f;
                float w = mBmpSmlWH + (mBmpOrgWH - mBmpSmlWH) * value;
                float scale = w * 1f / CameraPercentUtil.WidthPxToPercent(434);
                float y = local - CameraPercentUtil.WidthPxToPercent(92 + 217) * scale;
                float x = (ShareData.m_screenRealWidth * 1f - mBitmap.getWidth() * scale) / 2f;
                matrix.postScale(scale, scale);
                matrix.postTranslate(x, y);

                mBmpShader.setLocalMatrix(matrix);
                mPaint.setShader(mBmpShader);
                float start = CameraPercentUtil.WidthPxToPercent(217) * mBmpSmlWH * 1f / CameraPercentUtil.WidthPxToPercent(434) + CameraPercentUtil.WidthPxToPercent(3);
                mRadius = start + (CameraPercentUtil.WidthPxToPercent(600) - start) * value;
                invalidate();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(valueAnimator, animator);
        set.start();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.save();
        canvas.drawColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.5f)));

        canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaint);

        canvas.restore();
    }
}
