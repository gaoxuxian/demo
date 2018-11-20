package xx.demo.gles;

import android.widget.FrameLayout;

import gles.GLes14View;
import util.GLUtil;
import util.PxUtil;
import xx.demo.activity.BaseActivity;

public class GlesActivity14 extends BaseActivity
{
    GLes14View mItemView;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mItemView = new GLes14View(parent.getContext());
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sV_1080p(1920));
        parent.addView(mItemView, params);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mItemView != null)
        {
            mItemView.postDelayed(() -> mItemView.requestRender(), 3000);
        }
    }
}
