package xx.demo.gles;

import android.content.res.Configuration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import gles.Gles8View;
import xx.demo.activity.BaseActivity;

public class GlesActivity8 extends BaseActivity
{
    Gles8View mItemView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mItemView = new Gles8View(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mItemView, params);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mItemView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mItemView.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
}
