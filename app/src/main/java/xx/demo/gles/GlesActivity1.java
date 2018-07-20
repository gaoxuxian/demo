package xx.demo.gles;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import xx.demo.activity.BaseActivity;

public class GlesActivity1 extends BaseActivity
{
    private GlesView mGlView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mGlView = new GlesView(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mGlView, params);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mGlView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mGlView.onResume();
    }
}
