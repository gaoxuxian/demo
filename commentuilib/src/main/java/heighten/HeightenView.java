package heighten;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.graphics.ColorUtils;
import android.view.View;

import util.PxUtil;

public class HeightenView extends View
{
    private Bitmap mBmp;

    private Rect mBmpRect;
    private Rect mDrawRect;

    private Paint mPaint;

    private int mScaleType = ScaleType.none;

    private boolean mInit;

    private int mIncreasedHeight;

    private boolean mShowHeightChanged;

    /**
     * 可视区域占比, 范围{0 - 1}, 动态改变view 宽高的时候，通过这个值算出的数有误差
     */
    private float mVisualAreaRatio;

    private float mInitVisualAreaRatio;

    private float mDissectionRatioTop;
    private float mDissectionRatioBottom;

    private @interface ScaleType
    {
        int none = 0;
        int equal_height = 1;
        int equal_width = 2;
    }

    private @interface Dissection
    {
        int top = 0;
        int middle = 1;
        int bottom = 2;
        int all = 3;
    }

    public HeightenView(Context context)
    {
        super(context);
        mInit = true;
        mBmpRect = new Rect();
        mDrawRect = new Rect();
        mPaint = new Paint();
        mVisualAreaRatio = 1f; // 默认占比 100%
        setDissectionRatio(0.1f, 0.9f);
    }

    public void setBitmap(Bitmap bitmap)
    {
        mBmp = bitmap;
    }

    public void setDissectionRatio(float top, float bottom)
    {
        mDissectionRatioTop = top;
        mDissectionRatioBottom = bottom;
    }

    /**
     * 调节高度前, 必须调用, 传true，调节高度结束后，传false
     * @param show 是否显示高度调节过程
     */
    public void showHeightChanged(boolean show)
    {
        mShowHeightChanged = show;
    }

    public void setIncreasedHeight(int increased)
    {
        mIncreasedHeight = increased;

        int height = (int) (getMeasuredHeight() * mVisualAreaRatio);

        if (height + mIncreasedHeight >= getMeasuredHeight())
        {
            mIncreasedHeight = getMeasuredHeight() - height;
        }

        mVisualAreaRatio = (float) (height + mIncreasedHeight) / (float) getMeasuredHeight();
    }

    public int getCurrentAdjustableHeight()
    {
        if (mBmp == null || mBmp.isRecycled()) return -1;

        int top = (int) (mBmp.getHeight() * mDissectionRatioTop);
        int bottom = (int) (mBmp.getHeight() * mDissectionRatioBottom);
        return bottom - top;
    }

