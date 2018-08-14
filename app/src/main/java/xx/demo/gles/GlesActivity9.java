package xx.demo.gles;

import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import gles.Gles9View;
import lib.util.ThreadUtil;
import xx.demo.activity.BaseActivity;

public class GlesActivity9 extends BaseActivity implements View.OnClickListener
{
    private int mBmpType;
    private boolean mPauseShuffing;

    Gles9View mItemView;
    private Button button;

    Runnable mBmpShufflingRunnable;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mItemView = new Gles9View(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mItemView, params);

        button = new Button(parent.getContext());
        button.setOnClickListener(this);
        button.setText("换图");
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(button, params);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mBmpShufflingRunnable == null)
        {
            mBmpShufflingRunnable = () -> {
                String name = "opengl_test_" + mBmpType;
                mBmpType++;
                if (mBmpType > 6)
                {
                    mBmpType = 0;
                }
                int resId = getResources().getIdentifier(name, "drawable", getPackageName());
                mItemView.setBitmap(BitmapFactory.decodeResource(getResources(), resId));

//                if (!mPauseShuffing)
//                {
//                    ThreadUtil.runOnUiThreadDelay(mBmpShufflingRunnable, 1000);
//                }
            };
            ThreadUtil.runOnUiThreadDelay(mBmpShufflingRunnable, 1000);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == button)
        {
//            mPauseShuffing = !mPauseShuffing;
//            if (mPauseShuffing)
//            {
//                ThreadUtil.cancelRunOnUiThread(mBmpShufflingRunnable);
//            }
//            else
//            {
//                ThreadUtil.runOnUiThreadDelay(mBmpShufflingRunnable, 1000);
//            }
//            Toast.makeText(this, mPauseShuffing ? "暂停轮播" : "开始轮播", Toast.LENGTH_SHORT).show();
            String name = "opengl_test_" + mBmpType;
            mBmpType++;
            if (mBmpType > 6)
            {
                mBmpType = 0;
            }
            int resId = getResources().getIdentifier(name, "drawable", getPackageName());
            mItemView.setBitmap(BitmapFactory.decodeResource(getResources(), resId));
        }
    }
}
