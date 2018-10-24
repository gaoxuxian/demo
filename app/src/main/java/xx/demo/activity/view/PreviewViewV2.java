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
import android.view.ViewParent;

import util.ImageUtil;
import util.PxUtil;

public class PreviewViewV2 extends View
{
    private ValueAnimator mKickBackAnim;
    private ValueAnimator mDoubleClickAnim;

    private interface TouchArea
    {
        int none = 0;
        int image = 1;
        int water_mark = 2;
    }

    private Matrix mOutsideMatrix;

    private Matrix mStatusRecordMatrix;

    private Matrix mTempMatrix; // 用于计算

    private Matrix mAnimationMatrix; // 用于做动画

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
    private float mMaxScale = 5f;

    private boolean mDoingAnimation;

    private boolean mCancelWaterMarkClickEvent;
    private long mDoubleClickFirstTime;
    private boolean mHasMoveEvent;

    private boolean mHasDetectEventIntercept;

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
        mAnimationMatrix = new Matrix();
        mStatusRecordMatrix = new Matrix();
        mPaint = new Paint();

        mKickBackAnim = new ValueAnimator();
        mDoubleClickAnim = new ValueAnimator();
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
            mConfig.mImageCenter.x = getMeasuredWidth() / 2;
            mConfig.mImageCenter.y = getMeasuredHeight() / 2;
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
            return;
        }

        if (isBitmapValid(mImgBmp) && isBitmapValid(mWaterMarkBmp))
        {
            float scale = Math.min((float) getMeasuredWidth() / mImgBmp.getWidth(), (float) getMeasuredHeight() / mImgBmp.getHeight());
            float y = (float) mConfig.mImageCenter.y - mImgBmp.getHeight() * scale / 2f;

            mWaterMarkShape.mOwnMatrix.reset();
            float s = Math.min(mImgBmp.getWidth() * scale, mImgBmp.getHeight() * scale);
            float dx = PhotoMark.getLogoRight(s);
            float dy = PhotoMark.getLogoBottom(s, false);
            float watermarkW = PhotoMark.getLogoW(s);
            float waterScale = watermarkW / mWaterMarkBmp.getWidth();
            mWaterMarkShape.mOwnMatrix.postScale(waterScale, waterScale);
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
            if (dst.contains(x, y))
            {
                return TouchArea.water_mark;
            }
        }

        if (isBitmapValid(mImgBmp))
        {
            RectF imgRect = new RectF(0, 0, mImgBmp.getWidth(), mImgBmp.getHeight());
            RectF dst = new RectF();
            mImgShape.mCurrentStateMatrix.mapRect(dst, imgRect);
            if (dst.contains(x, y))
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
            if (temp != null)
            {
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
    }

    private void detectParentCanNotInterceptEvent(float dx, float dy, int touchArea)
    {
        if ((dx == 0 && dy == 0) || mHasDetectEventIntercept) return;

        int xDirection = 0;
        int yDirection = 0;

        if (dx > dy)
        {
            xDirection = dx > 0 ? 1 : dx < 0 ? -1 : 0;
        }
        else
        {
            yDirection = dy > 0 ? 1 : dy < 0 ? -1 : 0;
        }

        if (touchArea == TouchArea.none)
        {
            ViewParent parent = getParent();
            if (parent != null)
            {
                parent.requestDisallowInterceptTouchEvent(false);
            }
        }
        else if (touchArea == TouchArea.image)
        {
            mixMatrix(mTempMatrix, mImgShape.mOwnMatrix, mOutsideMatrix, mImgShape.mExtraMatrix);

            if (mTempMatrix != null)
            {
                RectF viewRect = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
                RectF orgImgRect = new RectF(0, 0, mImgBmp.getWidth(), mImgBmp.getHeight());
                RectF currentImgRect = new RectF();

                mTempMatrix.mapRect(currentImgRect, orgImgRect);

                int currentImgW = Math.round(currentImgRect.width());
                int currentImgH = Math.round(currentImgRect.height());

                boolean hasDeal = false;

                // 左 -- 右
                if (xDirection > 0)
                {
                    ViewParent parent = getParent();
                    if (parent != null)
                    {
                        if (currentImgW <= viewRect.width())
                        {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                        else
                        {
                            parent.requestDisallowInterceptTouchEvent(Math.round(currentImgRect.left) < viewRect.left);
                        }
                        hasDeal = true;
                    }
                }
                else if (xDirection < 0) // 右 -- 左
                {
                    ViewParent parent = getParent();
                    if (parent != null)
                    {
                        if (currentImgW <= viewRect.width())
                        {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                        else
                        {
                            parent.requestDisallowInterceptTouchEvent(Math.round(currentImgRect.right) > viewRect.right);
                        }
                        hasDeal = true;
                    }
                }

                if (!hasDeal)
                {
                    // 上 -- 下
                    if (yDirection > 0)
                    {
                        ViewParent parent = getParent();
                        if (parent != null)
                        {
                            if (currentImgH <= viewRect.height())
                            {
                                parent.requestDisallowInterceptTouchEvent(false);
                            }
                            else
                            {
                                parent.requestDisallowInterceptTouchEvent(Math.round(currentImgRect.top) < viewRect.top);
                            }
                            hasDeal = true;
                        }
                    }
                    else if (yDirection < 0) // 下 -- 上
                    {
                        ViewParent parent = getParent();
                        if (parent != null)
                        {
                            if (currentImgH <= viewRect.height())
                            {
                                parent.requestDisallowInterceptTouchEvent(false);
                            }
                            else
                            {
                                parent.requestDisallowInterceptTouchEvent(Math.round(currentImgRect.bottom) > viewRect.bottom);
                            }
                            hasDeal = true;
                        }
                    }
                }

                mHasDetectEventIntercept = hasDeal;
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
                    ViewParent parent = getParent();
                    if (parent != null)
                    {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mCancelWaterMarkClickEvent = false;
                    mHasDetectEventIntercept = false;
                    mHasMoveEvent = false;
                    mDownX = event.getX(0);
                    mDownY = event.getY(0);
                    mTouchArea = getTouchArea(event.getX(), event.getY());
                    if (mTouchArea == TouchArea.image)
                    {
                        cancelImageAnimation();
                        setStatusRecord(mOutsideMatrix);
                        update();
                    }
                    else if (mTouchArea == TouchArea.water_mark)
                    {
                        if (mDoingAnimation)
                        {
                            mTouchArea = TouchArea.none;
                            break;
                        }

                        if (parent != null)
                        {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                        mHasDetectEventIntercept = true;
                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    float moveSpace = ImageUtil.Spacing(event.getX(0) - mDownX, event.getY(0) - mDownY);
                    mHasMoveEvent = moveSpace > PxUtil.sWidthPxIn1080p(30);

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

                            moveSpace = ImageUtil.Spacing(event.getX(0) - event.getX(1), event.getY(0) - event.getY(1));

                            float dx = moveX - downX;
                            float dy = moveY - downY;

                            float scale = moveSpace / downSpace;

                            mOutsideMatrix.postScale(scale, scale, downX, downY);
                            compareImageScale(downX, downY, mOutsideMatrix);

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
                            detectParentCanNotInterceptEvent(dx, dy, TouchArea.image);
                            syncStatus(mOutsideMatrix);
                            mOutsideMatrix.postTranslate(dx, dy);
                            update();
                        }
                        else if (!mCancelWaterMarkClickEvent && mTouchArea == TouchArea.water_mark)
                        {
                            moveSpace = ImageUtil.Spacing(event.getX(0) - mDownX, event.getY(0) - mDownY);
                            mCancelWaterMarkClickEvent = moveSpace > PxUtil.sWidthPxIn1080p(30);
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_CANCEL:
                {
                    if (mTouchArea == TouchArea.image)
                    {
                        if (!mHasMoveEvent)
                        {
                            if (System.currentTimeMillis() - mDoubleClickFirstTime <= 1000)
                            {
                                mDoubleClickFirstTime = 0;
                                cancelImageAnimation();
                                doDoubleClickAnimation(event.getX(), event.getY());
                            }
                            else
                            {
                                doImageKickBackAnimation();
                                mDoubleClickFirstTime = System.currentTimeMillis();
                            }
                            break;
                        }
                        else
                        {
                            mDoubleClickFirstTime = 0;
                        }

                        doImageKickBackAnimation();
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:
                {
                    if (mTouchArea == TouchArea.image)
                    {
                        if (!mHasMoveEvent)
                        {
                            if (System.currentTimeMillis() - mDoubleClickFirstTime <= 1000)
                            {
                                mDoubleClickFirstTime = 0;
                                cancelImageAnimation();
                                doDoubleClickAnimation(event.getX(), event.getY());
                            }
                            else
                            {
                                doImageKickBackAnimation();
                                mDoubleClickFirstTime = System.currentTimeMillis();
                            }
                            break;
                        }
                        else
                        {
                            mDoubleClickFirstTime = 0;
                        }

                        doImageKickBackAnimation();
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
                        ViewParent parent = getParent();
                        if (parent != null)
                        {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                        mHasDetectEventIntercept = true;

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
                    mHasMoveEvent = true;
                    mDoubleClickFirstTime = 0;
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

    protected void doDoubleClickAnimation(float x, float y)
    {
        if (isBitmapValid(mImgBmp))
        {
            mixMatrix(mTempMatrix, mImgShape.mOwnMatrix, mOutsideMatrix, mImgShape.mExtraMatrix);

            RectF viewRect = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
            RectF orgImgRect = new RectF(0, 0, mImgBmp.getWidth(), mImgBmp.getHeight());
            RectF currentImgRect = new RectF();

            mTempMatrix.mapRect(currentImgRect, orgImgRect);

            RectF temp = getInitImageRect();
            if (temp != null)
            {
                float dstScale = 1f;
                float dx = 0;
                float dy = 0;

                float scale = currentImgRect.width() / temp.width();

                if (scale < mMaxScale)
                {
                    // 如果当前还没到最大倍数，就放大当前倍数(基于原始大小)的1倍
                    dstScale = 2f;

                    if (dstScale * scale >= mMaxScale)
                    {
                        dstScale = mMaxScale / scale;
                    }

                    Matrix tempM = new Matrix();
                    tempM.set(mOutsideMatrix);
                    tempM.postScale(dstScale, dstScale, x, y);

                    mixMatrix(mTempMatrix, mImgShape.mOwnMatrix, tempM, mImgShape.mExtraMatrix);
                    mTempMatrix.mapRect(currentImgRect, orgImgRect);

                    PointF imgCenter = new PointF();
                    imgCenter.x = currentImgRect.left + currentImgRect.width() / 2f;
                    imgCenter.y = currentImgRect.top + currentImgRect.height() / 2f;

                    /*
                        判断当前图片通过缩放后，是否已经超过view 的宽高
                    */
                    if (currentImgRect.width() >= getMeasuredWidth())
                    {
                        dx = mConfig.mImageCenter.x - x;

                        if (currentImgRect.left + dx >= viewRect.left)
                        {
                            dx = viewRect.left - currentImgRect.left;
                        }
                        else if (currentImgRect.right + dx <= viewRect.right)
                        {
                            dx = viewRect.right - currentImgRect.right;
                        }
                    }
                    else
                    {
                        dx = mConfig.mImageCenter.x - imgCenter.x;

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
                        dy = mConfig.mImageCenter.y - y;

                        if (currentImgRect.top + dy >= viewRect.top)
                        {
                            dy = viewRect.top - currentImgRect.top;
                        }
                        else if (currentImgRect.bottom + dy <= viewRect.bottom)
                        {
                            dy = viewRect.bottom - currentImgRect.bottom;
                        }
                    }
                    else
                    {
                        dy = mConfig.mImageCenter.y - imgCenter.y;

                        if (currentImgRect.top + dy < viewRect.top)
                        {
                            dy = dy + viewRect.top - (currentImgRect.top + dy);
                        }
                        else if (currentImgRect.bottom + dy > viewRect.bottom)
                        {
                            dx = dy + viewRect.bottom - (currentImgRect.bottom + dy);
                        }
                    }
                }
                else if (scale >= mMaxScale)
                {
                    // 直接恢复原始大小
                    dstScale = mMinScale / scale;
                    Matrix tempM = new Matrix();
                    tempM.set(mOutsideMatrix);
                    tempM.postScale(dstScale, dstScale, x, y);

                    mixMatrix(mTempMatrix, mImgShape.mOwnMatrix, tempM, mImgShape.mExtraMatrix);
                    mTempMatrix.mapRect(currentImgRect, orgImgRect);

                    dx = mConfig.mImageCenter.x - (currentImgRect.left + currentImgRect.width() / 2f);
                    dy = mConfig.mImageCenter.y - (currentImgRect.top + currentImgRect.height() / 2f);
                }

                mAnimationMatrix.reset();
                mAnimationMatrix.set(mOutsideMatrix);
                mDoubleClickAnim.setFloatValues(0f, 1f);
                float finalDstScale = dstScale - 1f;
                float finalDy = dy;
                float finalDx = dx;
                mDoubleClickAnim.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    mOutsideMatrix.reset();
                    mOutsideMatrix.set(mAnimationMatrix);
                    mOutsideMatrix.postScale(1f + finalDstScale * value, 1f + finalDstScale * value, x, y);
                    mOutsideMatrix.postTranslate(finalDx * value, finalDy * value);
                    invalidate();
                });
                mDoubleClickAnim.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        mDoingAnimation = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        mDoingAnimation = false;
                        mDoubleClickAnim.removeAllUpdateListeners();
                        mDoubleClickAnim.removeAllListeners();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {
                        mDoingAnimation = false;
                        mDoubleClickAnim.removeAllUpdateListeners();
                        mDoubleClickAnim.removeAllListeners();
                    }
                });
                mDoubleClickAnim.setDuration(300);
                mDoubleClickAnim.start();
            }
        }
    }

    protected void doImageKickBackAnimation()
    {
        if (!mDoingAnimation && isBitmapValid(mImgBmp) && mTempMatrix != null && mConfig != null)
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

            final float x = dx;
            final float y = dy;

            mAnimationMatrix.reset();
            mAnimationMatrix.set(mOutsideMatrix);

            mKickBackAnim.setFloatValues(0f, 1f);
            SpringInterpolator springInterpolator = new SpringInterpolator();
            springInterpolator.setFactorSize(0.8f);
            mKickBackAnim.setInterpolator(springInterpolator);
            mKickBackAnim.addUpdateListener(animation -> {
                if (mDoingAnimation)
                {
                    float value = (float) animation.getAnimatedValue();
                    float value_dx = x * value;
                    float value_dy = y * value;

                    mOutsideMatrix.reset();
                    mOutsideMatrix.set(mAnimationMatrix);
                    mOutsideMatrix.postTranslate(value_dx, value_dy);
                    update();
                }
            });
            mKickBackAnim.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mDoingAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mDoingAnimation = false;
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mDoingAnimation = false;
                    mKickBackAnim.removeAllListeners();
                }
            });
            mKickBackAnim.setDuration(500);
            mKickBackAnim.start();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        initDefaultConfig();
        setImageInitMatrix(w, h, oldw, oldh);
    }

    private void setImageInitMatrix(int w, int h, int oldw, int oldh)
    {
        if (mInit && isBitmapValid(mImgBmp))
        {
            mInit = false;
            float scale = Math.min((float) w / mImgBmp.getWidth(), (float) h / mImgBmp.getHeight());
            float x = (float) mConfig.mImageCenter.x - mImgBmp.getWidth() * scale / 2f;
            float y = (float) mConfig.mImageCenter.y - mImgBmp.getHeight() * scale / 2f;
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
                mWaterMarkShape.mOwnMatrix.postScale(waterScale, waterScale);
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
     * @param dst
     * @param mixArr
     */
    private void mixMatrix(Matrix dst, Matrix... mixArr)
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

    private void cancelImageAnimation()
    {
        if (mDoingAnimation)
        {
            if (mKickBackAnim != null && (mKickBackAnim.isStarted() || mKickBackAnim.isRunning()))
            {
                mKickBackAnim.cancel();
            }

            if (mDoubleClickAnim != null && (mDoubleClickAnim.isRunning() || mDoubleClickAnim.isStarted()))
            {
                mDoubleClickAnim.cancel();
            }
        }
    }
}