    public void update()
    {
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mInit || mScaleType == ScaleType.none)
        {
            mInit = false;
            initBmpData(w, h);
        }
    }

    private void initBmpData(int w, float h)
    {
        if (mBmp == null || mBmp.isRecycled()) return;

        float scaleX = ((float) w) / mBmp.getWidth();
        float scaleY = h / mBmp.getHeight();
        float scale = Math.min(scaleX, scaleY);

        if (scale == scaleY)
        {
            // 等高缩放
            mScaleType = ScaleType.equal_height;
            mVisualAreaRatio = 0.8f; // 等高缩放的时候, 先将可视区域缩小 20%
            mInitVisualAreaRatio = mVisualAreaRatio;
        }
        else if (scale == scaleX)
        {
            // 等宽缩放
            mScaleType = ScaleType.equal_width;

            int width = (int) (w * 0.8f); // 等宽缩放的时候, 先将可视区域缩小 20%
            float bmpScale = (float) mBmp.getHeight() / (float) mBmp.getWidth();
            int height = (int) (width * bmpScale);
            mVisualAreaRatio = (float) height / getMeasuredHeight();
            mInitVisualAreaRatio = mVisualAreaRatio;
        }
    }

    private Rect countBmpRect(Bitmap bmp, float dissectionRatioTop, float dissectionRatioBottom, @Dissection int dissectionType)
    {
        if (bmp == null || bmp.isRecycled()) return null;

        Rect out = new Rect();
        int top = (int) (bmp.getHeight() * dissectionRatioTop);
        int bottom = (int) (bmp.getHeight() * dissectionRatioBottom);
        switch (dissectionType)
        {
            case Dissection.top:
            {
                out.set(0, 0, bmp.getWidth(), top);
                break;
            }

            case Dissection.middle:
            {
                out.set(0, top, bmp.getWidth(), bottom);
                break;
            }

            case Dissection.bottom:
            {
                out.set(0, bottom, bmp.getWidth(), bmp.getHeight());
                break;
            }

            default:
            {
                out.set(0, 0, bmp.getWidth(), bmp.getHeight());
                break;
            }
        }

        return out;
    }

    private Rect countDrawRect(int previewW, int previewH, Bitmap bmp, float dissectionRatioTop, float dissectionRatioBottom, @Dissection int dissectionType)
    {
        if (bmp == null || bmp.isRecycled()) return null;

        Rect out = new Rect();
        switch (dissectionType)
        {
            case Dissection.top:
            {
                float topBmpHWScale = (float) bmp.getHeight() * dissectionRatioTop / (float) bmp.getWidth();
                int top = (int) (previewW * topBmpHWScale);
                out.set(0, 0, previewW, top);
                break;
            }

            case Dissection.middle:
            {
                float topBmpHWScale = (float) bmp.getHeight() * dissectionRatioTop / (float) bmp.getWidth();
                int top = (int) (previewW * topBmpHWScale);

                float botBmpHWScale = (float) bmp.getHeight() * (1f - dissectionRatioBottom) / (float) bmp.getWidth();
                int bot = (int) (previewW * botBmpHWScale);
                out.set(0, top, previewW, previewH - bot);
                break;
            }

            case Dissection.bottom:
            {
                float botBmpHWScale = (float) bmp.getHeight() * (1f - dissectionRatioBottom) / (float) bmp.getWidth();
                int bot = (int) (previewW * botBmpHWScale);
                out.set(0, previewH - bot, previewW, previewH);
                break;
            }

            default:
            {
                out.set(0, 0, previewW, previewH);
                break;
            }
        }
        return out;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int height = (int) (getMeasuredHeight() * mVisualAreaRatio);
        int initHeight = (int) (getMeasuredHeight() * mInitVisualAreaRatio);
        float bmpScale = (float) mBmp.getWidth() / (float) mBmp.getHeight();
        int width = (int) (initHeight * bmpScale);

        float x = (getMeasuredWidth() - width)/2f;
        float y = (getMeasuredHeight() - height)/2f;
        canvas.save();
        canvas.translate(x,y);

        mPaint.reset();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        if (mShowHeightChanged)
        {
            // 显示调节过程
            mDrawRect = countDrawRect(width, height, mBmp, mDissectionRatioTop, mDissectionRatioBottom, Dissection.top);
            mBmpRect = countBmpRect(mBmp, mDissectionRatioTop, mDissectionRatioBottom, Dissection.top);
            canvas.drawBitmap(mBmp, mBmpRect, mDrawRect, mPaint);

            mDrawRect = countDrawRect(width, height, mBmp, mDissectionRatioTop, mDissectionRatioBottom, Dissection.middle);
            mBmpRect = countBmpRect(mBmp, mDissectionRatioTop, mDissectionRatioBottom, Dissection.middle);
            canvas.drawBitmap(mBmp, mBmpRect, mDrawRect, mPaint);

            mDrawRect = countDrawRect(width, height, mBmp, mDissectionRatioTop, mDissectionRatioBottom, Dissection.bottom);
            mBmpRect = countBmpRect(mBmp, mDissectionRatioTop, mDissectionRatioBottom, Dissection.bottom);
            canvas.drawBitmap(mBmp, mBmpRect, mDrawRect, mPaint);
        }
        else
        {
            // 不显示调节过程，比例不重要，只需要画图片即可
            mDrawRect = countDrawRect(width, height, mBmp, 0, 0, Dissection.all);
            mBmpRect = countBmpRect(mBmp, 0, 0, Dissection.all);
            canvas.drawBitmap(mBmp, mBmpRect, mDrawRect, mPaint);
        }

        mPaint.setColor(ColorUtils.setAlphaComponent(Color.RED, (int) (255 * 0.6f)));
        mDrawRect = countDrawRect(width, height, mBmp, mDissectionRatioTop, mDissectionRatioBottom, Dissection.middle);
        canvas.drawLine(0, mDrawRect.top, width, mDrawRect.top, mPaint);
        canvas.drawLine(0, mDrawRect.bottom, width, mDrawRect.bottom, mPaint);

        mPaint.setColor(ColorUtils.setAlphaComponent(Color.GREEN, 255));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(PxUtil.sU_1080p(20));
        canvas.drawPoint(width, mDrawRect.top, mPaint);
        canvas.drawPoint(width, mDrawRect.bottom, mPaint);

        canvas.restore();
    }
}
