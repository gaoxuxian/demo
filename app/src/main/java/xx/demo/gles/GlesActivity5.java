package xx.demo.gles;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import gles.Gles5View;
import xx.demo.activity.BaseActivity;

public class GlesActivity5 extends BaseActivity
{
    private Gles5View mView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mView = new Gles5View(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mView, params);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mView.onResume();
    }
}
