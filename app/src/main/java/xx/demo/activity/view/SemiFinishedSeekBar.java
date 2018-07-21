package xx.demo.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public abstract class SemiFinishedSeekBar<T extends IConfig> extends View
{
    private boolean mCanTriggerTouchEvent;
    private boolean mEventLock;
    protected T mConfig;

    protected Paint mPaint;
    protected Matrix mMatrix;

    public SemiFinishedSeekBar(Context context)
    {
        super(context);
        mPaint = new Paint();
        mMatrix = new Matrix();
        onInitBaseData();
        mCanTriggerTouchEvent = true;
        setEventLock(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mEventLock) return true;

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                mCanTriggerTouchEvent = true;
                oddDown(event);
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (mCanTriggerTouchEvent)
                {
                    oddMove(event);
                }
                break;
            }

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            {
                if (mCanTriggerTouchEvent)
                {
                    oddUp(event);
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
            {
                mCanTriggerTouchEvent = false;
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        onDrawToCanvas(canvas);
    }

    public void setConfig(T config)
    {
        mConfig = config;
    }

    public void setEventLock(boolean lock)
    {
        mEventLock = lock;
    }

    public void update()
    {
        invalidate();
    }

    protected abstract void onInitBaseData();

    protected abstract void oddDown(MotionEvent event);

    protected abstract void oddMove(MotionEvent event);

    protected abstract void oddUp(MotionEvent event);

    protected abstract void onDrawToCanvas(Canvas canvas);

    public abstract void onClear();
}
