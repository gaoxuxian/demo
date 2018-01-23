package xx.demo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.view.MotionEvent;
import android.view.View;

import xx.demo.R;
import xx.demo.util.CameraPercentUtil;
import xx.demo.util.ShareData;

/**
 * Created by admin on 2018/1/23.
 */

public class ARWishView extends View
{
    private Bitmap mBitmap;
    private Bitmap mPackupBmp;

    private BitmapShader mBmpShader;

    private Paint mPaint;
    private Paint mPackupPaint;
    private float mRadius;

    private int mBmpOrgWH;
    private int mBmpSmlWH;

    private int mBmpOrgLocal;
    private int mBmpSmlLocal;

    private float mCircleX, mCircleY;

    private boolean mUIEnable = true;
    private boolean mDoingAnim;
    private Matrix mMatrix;
    private boolean isShow;

    private RectF mRectF;
    private RectF mShowRectF;
    private boolean mCanNarrow;
    private boolean mCanShow;

    public ARWishView(Context context)
    {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPackupPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mBmpOrgWH = CameraPercentUtil.WidthPxToPercent(434);
        mBmpSmlWH = CameraPercentUtil.WidthPxToPercent(118);

        mBmpOrgLocal = CameraPercentUtil.WidthPxToPercent(140 + 80 + 50 + 90 + 217);
        mBmpSmlLocal = CameraPercentUtil.WidthPxToPercent(100 + 32 + 60);

        mPackupBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ar_find_wish_pack);

        mMatrix = new Matrix();
    }

    public void setParams(Bitmap wish_logo, Bitmap tou_xiang, String local_msg, String user_name)
    {
        if (wish_logo == null || tou_xiang == null || local_msg == null || user_name == null) return;

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

        // 上方圆
        Matrix matrix = new Matrix();
        float x = (mBitmap.getWidth() - CameraPercentUtil.WidthPxToPercent(434) * 1f) / 2f;
        float y = CameraPercentUtil.WidthPxToPercent(92);
        float scale = CameraPercentUtil.WidthPxToPercent(434) * 1f / wish_logo.getWidth();
        matrix.postScale(scale, scale);
        matrix.postTranslate(x, y);
        canvas.drawBitmap(wish_logo, matrix, paint);

        // 位置信息
        String text = "超过十四个字后面就是省略号嗯额哦无";
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
        x = (mBitmap.getWidth() - textW - CameraPercentUtil.WidthPxToPercent(38) * 1f) / 2f + CameraPercentUtil.WidthPxToPercent(38) * 1f;
        y = CameraPercentUtil.WidthPxToPercent(92 + 434 + 36) - fontMetrics.top;
        canvas.drawText(text, x, y, paint);

        // 位置 logo
        x -= CameraPercentUtil.WidthPxToPercent(38);
        y = CameraPercentUtil.WidthPxToPercent(92 + 434 + 36) - (fontMetrics.top - fontMetrics.ascent);
        matrix.reset();
        scale = CameraPercentUtil.WidthPxToPercent(32) * 1f / localBmp.getWidth();
        matrix.postScale(scale, scale);
        matrix.postTranslate(x, y);
        canvas.drawBitmap(localBmp, matrix, paint);

        // 用户名称
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

        // 用户头像 logo
        BitmapShader shader = new BitmapShader(tou_xiang, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        matrix.reset();
        scale = CameraPercentUtil.WidthPxToPercent(68) * 1f / tou_xiang.getWidth();
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

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mBitmap != null)
        {
            mRectF = new RectF();
            mRectF.set((w - mBitmap.getWidth()) / 2f, (h - mBitmap.getHeight()) / 2f, (w + mBitmap.getWidth()) / 2f, (h + mBitmap.getHeight()) / 2f);

            mShowRectF = new RectF();
            mShowRectF.set((w - CameraPercentUtil.WidthPxToPercent(120)) / 2f, CameraPercentUtil.WidthPxToPercent(132), (w + CameraPercentUtil.WidthPxToPercent(120)) / 2f, CameraPercentUtil.WidthPxToPercent(252));
        }
    }

    public void zanding()
    {
        if (mDoingAnim || isShow) return;

        mUIEnable = false;
        mDoingAnim = true;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(250);
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
                float start = CameraPercentUtil.WidthPxToPercent(217) * mBmpSmlWH * 1f / CameraPercentUtil.WidthPxToPercent(434) + CameraPercentUtil.WidthPxToPercent(3);
                mRadius = start + (CameraPercentUtil.WidthPxToPercent(540) * 1f - start) * value;
                mCircleY = local;
                invalidate();
            }
        });

        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mUIEnable = true;
                mDoingAnim = false;
                isShow = true;
                invalidate();
            }
        });
        animator.start();
    }

    public void narrow()
    {
        if (mDoingAnim) return;

        mUIEnable = false;
        isShow = false;
        mDoingAnim = true;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(250);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();

                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                Matrix matrix = new Matrix();
                float local = mBmpOrgLocal + (mBmpSmlLocal - mBmpOrgLocal) * value;
                float w = mBmpOrgWH + (mBmpSmlWH - mBmpOrgWH) * value;
                float scale = w * 1f / CameraPercentUtil.WidthPxToPercent(434);
                float y = local - CameraPercentUtil.WidthPxToPercent(92 + 217) * scale;
                float x = (ShareData.m_screenRealWidth * 1f - mBitmap.getWidth() * scale) / 2f;
                matrix.postScale(scale, scale);
                matrix.postTranslate(x, y);

                mBmpShader.setLocalMatrix(matrix);
                mPaint.setShader(mBmpShader);
                float start = CameraPercentUtil.WidthPxToPercent(540);
                mRadius = start + (CameraPercentUtil.WidthPxToPercent(217) * scale + CameraPercentUtil.WidthPxToPercent(3) * 1f - start) * value;
                mCircleY = local;
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mUIEnable = true;
                mDoingAnim = false;
            }
        });
        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mUIEnable)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    if (isShow)
                    {
                        if (mRectF != null && !mRectF.contains(event.getX(), event.getY()))
                        {
                            mCanNarrow = true;
                        }
                    }
                    else
                    {
                        if (mShowRectF != null && mShowRectF.contains(event.getX(), event.getY()))
                        {
                            mCanShow = true;
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_UP:
                {
                    if (isShow)
                    {
                        if (mCanNarrow && mRectF != null && !mRectF.contains(event.getX(), event.getY()))
                        {
                            narrow();
                        }
                    }
                    else
                    {
                        if (mCanShow && mShowRectF != null && mShowRectF.contains(event.getX(), event.getY()))
                        {
                            zanding();
                        }
                    }
                    mCanShow = false;
                    mCanNarrow = false;
                    break;
                }
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

        canvas.drawCircle(mCircleX, mCircleY, mRadius, mPaint);

        if (isShow)
        {
            mMatrix.reset();
            int packupBmpWH = CameraPercentUtil.WidthPxToPercent(80);
            float scale = packupBmpWH *1f / mPackupBmp.getWidth();
            mMatrix.postScale(scale, scale);
            mMatrix.postTranslate((getMeasuredWidth() - packupBmpWH)/2f, CameraPercentUtil.WidthPxToPercent(140));
            canvas.drawBitmap(mPackupBmp, mMatrix, mPackupPaint);
        }

        canvas.restore();
    }
}
