package xx.demo.gles;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import gles.Gles12View;
import xx.demo.activity.BaseActivity;

public class GlesActivity12 extends BaseActivity
{
    Gles12View mItemView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mItemView = new Gles12View(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mItemView, params);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mItemView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mItemView.onPause();
    }
}
