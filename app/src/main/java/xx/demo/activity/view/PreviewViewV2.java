package xx.demo.activity.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import lib.util.ImageUtil;
import lib.util.PixelPercentUtil;

public class PreviewViewV2 extends View
{
    private ValueAnimator mResetAnim;

    private interface TouchArea
    {
        int none = 0;
        int image = 1;
        int water_mark = 2;
    }

    private Matrix mOutsideMatrix;

    private Matrix mStatusRecordMatrix;

    private Matrix mTempMatrix;

    private Shape mImgShape;

    private Bitmap mImgBmp;

    private Shape mWaterMarkShape;

    private Bitmap mWaterMarkBmp;

    private boolean mEventLock;

    private PreviewViewConfig mConfig;

    private boolean mInit;

    private Paint mPaint;

    private float mDownX;
    private float mDownY;

    // 只支持两个手指的缩放，但多指操作无影响
    private float mPointer1DownX;
    private float mPointer1DownY;

    private float mPointer2DownX;
    private float mPointer2DownY;

    private int mTouchArea = TouchArea.none;

    private float mMinScale = 1f;
    private float mMaxScale = 3f;

    private boolean mInitSetWaterMatrix;

    private boolean mDoingAnim;

    private boolean mCancelWaterMarkClickEvent;

    public PreviewViewV2(Context context)
    {
        super(context);
        mInit = true;
        initData();
        setEventLock(false);
    }

    private void initData()
    {
        mImgShape = new Shape();
        mWaterMarkShape = new Shape();
        mTempMatrix = new Matrix();
        mOutsideMatrix = new Matrix();
        mStatusRecordMatrix = new Matrix();
        mPaint = new Paint();
    }

    public void setConfig(PreviewViewConfig config)
    {
        mConfig = config;
    }

    private void initDefaultConfig()
    {
        if (mConfig == null)
        {
            mConfig = new PreviewViewConfig();
            mConfig.mImageCenter = new Point();
            mConfig.mImageCenter.x = getMeasuredWidth() /2;
            mConfig.mImageCenter.y = getMeasuredHeight() /2;
        }
    }

    public void setEventLock(boolean lock)
    {
        mEventLock = lock;
    }

    public void setImage(Bitmap bitmap)
    {
        mImgBmp = bitmap;
    }

    public void setWaterMark(Bitmap bitmap)
    {
        mWaterMarkBmp = bitmap;

        if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0 || !isBitmapValid(mImgBmp))
        {
            mInitSetWaterMatrix = true;
            return;
        }

