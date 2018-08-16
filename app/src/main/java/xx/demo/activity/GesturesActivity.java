package xx.demo.activity;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import util.ShareData;

public class GesturesActivity extends BaseActivity
{
    private int mShowType = ShowType.TYPE_NONE;

    @interface ShowType
    {
        int TYPE_NONE = 0;
        int TYPE_SHOW_LEFT = 1;
        int TYPE_SHOW_RIGHT = 2;
        int TYPE_SHOW_TOP = 3;
        int TYPE_SHOW_BOTTOM = 4;
    }

    private View mLeftView;
    private View mBottomView;
    private GesturesManager mGesturesManager;
    private FrameLayout layout;

    @Override
    public void onCreateInitData()
    {
        mGesturesManager = new GesturesManager(this);
        mGesturesManager.setBaseDataListener(new GesturesManager.BasicDataListener()
        {
            @Override
            public int getLeftToRightMinSize()
            {
                return ShareData.m_screenRealWidth / 3;
            }

            @Override
            public int getRightToLeftMinSize()
            {
                return ShareData.m_screenRealWidth / 3;
            }

            @Override
            public int getTopToBottomMinSize()
            {
                return ShareData.m_screenRealHeight / 3;
            }

            @Override
            public int getBottomToTopMinSize()
            {
                return ShareData.m_screenRealHeight / 3;
            }
        });
        mGesturesManager.setGesturesListener(new GesturesManager.GesturesListener()
        {
            @Override
            public void onMove(float distance, int moveType)
            {
                switch (moveType)
                {
                    case GesturesManager.MoveType.TYPE_LEFT_TO_RIGHT:
                    {
                        if (mShowType == ShowType.TYPE_NONE)
                        {
                            initLeft();
                            mShowType = ShowType.TYPE_SHOW_LEFT;
                        }

                        if (mShowType == ShowType.TYPE_SHOW_LEFT)
                        {
                            if (mLeftView != null)
                            {
                                float x = -ShareData.m_screenRealWidth + distance;
                                if (x > 0)
                                {
                                    x = 0;
                                }
                                else if (x < -ShareData.m_screenRealWidth)
                                {
                                    x = -ShareData.m_screenRealWidth;
                                }
                                mLeftView.setTranslationX(x);
                            }
                        }
                        else if (mShowType == ShowType.TYPE_SHOW_RIGHT)
                        {
                            // TODO: 2018/6/2 滑动右边的view
                        }
                        break;
                    }

                    case GesturesManager.MoveType.TYPE_RIGHT_TO_LEFT:
                    {
                        if (mShowType == ShowType.TYPE_NONE)
                        {
                            // TODO: 2018/6/2 滑动右边view
                        }
                        else if (mShowType == ShowType.TYPE_SHOW_LEFT)
                        {
                            if (mLeftView != null)
                            {
                                float x = distance;
                                if (x > 0)
                                {
                                    x = 0;
                                }
                                else if (x < -ShareData.m_screenRealWidth)
                                {
                                    x = -ShareData.m_screenRealWidth;
                                }
                                mLeftView.setTranslationX(x);
                            }
                        }
                        break;
                    }

                    case GesturesManager.MoveType.TYPE_TOP_TO_BOTTOM:
                    {
                        if (mShowType == ShowType.TYPE_NONE)
                        {

                        }

                        if (mShowType == ShowType.TYPE_SHOW_BOTTOM)
                        {
                            if (mBottomView != null)
                            {
                                float y = distance;
                                if (y < 0)
                                {
                                    y = 0;
                                }
                                else if (y > ShareData.m_screenRealHeight)
                                {
                                    y = ShareData.m_screenRealHeight;
                                }
                                mBottomView.setTranslationY(y);
                            }
                        }
                        break;
                    }

                    case GesturesManager.MoveType.TYPE_BOTTOM_TO_TOP:
                    {
                        if (mShowType == ShowType.TYPE_NONE)
                        {
                            initBottom();
                            mShowType = ShowType.TYPE_SHOW_BOTTOM;
                        }

                        if (mShowType == ShowType.TYPE_SHOW_BOTTOM)
                        {
                            if (mBottomView != null)
                            {
                                float y = ShareData.m_screenRealHeight + distance;
                                if (y < 0)
                                {
                                    y = 0;
                                }
                                else if (y > ShareData.m_screenRealHeight)
                                {
                                    y = ShareData.m_screenRealHeight;
                                }
                                mBottomView.setTranslationY(y);
                            }
                        }
                        else if (mShowType == ShowType.TYPE_SHOW_TOP)
                        {
                            // TODO: 2018/6/2 滑动top view
                        }
                        break;
                    }
                }
            }

            @Override
            public void onUp(int upType, int moveType)
            {
                switch (moveType)
                {
                    case GesturesManager.MoveType.TYPE_LEFT_TO_RIGHT:
                    {
                        if (upType == GesturesManager.UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE)
                        {
                            if (mShowType == ShowType.TYPE_SHOW_LEFT)
                            {
                                if (mLeftView != null)
                                {
                                    ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, "translationX", mLeftView.getTranslationX(), 0);
                                    animator.setDuration(300);
                                    animator.start();
                                }
                            }
                            else if (mShowType == ShowType.TYPE_SHOW_RIGHT)
                            {
                                // TODO: 2018/6/2 关右边view 动画
                            }
                        }
                        else
                        {
                            if (mShowType == ShowType.TYPE_SHOW_LEFT)
                            {
                                if (mLeftView != null)
                                {
                                    ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, "translationX", mLeftView.getTranslationX(), -ShareData.m_screenRealWidth);
                                    animator.setDuration(300);
                                    animator.start();
                                    mShowType = ShowType.TYPE_NONE;
                                }
                            }
                            else if (mShowType == ShowType.TYPE_SHOW_RIGHT)
                            {

                            }
                        }
                        break;
                    }

                    case GesturesManager.MoveType.TYPE_RIGHT_TO_LEFT:
                    {
                        if (upType == GesturesManager.UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE)
                        {
                            if (mShowType == ShowType.TYPE_SHOW_RIGHT)
                            {
                                // TODO: 2018/6/2 滑动右边view 动画
                            }
                            else if (mShowType == ShowType.TYPE_SHOW_LEFT)
                            {
                                if (mLeftView != null)
                                {
                                    ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, "translationX", mLeftView.getTranslationX(), -ShareData.m_screenRealWidth);
                                    animator.setDuration(300);
                                    animator.start();
                                    mShowType = ShowType.TYPE_NONE;
                                }
                            }
                        }
                        else
                        {
                            if (mShowType == ShowType.TYPE_SHOW_LEFT)
                            {
                                if (mLeftView != null)
                                {
                                    ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, "translationX", mLeftView.getTranslationX(), 0);
                                    animator.setDuration(300);
                                    animator.start();
                                    mShowType = ShowType.TYPE_SHOW_LEFT;
                                }
                            }
                            else if (mShowType == ShowType.TYPE_SHOW_RIGHT)
                            {
                                // TODO: 2018/6/2 显示右边view 的动画
                            }
                        }
                        break;
                    }

                    case GesturesManager.MoveType.TYPE_TOP_TO_BOTTOM:
                    {
                        if (upType == GesturesManager.UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE)
                        {
                            if (mShowType == ShowType.TYPE_SHOW_TOP)
                            {
                                // TODO: 2018/6/2 显示top view 动画
                            }
                            else if (mShowType == ShowType.TYPE_SHOW_BOTTOM)
                            {
                                if (mBottomView != null)
                                {
                                    ObjectAnimator animator = ObjectAnimator.ofFloat(mBottomView, "translationY", mBottomView.getTranslationY(), ShareData.m_screenRealHeight);
                                    animator.setDuration(300);
                                    animator.start();
                                    mShowType = ShowType.TYPE_NONE;
                                }
                            }
                        }
                        else
                        {
                            if (mShowType == ShowType.TYPE_SHOW_TOP)
                            {
                                // TODO: 2018/6/2 关闭top view 动画
                            }
                            else if (mShowType == ShowType.TYPE_SHOW_BOTTOM)
                            {
                                if (mBottomView != null)
                                {
                                    ObjectAnimator animator = ObjectAnimator.ofFloat(mBottomView, "translationY", mBottomView.getTranslationY(), 0);
                                    animator.setDuration(300);
                                    animator.start();
                                }
                            }
                        }
                        break;
                    }

                    case GesturesManager.MoveType.TYPE_BOTTOM_TO_TOP:
                    {
                        if (upType == GesturesManager.UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE)
                        {
                            if (mShowType == ShowType.TYPE_SHOW_BOTTOM)
                            {
                                if (mBottomView != null)
                                {
                                    ObjectAnimator animator = ObjectAnimator.ofFloat(mBottomView, "translationY", mBottomView.getTranslationY(), 0);
                                    animator.setDuration(300);
                                    animator.start();
                                }
                            }
                            else if (mShowType == ShowType.TYPE_SHOW_TOP)
                            {
                                // TODO: 2018/6/2 关闭 top view 动画
                            }
                        }
                        else
                        {
                            if (mShowType == ShowType.TYPE_SHOW_BOTTOM)
                            {
                                if (mBottomView != null)
                                {
                                    ObjectAnimator animator = ObjectAnimator.ofFloat(mBottomView, "translationY", mBottomView.getTranslationY(), ShareData.m_screenRealHeight);
                                    animator.setDuration(300);
                                    animator.start();
                                    mShowType = ShowType.TYPE_NONE;
                                }
                            }
                            else if (mShowType == ShowType.TYPE_SHOW_TOP)
                            {
                                // TODO: 2018/6/2 显示 top view 动画
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    private void initLeft()
    {
        if (mLeftView == null)
        {
            mLeftView = new View(layout.getContext());
            mLeftView.setBackgroundColor(Color.RED);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addView(mLeftView, params);
        }
    }

    private void initBottom()
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