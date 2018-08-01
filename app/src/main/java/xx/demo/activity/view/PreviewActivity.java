package xx.demo.activity.view;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Random;

import xx.demo.R;
import xx.demo.activity.BaseActivity;

public class PreviewActivity extends BaseActivity
{
    private PreviewViewV2 mView;
    private boolean mHasMoveX;
    private boolean mHasMove;
    private float mPageEventDownX;
    private float mPageEventDownY;
    private boolean mCanInterceptTouchEvent;
    private boolean mInterceptTouchEvent;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        FrameLayout layout = new FrameLayout(parent.getContext())
        {
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev)
            {
                switch (ev.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        mHasMoveX = false;
                        mCanInterceptTouchEvent = true;
                        mInterceptTouchEvent = false;
                        mPageEventDownX = ev.getX();
                        mPageEventDownY = ev.getY();
                        break;
                    }
                }
                return super.dispatchTouchEvent(ev);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev)
            {
                float x = ev.getX();
                float y = ev.getY();

                switch (ev.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        float dy = Math.abs(y - mPageEventDownY);
                        float dx = Math.abs(x - mPageEventDownX);

                        if (mCanInterceptTouchEvent)
                        {
                            if (!mHasMove)
                            {
                                if (dx > dy)
                                {
                                    mHasMove = true;
                                    mHasMoveX = true;
                                    mCanInterceptTouchEvent = false;
                                }
                                else if (dy > dx)
                                {
                                    mHasMove = true;
                                    mHasMoveX = false;
                                    mInterceptTouchEvent = true;
                                }
                            }
                        }
                        break;
                    }
                }
                return mInterceptTouchEvent || super.onInterceptTouchEvent(ev);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event)
            {
                switch (event.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        break;
                    }

                    case MotionEvent.ACTION_UP:
                    {
                        if (mView != null)
                        {
                            mView.setTranslationY(300);
                        }
                        break;
                    }
                }
                return true;
            }
        };
        layout.setBackgroundColor(Color.RED);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(layout, params);
        {
            mView = new PreviewViewV2(parent.getContext());
            mView.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.heighten_test_bmp));
            mView.setWaterMark(BitmapFactory.decodeResource(getResources(), R.drawable.ic_watermark_big_3));
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.addView(mView, params);
        }

        Button button = new Button(parent.getContext());
        button.setText("上升");
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mView.setWaterMarkTranslationY(300);
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        parent.addView(button, params);

        button = new Button(parent.getContext());
        button.setText("下降");
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mView.setWaterMarkTranslationY(0);
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        parent.addView(button, params);

        button = new Button(parent.getContext());
        button.setText("换水印");
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Random random = new Random(System.nanoTime());
                int i = random.nextInt(7) + 1;
                int resId = getResources().getIdentifier("ic_watermark_big_"+i, "drawable", getPackageName());
                mView.setWaterMark(BitmapFactory.decodeResource(getResources(), resId));
                mView.update();
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        parent.addView(button, params);
    }
}
