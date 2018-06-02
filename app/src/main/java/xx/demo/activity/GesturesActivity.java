package xx.demo.activity;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import lib.util.ShareData;

public class GesturesActivity extends BaseActivity
{
    private View mLeftView;
    private View mBottomView;
    private GesturesManager mGesturesManager;
    private FrameLayout layout;

    @Override
    public void onCreateInitData()
    {
        mGesturesManager = new GesturesManager(this);
        mGesturesManager.setGesturesListener(new GesturesManager.GesturesListener()
        {
            @Override
            public void onInitLeft()
            {
                if (mLeftView == null)
                {
                    mLeftView = new View(layout.getContext());
                    mLeftView.setBackgroundColor(Color.RED);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layout.addView(mLeftView, params);
                }
            }

            @Override
            public void onInitRight()
            {

            }

            @Override
            public void onInitBottom()
            {
                if (mBottomView == null)
                {
                    mBottomView = new View(layout.getContext());
                    mBottomView.setBackgroundColor(Color.GREEN);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layout.addView(mBottomView, params);
                }
            }

            @Override
            public void onTransLeft(float transX, boolean show, boolean hide)
            {
                if (mLeftView != null)
                {
                    mLeftView.setTranslationX(transX);
                }

                if (show)
                {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, "translationX", mLeftView.getTranslationX(), 0);
                    animator.setDuration(300);
                    animator.start();
                }

                if (hide)
                {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, "translationX", mLeftView.getTranslationX(), -ShareData.m_screenRealWidth);
                    animator.setDuration(300);
                    animator.start();
                }
            }

            @Override
            public void onTransRight(float transX, boolean show, boolean hide)
            {

            }
        });

        mGesturesManager.setBasicDataListener(new GesturesManager.BasicDataListener()
        {
            @Override
            public int getLeftWidth()
            {
                return ShareData.m_screenRealWidth;
            }

            @Override
            public int getRightWidth()
            {
                return 0;
            }

            @Override
            public int getBottomHeight()
            {
                return 0;
            }

            @Override
            public int getTopHeight()
            {
                return 0;
            }
        });
    }

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        layout = new FrameLayout(parent.getContext())
        {
            @Override
            public boolean onTouchEvent(MotionEvent event)
            {
                mGesturesManager.onTouchEvent(event);
                return true;
            }
        };
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(layout, params);
    }

}