        if (isBitmapValid(mImgBmp) && isBitmapValid(mWaterMarkBmp))
        {
            float scale = Math.min((float) getMeasuredWidth() / mImgBmp.getWidth(), (float) getMeasuredHeight() / mImgBmp.getHeight());
            float y = (float) mConfig.mImageCenter.y - mImgBmp.getHeight() * scale /2f;

            mWaterMarkShape.mOwnMatrix.reset();
            float s = Math.min(mImgBmp.getWidth() * scale, mImgBmp.getHeight() * scale);
            float dx = PhotoMark.getLogoRight(s);
            float dy = PhotoMark.getLogoBottom(s, false);
            float watermarkW = PhotoMark.getLogoW(s);
            float waterScale = watermarkW / mWaterMarkBmp.getWidth();
            mWaterMarkShape.mOwnMatrix.postScale(waterScale,waterScale);
            y += mImgBmp.getHeight() * scale - dy - mWaterMarkBmp.getHeight() * waterScale;
            mWaterMarkShape.mOwnMatrix.postTranslate(dx, y);
        }
    }

    public void update()
    {
        invalidate();
    }

    public void setWaterMarkTranslationY(int y)
    {
        if (isBitmapValid(mWaterMarkBmp))
        {
            RectF waterRect = new RectF(0, 0, mWaterMarkBmp.getWidth(), mWaterMarkBmp.getHeight());
            RectF waterDst = new RectF();
            mWaterMarkShape.mCurrentStateMatrix.mapRect(waterDst, waterRect);

            if (y > 0)
            {
                if (waterDst.bottom >= getMeasuredHeight() - y)
                {
                    float dy = getMeasuredHeight() - y - waterDst.bottom;
                    ValueAnimator animator = ValueAnimator.ofFloat(0, dy);
                    animator.setDuration(300);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                    {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation)
                        {
                            float value = (float) animation.getAnimatedValue();
                            mWaterMarkShape.mExtraMatrix.reset();
                            mWaterMarkShape.mExtraMatrix.postTranslate(0, value);
                            invalidate();
                        }
                    });
                    animator.start();
                }
            }
            else if (y == 0)
            {
                mTempMatrix.reset();
                mWaterMarkShape.mExtraMatrix.reset();
                mixMatrix(mTempMatrix, mWaterMarkShape.mOwnMatrix, mOutsideMatrix, mWaterMarkShape.mExtraMatrix);

                RectF waterDst1 = new RectF();
                mTempMatrix.mapRect(waterDst1, waterRect);

                float dy = waterDst1.bottom - waterDst.bottom;
                ValueAnimator animator = ValueAnimator.ofFloat(dy, 0);
                animator.setDuration(300);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        float value = (float) animation.getAnimatedValue();
                        mWaterMarkShape.mExtraMatrix.reset();
                        mWaterMarkShape.mExtraMatrix.postTranslate(0, -value);
                        invalidate();
                    }
                });
                animator.start();
            }
        }
    }

    private void setStatusRecord(Matrix matrix)
    {
        if (matrix != null && mStatusRecordMatrix != null)
        {
            mStatusRecordMatrix.reset();
            mStatusRecordMatrix.set(matrix);
        }
    }

    private void syncStatus(Matrix dst)
    {
        if (dst != null && mStatusRecordMatrix != null)
        {
            dst.reset();
            dst.set(mStatusRecordMatrix);
        }
    }

    private int getTouchArea(float x, float y)
    {
        if (isBitmapValid(mWaterMarkBmp))
        {
            RectF imgRect = new RectF(0, 0, mWaterMarkBmp.getWidth(), mWaterMarkBmp.getHeight());
            RectF dst = new RectF();
            mWaterMarkShape.mCurrentStateMatrix.mapRect(dst, imgRect);
            if (dst.contains(x,y))
            {
                return TouchArea.water_mark;
            }
        }

        if (isBitmapValid(mImgBmp))
        {
            RectF imgRect = new RectF(0, 0, mImgBmp.getWidth(), mImgBmp.getHeight());
            RectF dst = new RectF();
            mImgShape.mCurrentStateMatrix.mapRect(dst, imgRect);
            if (dst.contains(x,y))
            {
                return TouchArea.image;
            }
        }
        return TouchArea.none;
    }

    private void compareImageScale(float x, float y, Matrix srcMatrix)
    {
        if (isBitmapValid(mImgBmp) && mTempMatrix != null && srcMatrix != null)
        {
            mTempMatrix.reset();
            mixMatrix(mTempMatrix, mImgShape.mOwnMatrix, srcMatrix);

            RectF viewRect = new RectF(0, 0, mImgBmp.getWidth(), mImgBmp.getHeight());
            RectF dst = new RectF();
            mTempMatrix.mapRect(dst, viewRect);

            RectF temp = getInitImageRect();
            float dstScale = 1f;
            float scale = dst.width() / temp.width();
            if (scale < mMinScale)
            {
                dstScale = mMinScale / scale;
            }
            else if (scale > mMaxScale)
            {
                dstScale = mMaxScale / scale;
            }

            if (dstScale != 1)
            {
                srcMatrix.postScale(dstScale, dstScale, x, y);
            }
        }
    }

    private void compareImageBorder(Matrix srcMatrix)
    {
        if (isBitmapValid(mImgBmp) && mTempMatrix != null && srcMatrix != null)
        {
            mTempMatrix.reset();
            mixMatrix(mTempMatrix, mImgShape.mOwnMatrix, srcMatrix);

            RectF viewRect = new RectF(0, 0, mImgBmp.getWidth(), mImgBmp.getHeight());
            RectF dst = new RectF();
            mTempMatrix.mapRect(dst, viewRect);

            // 存在误差，直接用整数
            int w = Math.round(dst.width());

            float dx = 0;
            float dy = 0;
            if (w >= getMeasuredWidth())
            {
                if (dst.left > 0)
                {
                    dx = 0 - dst.left;
                }
                else if (dst.right + dx < getMeasuredWidth())
                {
                    dx = getMeasuredWidth() - dst.right;
                }
            }
            else
            {
                if (dst.left < 0)
                {
                    dx = 0 - dst.left;
                }
                else if (dst.right > getMeasuredWidth())
                {
                    dx = getMeasuredWidth() - dst.right;
                }
            }

            int h = Math.round(dst.height());

            if (h >= getMeasuredHeight())
            {
                if (dst.top > 0)
                {
                    dy = 0 - dst.top;
                }
                else if (dst.bottom < getMeasuredHeight())
                {
                    dy = getMeasuredHeight() - dst.bottom;
                }
            }
            else
            {
                if (dst.top < 0)
                {
                    dy = 0 - dst.top;
                }
                else if (dst.bottom > getMeasuredHeight())
                {
                    dy = getMeasuredHeight() - dst.bottom;
                }
            }

            if (dx != 0 || dy != 0)
            {
                srcMatrix.postTranslate(dx, dy);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!mEventLock && isBitmapValid(mImgBmp))
        {
            switch (event.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                {
                    mCancelWaterMarkClickEvent = false;
                    mTouchArea = getTouchArea(event.getX(), event.getY());
                    if (mTouchArea == TouchArea.image)
                    {
                        if (mDoingAnim && mResetAnim != null)
                        {
                            mResetAnim.cancel();
                        }

                        setStatusRecord(mOutsideMatrix);
                        mDownX = event.getX(0);
                        mDownY = event.getY(0);
                        update();
                    }
                    else if (mTouchArea == TouchArea.water_mark)
                    {
                        if (mDoingAnim)
                        {
                            mTouchArea = TouchArea.none;
                            break;
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    if (event.getPointerCount() >= 2)
                    {
                        if (mTouchArea == TouchArea.image)
                        {
                            syncStatus(mOutsideMatrix);

                            float downX = (mPointer1DownX + mPointer2DownX) / 2f;
                            float downY = (mPointer1DownY + mPointer2DownY) / 2f;

                            float moveX = (event.getX(0) + event.getX(1)) / 2f;
                            float moveY = (event.getY(0) + event.getY(1)) / 2f;

                            float downSpace = ImageUtil.Spacing(mPointer1DownX - mPointer2DownX, mPointer1DownY - mPointer2DownY);

                            float moveSpace = ImageUtil.Spacing(event.getX(0) - event.getX(1), event.getY(0) - event.getY(1));

                            float scale = moveSpace / downSpace;

                            mOutsideMatrix.postScale(scale, scale, downX, downY);
                            compareImageScale(downX, downY, mOutsideMatrix);

                            float dx = moveX - downX;
                            float dy = moveY - downY;

                            mOutsideMatrix.postTranslate(dx, dy);
                            update();
                        }
                    }
                    else
                    {
                        if (mTouchArea == TouchArea.image)
                        {
                            float dx = event.getX(0) - mDownX;
                            float dy = event.getY(0) - mDownY;
                            syncStatus(mOutsideMatrix);
                            mOutsideMatrix.postTranslate(dx, dy);
                            update();
                        }
                        else if (!mCancelWaterMarkClickEvent && mTouchArea == TouchArea.water_mark)
                        {
                            float moveSpace = ImageUtil.Spacing(event.getX(0) - mDownX, event.getY(0) - mDownY);
                            mCancelWaterMarkClickEvent = moveSpace > PixelPercentUtil.WidthPxxToPercent(30);
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                {
                    if (mTouchArea == TouchArea.image)
                    {
                        doImageResetAnimation();
                    }
                    else if (!mCancelWaterMarkClickEvent && mTouchArea == TouchArea.water_mark)
                    {
                        mTouchArea = getTouchArea(event.getX(), event.getY());
                        if (mTouchArea == TouchArea.water_mark)
                        {
                            Log.d("xxx", "PreviewViewV2 --> onTouchEvent: 水印点击事件");
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_POINTER_DOWN:
                {
                    if (mTouchArea == TouchArea.image)
                    {
                        if (event.getPointerCount() <= 2)
                        {
                            mPointer1DownX = event.getX(0);
                            mPointer1DownY = event.getY(0);

                            mPointer2DownX = event.getX(1);
                            mPointer2DownY = event.getY(1);

                            if (mTouchArea == TouchArea.image)
                            {
                                setStatusRecord(mOutsideMatrix);
                            }
                        }
                    }
                    mCancelWaterMarkClickEvent = true;
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP:
                {
                    // 多指抬起时，event.getPointerCount() 依然是多指的数量，并没有减去 1
                    if (event.getPointerCount() - 1 >= 2)
                    {
                        int pointerDownCount = 0;
                        int actionIndex = event.getActionIndex(); // 获取是那个 手指index 抬起
                        for (int i = 0; i < event.getPointerCount(); i++)
                        {
                            if (i == actionIndex) continue;

                            if (pointerDownCount <= 0)
                            {
                                mPointer1DownX = event.getX(i);
                                mPointer1DownY = event.getY(i);
                            }
                            else
                            {
                                mPointer2DownX = event.getX(i);
                                mPointer2DownY = event.getY(i);
                            }
                            pointerDownCount += 1;
                            if (pointerDownCount >= 2) break;
                        }

                        if (mTouchArea == TouchArea.image)
                        {
                            setStatusRecord(mOutsideMatrix);
                        }
                    }
                    else
                    {
                        int pointerDownCount = 0;
                        int actionIndex = event.getActionIndex();
                        for (int i = 0; i < event.getPointerCount(); i++)
                        {
                            if (i == actionIndex) continue;

                            if (pointerDownCount == 0)
                            {
                                mDownX = event.getX(i);
                                mDownY = event.getY(i);
                            }
                            pointerDownCount += 1;
                            if (pointerDownCount >= 1) break;
                        }

                        if (mTouchArea == TouchArea.image)
                        {
                            setStatusRecord(mOutsideMatrix);
                        }
                    }
                    update();
                    break;
                }
            }
        }
        return !mEventLock;
    }

    protected void doImageResetAnimation()
    {
        if (!mDoingAnim && isBitmapValid(mImgBmp) && mTempMatrix != null && mConfig != null)
        {
            mixMatrix(mTempMatrix, mImgShape.mOwnMatrix, mOutsideMatrix, mImgShape.mExtraMatrix);

            RectF viewRect = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
            RectF orgImgRect = new RectF(0, 0, mImgBmp.getWidth(), mImgBmp.getHeight());
            RectF currentImgRect = new RectF();

            mTempMatrix.mapRect(currentImgRect, orgImgRect);

            PointF currentImgCenter = new PointF();
            currentImgCenter.x = currentImgRect.left + currentImgRect.width() / 2f;
            currentImgCenter.y = currentImgRect.top + currentImgRect.height() / 2f;

            float dx = 0;
            float dy = 0;
            /*
                判断当前图片通过缩放后，是否已经超过view 的宽高
             */
            if (currentImgRect.width() >= getMeasuredWidth())
            {
                if (currentImgRect.left >= viewRect.left)
                {
                    dx = viewRect.left - currentImgRect.left;
                }
                else if (currentImgRect.right <= viewRect.right)
                {
                    dx = viewRect.right - currentImgRect.right;
                }
            }
            else
            {
                dx = mConfig.mImageCenter.x - currentImgCenter.x;

                if (currentImgRect.left + dx < viewRect.left)
                {
                    dx = dx + viewRect.left - (currentImgRect.left + dx);
                }
                else if (currentImgRect.right + dx > viewRect.right)
                {
                    dx = dx + viewRect.right - (currentImgRect.right + dx);
                }
            }

            if (currentImgRect.height() >= getMeasuredHeight())
            {
                if (currentImgRect.top >= viewRect.top)
                {
                    dy = viewRect.top - currentImgRect.top;
                }
                else if (currentImgRect.bottom <= viewRect.bottom)
                {
                    dy = viewRect.bottom - currentImgRect.bottom;
                }
            }
            else
            {
                dy = mConfig.mImageCenter.y - currentImgCenter.y;

                if (currentImgRect.top + dy < viewRect.top)
                {
                    dy = dy + viewRect.top - (currentImgRect.top + dy);
                }
                else if (currentImgRect.bottom + dy > viewRect.bottom)
                {
                    dx = dy + viewRect.bottom - (currentImgRect.bottom + dy);
                }
            }

            Matrix temp = new Matrix();
            temp.set(mOutsideMatrix);
            mOutsideMatrix.postTranslate(dx, dy);

            mixMatrix(mTempMatrix, mImgShape.mOwnMatrix, mOutsideMatrix, mImgShape.mExtraMatrix);
            currentImgRect.setEmpty();
            mTempMatrix.mapRect(currentImgRect, orgImgRect);
            PointF finalImgCenter = new PointF();
            finalImgCenter.x = currentImgRect.left + currentImgRect.width() / 2f;
            finalImgCenter.y = currentImgRect.top + currentImgRect.height() / 2f;

            dx = finalImgCenter.x - currentImgCenter.x;
            dy = finalImgCenter.y - currentImgCenter.y;

            mOutsideMatrix.reset();
            mOutsideMatrix.set(temp);

            final float x = dx;
            final float y = dy;

            mTempMatrix.reset();
            mTempMatrix.set(mOutsideMatrix);

            mResetAnim = ValueAnimator.ofFloat(0, 1);
            SpringInterpolator springInterpolator = new SpringInterpolator();
            springInterpolator.setFactorSize(0.8f);
            mResetAnim.setInterpolator(springInterpolator);
            mResetAnim.addUpdateListener(animation -> {
                if (mDoingAnim)
                {
                    float value = (float) animation.getAnimatedValue();
                    float value_dx = x * value;
                    float value_dy = y * value;

                    mOutsideMatrix.reset();
                    mOutsideMatrix.set(mTempMatrix);
                    mOutsideMatrix.postTranslate(value_dx, value_dy);
                    update();
                }
            });
            mResetAnim.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mDoingAnim = true;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mDoingAnim = false;
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mDoingAnim = false;
                    mResetAnim.removeAllListeners();
                }
            });
            mResetAnim.setDuration(500);
            mResetAnim.start();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        initDefaultConfig();
        setImageMatrix(w, h, oldw, oldh);
    }

    private void setImageMatrix(int w, int h, int oldw, int oldh)
    {
        if (isBitmapValid(mImgBmp))
        {
            float scale = Math.min((float) w / mImgBmp.getWidth(), (float) h / mImgBmp.getHeight());
            float x = (float) mConfig.mImageCenter.x - mImgBmp.getWidth() * scale / 2f;
            float y = (float) mConfig.mImageCenter.y - mImgBmp.getHeight() * scale /2f;
            mImgShape.mOwnMatrix.reset();
            mImgShape.mOwnMatrix.postScale(scale, scale);
            mImgShape.mOwnMatrix.postTranslate(x, y);

            if (isBitmapValid(mWaterMarkBmp))
            {
                mWaterMarkShape.mOwnMatrix.reset();
                float s = Math.min(mImgBmp.getWidth() * scale, mImgBmp.getHeight() * scale);
                float dx = PhotoMark.getLogoRight(s);
                float dy = PhotoMark.getLogoBottom(s, false);
                float watermarkW = PhotoMark.getLogoW(s);
                float waterScale = watermarkW / mWaterMarkBmp.getWidth();
                mWaterMarkShape.mOwnMatrix.postScale(waterScale,waterScale);
                x = dx;
                y += mImgBmp.getHeight() * scale - dy - mWaterMarkBmp.getHeight() * waterScale;
                mWaterMarkShape.mOwnMatrix.postTranslate(x, y);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int layer = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

        canvas.drawColor(Color.BLACK);

        if (isBitmapValid(mImgBmp))
        {
            mixMatrix(mImgShape.mCurrentStateMatrix, mImgShape.mOwnMatrix, mOutsideMatrix, mImgShape.mExtraMatrix);
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            canvas.drawBitmap(mImgBmp, mImgShape.mCurrentStateMatrix, mPaint);

            if (isBitmapValid(mWaterMarkBmp))
            {
                mixMatrix(mWaterMarkShape.mCurrentStateMatrix, mWaterMarkShape.mOwnMatrix, mOutsideMatrix, mWaterMarkShape.mExtraMatrix);
                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                mPaint.setAntiAlias(true);
                mPaint.setFilterBitmap(true);
                canvas.drawBitmap(mWaterMarkBmp, mWaterMarkShape.mCurrentStateMatrix, mPaint);
            }
        }

        canvas.restoreToCount(layer);
    }

    /**
     *
     * @param dst
     * @param mixArr
     */
    private void mixMatrix(Matrix dst, Matrix...mixArr)
    {
        if (dst != null)
        {
            dst.reset();
            for (Matrix matrix : mixArr)
            {
                if (matrix != null)
                {
                    dst.postConcat(matrix);
                }
            }
        }
    }

    private boolean isBitmapValid(Bitmap bitmap)
    {
        return bitmap != null && !bitmap.isRecycled();
    }

    private RectF getInitImageRect()
    {
        if (isBitmapValid(mImgBmp) && mTempMatrix != null)
        {
            RectF out = new RectF();
            RectF src = new RectF(0, 0, mImgBmp.getWidth(), mImgBmp.getHeight());
            mixMatrix(mTempMatrix, mImgShape.mOwnMatrix);
            mTempMatrix.mapRect(out, src);

            return out;
        }

        return null;
    }
}
