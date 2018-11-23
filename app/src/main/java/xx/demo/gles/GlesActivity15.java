package xx.demo.gles;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import gles.GLes15View;
import xx.demo.activity.BaseActivity;

public class GlesActivity15 extends BaseActivity
{

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        GLes15View view = new GLes15View(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(view, params);
    }
}
