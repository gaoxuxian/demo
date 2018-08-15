package xx.demo.gles;

import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import gles.Gles10View;
import lib.util.ThreadUtil;
import xx.demo.R;
import xx.demo.activity.BaseActivity;

public class GlesActivity10 extends BaseActivity
{
    Gles10View mItemView;

    Runnable mRunnable;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mItemView = new Gles10View(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mItemView, params);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mItemView.onResume();

        if (mRunnable == null)
        {
            mRunnable = () -> {
                mItemView.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.opengl_test_5));
                mItemView.setWaterBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_watermark_big_10));
                mItemView.requestRender();
            };

            ThreadUtil.runOnUiThreadDelay(mRunnable, 1000);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mItemView.onPause();
    }
}
