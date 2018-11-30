package xx.demo.gles;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import gles.GLes16View;
import xx.demo.activity.BaseActivity;

public class GlesActivity16 extends BaseActivity
{

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        GLes16View view = new GLes16View(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(view, params);
    }

    @Override
    public void onCreateInitData()
    {

    }
}
