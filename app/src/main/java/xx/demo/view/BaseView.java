package xx.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

import xx.demo.util.CameraPercentUtil;

/**
 * 快门
 * Created by Gxx on 2018/1/12.
 */

public abstract class BaseView<T extends BaseConfig> extends View
{
    protected int mViewW;
    protected int mViewH;

    protected int mDefWH;

    protected Paint mPaint;

    private BaseView(Context context)
    {
        super(context);
    }

    public BaseView(Context context, int def_wh)
    {
        this(context);

        mDefWH = def_wh > 0 ? def_wh : CameraPercentUtil.WidthPxToPercent(150);

        mPaint = new Paint();

        setWillNotDraw(true);
    }

    public abstract void setConfig(T config);

    protected abstract void drawToCanvas(Canvas canvas);

    protected void startToDraw()
    {
        setWillNotDraw(false);
        invalidate();
    }

    public void ReLayout(int w, int h)
    {
        if (mViewW != w || mViewH != h)
        {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = w;
            params.height = h;
            requestLayout();
        }
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
        int result = size;

        if (mode == MeasureSpec.AT_MOST)
        {
            result = mDefWH > size ? size : mDefWH;
        }

        return result;
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
        super.onDraw(canvas);

        drawToCanvas(canvas);
    }
}