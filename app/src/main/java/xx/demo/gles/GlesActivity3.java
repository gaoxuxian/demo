package xx.demo.gles;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import gles.Gles3View;
import xx.demo.activity.BaseActivity;

public class GlesActivity3 extends BaseActivity
{
    private Gles3View view;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        view = new Gles3View(parent.getContext());
//        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params = new FrameLayout.LayoutParams(400, 1000);
        parent.addView(view, params);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        view.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        view.onResume();
    }
}
