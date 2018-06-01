package xx.demo.activity;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import lib.util.PixelPercentUtil;

public class GesturesManager extends GestureDetector.SimpleOnGestureListener
{
    private GestureDetector mDetector;
    private float mVelocityX;
    private float mVelocityY;
    private int mTouchSlop;
    private int mMinMoveInterceptSize;

    private float mDownX;
    private float mDownY;

    private int mShowType = ShowType.TYPE_NONE;

    public void setBasicDataListener(BasicDataListener listener)
    {
        this.mDataListener = listener;
    }

    @interface ShowType
    {
        int TYPE_NONE = 0;
        int TYPE_SHOW_LEFT = 1;
        int TYPE_SHOW_RIGHT = 2;
        int TYPE_SHOW_TOP = 3;
        int TYPE_SHOW_BOTTOM = 4;
    }

    private GesturesListener mListener;
    private BasicDataListener mDataListener;

    public void setGesturesListener(GesturesListener listener)
    {
        this.mListener = listener;
    }

    public interface BasicDataListener
    {
        int getLeftWidth();

        int getRightWidth();

        int getBottomHeight();

        int getTopHeight();
    }

    public interface GesturesListener
    {
        void onInitLeft();

        void onInitRight();

        void onInitBottom();

        void onTransLeft(float transX, boolean show, boolean hide);

        void onTransRight(float transX, boolean show, boolean hide);
    }

    private int mMoveType = MoveType.TYPE_NONE;

    @interface MoveType
    {
        int TYPE_NONE = 0;
        int TYPE_LEFT_TO_RIGHT = 1;
        int TYPE_RIGHT_TO_LEFT = 2;
        int TYPE_TOP_TO_BOTTOM = 3;
        int TYPE_BOTTOM_TO_TOP = 4;
    }

    public GesturesManager(Context context)
    {
        mDetector = new GestureDetector(context, this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMinMoveInterceptSize = PixelPercentUtil.WidthPxxToPercent(30);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        mVelocityX = velocityX;
        mVelocityY = velocityY;
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    public boolean onTouchEvent(MotionEvent ev)
    {
        boolean out = false;

        switch (ev.getAction() & ev.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                mDownX = ev.getX();
                mDownY = ev.getY();
                mMoveType = MoveType.TYPE_NONE;
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (mMoveType == MoveType.TYPE_NONE)
                {
                    float diffX = Math.abs(ev.getX() - mDownX);
                    float diffY = Math.abs(ev.getY() - mDownY);

                    if (diffX > mMinMoveInterceptSize || diffY > mMinMoveInterceptSize)
                    {
                        if (diffX > diffY)
                        {
                            if ((ev.getX() - mDownX) > 0)
                            {
                                mMoveType = MoveType.TYPE_LEFT_TO_RIGHT;

                                if (mShowType == ShowType.TYPE_NONE && mListener != null)
                                {
                                    mListener.onInitLeft();
                                }
                            }
                            else if ((ev.getX() - mDownX) < 0)
                            {
                                mMoveType = MoveType.TYPE_RIGHT_TO_LEFT;

                                if (mShowType == ShowType.TYPE_NONE && mListener != null)
                                {
                                    mListener.onInitRight();
                                }
                            }
                        }
                        else
                        {
                            if ((ev.getY() - mDownY) > 0)
                            {
                                mMoveType = MoveType.TYPE_TOP_TO_BOTTOM;
                            }
                            else if ((ev.getY() - mDownY) < 0)
                            {
                                mMoveType = MoveType.TYPE_BOTTOM_TO_TOP;

                                if (mShowType == ShowType.TYPE_NONE && mListener != null)
                                {
                                    mListener.onInitBottom();
                                }
                            }
                        }
                    }
                }

                float diffX = ev.getX() - mDownX;
                float diffY = ev.getY() - mDownY;

                switch (mMoveType)
                {
                    case MoveType.TYPE_LEFT_TO_RIGHT:
                    {
                        if (mListener != null && mDataListener != null)
                        {
                            if (mShowType == ShowType.TYPE_NONE)
                            {
                                int leftWidth = mDataListener.getLeftWidth();
                                float transX = -leftWidth + diffX;
                                if (transX < -leftWidth)
                                {
                                    transX = -leftWidth;
                                }
                                else if (transX > 0)
                                {
                                    transX = 0;
                                }
                                mListener.onTransLeft(transX, false, false);
                            }
                            else if (mShowType == ShowType.TYPE_SHOW_RIGHT)
                            {
                                mListener.onTransRight(diffX, false, false);
                            }
                        }
                        break;
                    }

                    case MoveType.TYPE_RIGHT_TO_LEFT:
                    {
                        if (mListener != null)
                        {
                            if (mShowType == ShowType.TYPE_NONE)
                            {
                                mListener.onTransRight(diffX,false, false);
                            }
                            else if (mShowType == ShowType.TYPE_SHOW_LEFT)
                            {
                                int leftWidth = mDataListener.getLeftWidth();
                                float transX = diffX;
                                if (transX < -leftWidth)
                                {
                                    transX = -leftWidth;
                                }
                                else if (transX > 0)
                                {
                                    transX = 0;
                                }
                                mListener.onTransLeft(transX,false, false);
                            }
                        }
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                float diffX = ev.getX() - mDownX;
                float diffY = ev.getY() - mDownY;

                switch (mShowType)
                {
                    case ShowType.TYPE_NONE:
                    {
                        switch (mMoveType)
                        {
                            case MoveType.TYPE_LEFT_TO_RIGHT:
                            {
                                if (mListener != null)
                                {
                                    int leftWidth = mDataListener.getLeftWidth();
                                    float transX = -leftWidth + diffX;
                                    if (transX < -leftWidth)
                                    {
                                        transX = -leftWidth;
                                    }
                                    else if (transX > 0)
                                    {
                                        transX = 0;
                                    }
                                    mListener.onTransLeft(transX, true, false);
                                    mShowType = ShowType.TYPE_SHOW_LEFT;
                                }
                                break;
                            }

                            case MoveType.TYPE_RIGHT_TO_LEFT:
                            {

                                break;
                            }

                            case MoveType.TYPE_TOP_TO_BOTTOM:
                            {

                                break;
                            }

                            case MoveType.TYPE_BOTTOM_TO_TOP:
                            {

                                break;
                            }
                        }
                        break;
                    }

                    case ShowType.TYPE_SHOW_LEFT:
                    {
                        if (mListener != null)
                        {
                            int leftWidth = mDataListener.getLeftWidth();
                            float transX = diffX;
                            if (transX < -leftWidth)
                            {
                                transX = -leftWidth;
                            }
                            else if (transX > 0)
                            {
                                transX = 0;
                            }
                            mListener.onTransLeft(transX, false, true);
                            mShowType = ShowType.TYPE_NONE;
                        }
                        break;
                    }

                    case ShowType.TYPE_SHOW_RIGHT:
                    {
                        break;
                    }

                    case ShowType.TYPE_SHOW_TOP:
                    {
                        break;
                    }

                    case ShowType.TYPE_SHOW_BOTTOM:
                    {
                        break;
                    }
                }
                break;
            }
        }

        return out;
    }
}
