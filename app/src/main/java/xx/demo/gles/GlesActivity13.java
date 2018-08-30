package xx.demo.gles;

import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import egl.Gles20BackEnv;
import filter.EGLFilter;
import util.ThreadUtil;
import xx.demo.R;
import xx.demo.activity.BaseActivity;

public class GlesActivity13 extends BaseActivity
{
    private ImageView mItemView;

    private boolean mInit;

    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mInit = true;
        mItemView = new ImageView(parent.getContext());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(mItemView, params);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mInit)
        {
            mInit = false;
            ThreadUtil.runOnUiThreadDelay(new Runnable()
            {
                @Override
                public void run()
                {
                    Gles20BackEnv env = new Gles20BackEnv();
                    env.setFilter(new EGLFilter(getResources()));
                    env.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.opengl_test_5));
                    mItemView.setImageBitmap(env.getOutputBitmap());
                }
            }, 1000);
        }
    }
}
