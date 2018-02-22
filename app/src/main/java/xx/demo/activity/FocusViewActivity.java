package xx.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import xx.demo.util.ShareData;
import xx.demo.view.FocusView;

public class FocusViewActivity extends Activity
{
    FrameLayout parent;
    FocusView mFocusView;
    private GestureListener mGestureListener;
    private GestureDetector mGestureDetector;
    private boolean mTriggerLongPress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initGesture(this);

        parent = new FrameLayout(this);
        parent.setBackgroundColor(Color.BLUE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.setLayoutParams(params);
        setContentView(parent);

        mFocusView = new FocusView(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(mFocusView, params);
    }

    private void initGesture(Context context)
    {
        mGestureListener = new GestureListener();
        mGestureDetector = new GestureDetector(context, mGestureListener);
        mGestureDetector.setIsLongpressEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP && mTriggerLongPress)
        {
            if (mFocusView != null)
            {
                mFocusView.setVisibility(View.VISIBLE);
                mFocusView.doFingerUpAnim();
                return false;
            }
        }
        return mGestureDetector != null ? mGestureDetector.onTouchEvent(event) : super.onTouchEvent(event);
    }

    /**
     * 手势操作监听
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            mTriggerLongPress = false;
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            if (!mTriggerLongPress && mFocusView != null)
            {
                mFocusView.setCircleXY(e.getX(), e.getY());
                mFocusView.showFocus(true);
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            mTriggerLongPress = true;
            if (mFocusView != null)
            {
                mFocusView.setCircleXY(e.getX(), e.getY());
                mFocusView.doLongPressAnim();
            }
        }
    }
}
