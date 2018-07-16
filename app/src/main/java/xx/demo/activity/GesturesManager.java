package xx.demo.activity;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import lib.util.PixelPercentUtil;

public class GesturesManager extends GestureDetector.SimpleOnGestureListener
{
    private GestureDetector mDetector;
    private float mVelocityX;
    private float mVelocityY;
    private int mMinMoveInterceptSize;

    private float mDownX;
    private float mDownY;

    private GesturesListener mGesturesListener;
    private BasicDataListener mDataListener;

    public void setGesturesListener(GesturesListener listener)
    {
        this.mGesturesListener = listener;
    }

    public void setBaseDataListener(BasicDataListener listener)
    {
        this.mDataListener = listener;
    }

    public interface BasicDataListener
    {
        int getLeftToRightMinSize();

        int getRightToLeftMinSize();

        int getTopToBottomMinSize();

        int getBottomToTopMinSize();
    }

    public interface GesturesListener
    {
        void onMove(float distance, int moveType);

        void onUp(int upType, int moveType);
    }

    private int mMoveType = MoveType.TYPE_NONE;

    public @interface MoveType
    {
        int TYPE_NONE = 0;
        int TYPE_LEFT_TO_RIGHT = 1;
        int TYPE_RIGHT_TO_LEFT = 2;
        int TYPE_TOP_TO_BOTTOM = 3;
        int TYPE_BOTTOM_TO_TOP = 4;
    }

    public @interface UpType
    {
        int TYPE_STATIC_LIFT = 0;
        int TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE = 1; // 与 move 类型 一致的
        int TYPE_FLING_OPPOSITE_OF_MOVE_TYPE = 2; // 与 move 类型 相反的
    }

    public GesturesManager(Context context)
    {
        mDetector = new GestureDetector(context, this);
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

        if (mDetector != null)
        {
            mDetector.onTouchEvent(ev);
        }

        switch (ev.getAction() & ev.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                mDownX = ev.getX();
                mDownY = ev.getY();
                mVelocityX = 0;
                mVelocityY = 0;
                mMoveType = MoveType.TYPE_NONE;
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                float diff = 0;
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
                            }
                            else if ((ev.getX() - mDownX) < 0)
                            {
                                mMoveType = MoveType.TYPE_RIGHT_TO_LEFT;
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
                            }
                        }
                    }
                }

                switch (mMoveType)
                {
                    case MoveType.TYPE_RIGHT_TO_LEFT:
                    case MoveType.TYPE_LEFT_TO_RIGHT:
                    {
                        diff = ev.getX() - mDownX;
                        break;
                    }

                    case MoveType.TYPE_TOP_TO_BOTTOM:
                    case MoveType.TYPE_BOTTOM_TO_TOP:
                    {
                        diff = ev.getY() - mDownY;
                        break;
                    }
                }

                if (mGesturesListener != null)
                {
                    mGesturesListener.onMove(diff, mMoveType);
                }
                break;
            }

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            {
                float diffX = Math.abs(ev.getX() - mDownX);
                float diffY = Math.abs(ev.getY() - mDownY);

                int minSize = 0;
                int type = UpType.TYPE_STATIC_LIFT;
                switch (mMoveType)
                {
                    case MoveType.TYPE_LEFT_TO_RIGHT:
                    {
                        if (mDataListener != null)
                        {
                            minSize = mDataListener.getLeftToRightMinSize();
                        }

                        if (mVelocityX > 0)
                        {
                            type = UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE;
                        }
                        else if (mVelocityX < 0)
                        {
                            type = UpType.TYPE_FLING_OPPOSITE_OF_MOVE_TYPE;
                        }
                        else if (diffX < minSize)
                        {
                            type = UpType.TYPE_FLING_OPPOSITE_OF_MOVE_TYPE;
                        }
                        else if (diffX >= minSize)
                        {
                            type = UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE;
                        }
                        break;
                    }

                    case MoveType.TYPE_RIGHT_TO_LEFT:
                    {
                        if (mDataListener != null)
                        {
                            minSize = mDataListener.getRightToLeftMinSize();
                        }

                        if (mVelocityX > 0)
                        {
                            type = UpType.TYPE_FLING_OPPOSITE_OF_MOVE_TYPE;
                        }
                        else if (mVelocityX < 0)
                        {
                            type = UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE;
                        }
                        else if (diffX >= minSize)
                        {
                            type = UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE;
                        }
                        else if (diffX < minSize)
                        {
                            type = UpType.TYPE_FLING_OPPOSITE_OF_MOVE_TYPE;
                        }
                        break;
                    }

                    case MoveType.TYPE_TOP_TO_BOTTOM:
                    {
                        if (mDataListener != null)
                        {
                            minSize = mDataListener.getTopToBottomMinSize();
                        }

                        if (mVelocityY > 0)
                        {
                            type = UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE;
                        }
                        else if (mVelocityY < 0)
                        {
                            type = UpType.TYPE_FLING_OPPOSITE_OF_MOVE_TYPE;
                        }
                        else if (diffY < minSize)
                        {
                            type = UpType.TYPE_FLING_OPPOSITE_OF_MOVE_TYPE;
                        }
                        else if (diffY >= minSize)
                        {
                            type = UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE;
                        }
                        break;
                    }

                    case MoveType.TYPE_BOTTOM_TO_TOP:
                    {
                        if (mDataListener != null)
                        {
                            minSize = mDataListener.getBottomToTopMinSize();
                        }

                        if (mVelocityY < 0)
                        {
                            type = UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE;
                        }
                        else if (mVelocityY > 0)
                        {
                            type = UpType.TYPE_FLING_OPPOSITE_OF_MOVE_TYPE;
                        }
                        else if (diffY < minSize)
                        {
                            type = UpType.TYPE_FLING_OPPOSITE_OF_MOVE_TYPE;
                        }
                        else if (diffY >= minSize)
                        {
                            type = UpType.TYPE_FLING_CONSISTENT_WITH_MOVE_TYPE;
                        }
                        break;
                    }
                }

                if (mGesturesListener != null)
                {
                    mGesturesListener.onUp(type, mMoveType);
                }
                break;
            }
        }
        return out;
    }
}